//per questioni di tempistiche questa pagina di testing è stata generata dall'AI
//tuttavia avevo già provato con postman e funziona, questa è stato solo una "ngulia"

package it.unical.cenetta;

import it.unical.cenetta.dto.CreateTaskRequest;
import it.unical.cenetta.dto.TaskDto;
import it.unical.cenetta.model.Event;
import it.unical.cenetta.model.TaskStatus;
import it.unical.cenetta.model.User;
import it.unical.cenetta.repository.EventRepository;
import it.unical.cenetta.repository.UserRepository;
import it.unical.cenetta.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifica il requisito non funzionale di gestione della concorrenza.
 *
 * Scenario: due utenti tentano di prendere in carico (claim) la STESSA task
 * nello stesso istante. Grazie al locking ottimistico (@Version su Task),
 * esattamente uno deve riuscire; l'altro deve fallire in modo sicuro con
 * un'eccezione, senza corrompere lo stato del database.
 */
@SpringBootTest
class OptimisticLockingTest {

    @Autowired private TaskService taskService;
    @Autowired private UserRepository userRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private PasswordEncoder encoder;
    private Long taskVersion;

    private User luigi;
    private User peach;
    private Long taskId;

    @BeforeEach
    void setUp() {
        // Username univoci: il DB in memoria persiste tra i test della stessa run
        long n = System.nanoTime();
        User mario = userRepository.save(new User("mario" + n, encoder.encode("x"), "Mario"));
        luigi = userRepository.save(new User("luigi" + n, encoder.encode("x"), "Luigi"));
        peach = userRepository.save(new User("peach" + n, encoder.encode("x"), "Peach"));

        Event event = new Event("Test", "desc",
        LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1),
        mario,                              // organizer (5°)
        "CODE" + (n % 100000),              // inviteCode (6°)
        encoder.encode("pw"));              // passwordHash (7°)
        event.getParticipants().add(luigi);
        event.getParticipants().add(peach);
        eventRepository.save(event);

        // L'organizzatore crea una task: nasce FREE
        TaskDto task = taskService.create(
                new CreateTaskRequest("Portare le uova", null), event.getId(), mario);
        taskId = task.id();
        taskVersion = task.version();
        assertEquals(TaskStatus.FREE, task.status());
    }

    @Test
    void soloUnUtenteRiesceAPrendereInCaricoLaStessaTask() throws Exception {

        // Un "cancello di partenza": entrambi i thread aspettano qui, così
        // partono davvero insieme e massimizzano la probabilita' di conflitto.
        CountDownLatch start = new CountDownLatch(1);

        ExecutorService pool = Executors.newFixedThreadPool(2);
        AtomicInteger successi = new AtomicInteger(0);
        AtomicInteger conflitti = new AtomicInteger(0);

        Callable<Void> tentaClaim = () -> {
            // sceglie l'utente in base al thread
            User utente = Thread.currentThread().getName().endsWith("1") ? luigi : peach;
            start.await();                    // aspetta l'apertura del cancello
            try {
                
                taskService.claim(taskId, taskVersion, utente);
                successi.incrementAndGet();    // ha fatto commit
            } catch (Exception ex) {
                conflitti.incrementAndGet();   // fallito in modo sicuro
            }
            return null;
        };

        Future<Void> f1 = pool.submit(tentaClaim);
        Future<Void> f2 = pool.submit(tentaClaim);

        start.countDown();                    // via! entrambi partono insieme

        f1.get(10, TimeUnit.SECONDS);
        f2.get(10, TimeUnit.SECONDS);
        pool.shutdown();

        // Il cuore del test: uno passa, uno fallisce.
        assertEquals(1, successi.get(),
                "Esattamente una transazione deve fare commit");
        assertEquals(1, conflitti.get(),
                "L'altra deve fallire in modo sicuro (conflitto di concorrenza)");
    }
}
