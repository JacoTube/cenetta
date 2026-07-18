package it.unical.cenetta.config;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unical.cenetta.model.Event;
import it.unical.cenetta.model.Task;
import it.unical.cenetta.model.TaskStatus;
import it.unical.cenetta.model.User;
import it.unical.cenetta.repository.EventRepository;
import it.unical.cenetta.repository.TaskRepository;
import it.unical.cenetta.repository.UserRepository;

/**
 * Popola il database in memoria con dati dimostrativi all'avvio.
 *
 * Credenziali: mario / luigi / sinutaro  — password: password123
 *
 * Eventi:
 *   CARBO123 (pw: risiko) — organizzato da Mario, aperto
 *   PIZZA456 (pw: pizza)  — organizzato da Luigi, aperto
 *   PASTA789 (pw: pasta)  — organizzato da Mario, GIA' SCADUTO (per la demo del 423)
 *
 * Nota: usa direttamente i repository anziche' i service, cosi' da poter
 * impostare stati e date arbitrari (comprese deadline nel passato) senza
 * passare per le validazioni applicative.
 */
@Service
public class SeedService {

    private final UserRepository uRepo;
    private final EventRepository eRepo;
    private final TaskRepository tRepo;
    private final PasswordEncoder encoder;

    public SeedService(UserRepository uRepo, EventRepository eRepo,
                       TaskRepository tRepo, PasswordEncoder encoder) {
        this.uRepo = uRepo;
        this.eRepo = eRepo;
        this.tRepo = tRepo;
        this.encoder = encoder;
    }

    /**
     * @Transactional: la sessione resta aperta per tutto il metodo, cosi' gli
     * accessi alle collezioni lazy (partecipanti, task) non falliscono.
     * Funziona perche' questo e' un metodo di un bean, quindi la chiamata passa
     * dal proxy Spring.
     */
    @Transactional
    public void testing() {

        // Evita di duplicare i dati se il metodo venisse invocato due volte
        if (uRepo.count() > 0) {
            System.out.println(">>> Database gia' popolato, seed saltato.");
            return;
        }

        // ------------------------------------------------------------------
        // Utenti
        // ------------------------------------------------------------------
        User mario    = uRepo.save(new User("mario",    encoder.encode("password123"), "Mario Mario"));
        User luigi    = uRepo.save(new User("luigi",    encoder.encode("password123"), "Luigi Mario"));
        User sinutaro = uRepo.save(new User("sinutaro", encoder.encode("password123"), "Sinutaro Sinu"));

        // ------------------------------------------------------------------
        // Evento 1 — "Carbonara e Risiko", organizzato da Mario
        // ------------------------------------------------------------------
        Event carbonara = new Event(
                "Carbonara e Risiko",
                "Cena a casa di Mario, poi partita di Risiko fino a tarda notte.",
                LocalDateTime.now().plusDays(8),      // quando si tiene
                LocalDateTime.now().plusDays(7),      // deadline: un giorno prima
                mario,
                "CARBO123",
                encoder.encode("risiko"));
        carbonara.getParticipants().add(luigi);
        carbonara.getParticipants().add(sinutaro);
        eRepo.save(carbonara);

        // Task in stati diversi, per coprire tutta la macchina a stati
        Task uova = new Task("Portare le uova", "Almeno 6, fresche",
                TaskStatus.FREE, carbonara, mario);
        carbonara.addTask(uova);

        Task guanciale = new Task("Portare il guanciale", "300g",
                TaskStatus.FREE, carbonara, mario);
        carbonara.addTask(guanciale);

        Task risiko = new Task("Portare il Risiko", null,
                TaskStatus.ASSIGNED, carbonara, mario);
        risiko.assignUser(luigi);                     // gia' presa in carico
        carbonara.addTask(risiko);

        Task pecorino = new Task("Portare il pecorino", "Romano, non grana",
                TaskStatus.COMPLETED, carbonara, mario);
        pecorino.assignUser(sinutaro);                // presa e gia' completata
        carbonara.addTask(pecorino);

        Task vino = new Task("Portare il vino", "Un rosso corposo",
                TaskStatus.PENDING_APPROVAL, carbonara, luigi);   // proposta da un invitato
        carbonara.addTask(vino);

        Task karaoke = new Task("Portare il karaoke", "Per dopo cena",
                TaskStatus.REJECTED, carbonara, sinutaro);        // proposta rifiutata
        carbonara.addTask(karaoke);

        tRepo.save(uova);
        tRepo.save(guanciale);
        tRepo.save(risiko);
        tRepo.save(pecorino);
        tRepo.save(vino);
        tRepo.save(karaoke);

        // ------------------------------------------------------------------
        // Evento 2 — "Pizzata di fine sessione", organizzato da Luigi
        // ------------------------------------------------------------------
        Event pizzata = new Event(
                "Pizzata di fine sessione",
                "Si festeggia la fine degli esami. Pizza fatta in casa.",
                LocalDateTime.now().plusDays(15),
                LocalDateTime.now().plusDays(13),
                luigi,
                "PIZZA456",
                encoder.encode("pizza"));
        pizzata.getParticipants().add(mario);
        pizzata.getParticipants().add(sinutaro);
        eRepo.save(pizzata);

        Task impasto = new Task("Preparare l'impasto", "Lievitazione 24h",
                TaskStatus.ASSIGNED, pizzata, luigi);
        impasto.assignUser(luigi);
        pizzata.addTask(impasto);

        Task mozzarella = new Task("Comprare la mozzarella", "Fiordilatte, 1kg",
                TaskStatus.FREE, pizzata, luigi);
        pizzata.addTask(mozzarella);

        Task forno = new Task("Portare la pietra refrattaria", null,
                TaskStatus.FREE, pizzata, luigi);
        pizzata.addTask(forno);

        Task birra = new Task("Portare le birre", "Almeno 12",
                TaskStatus.PENDING_APPROVAL, pizzata, sinutaro);
        pizzata.addTask(birra);

        tRepo.save(impasto);
        tRepo.save(mozzarella);
        tRepo.save(forno);
        tRepo.save(birra);

        // ------------------------------------------------------------------
        // Evento 3 — GIA' SCADUTO: serve a dimostrare il congelamento passivo
        // ------------------------------------------------------------------
        Event scaduto = new Event(
                "Pasta alla Norma (archiviato)",
                "Evento passato: la checklist e' in sola lettura.",
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(3),     // deadline nel PASSATO
                mario,
                "PASTA789",
                encoder.encode("pasta"));
        scaduto.getParticipants().add(luigi);
        scaduto.getParticipants().add(sinutaro);
        eRepo.save(scaduto);

        Task melanzane = new Task("Portare le melanzane", null,
                TaskStatus.COMPLETED, scaduto, mario);
        melanzane.assignUser(luigi);
        scaduto.addTask(melanzane);

        Task ricotta = new Task("Portare la ricotta salata", "Rimasta non assegnata",
                TaskStatus.FREE, scaduto, mario);     // resta libera per sempre
        scaduto.addTask(ricotta);

        tRepo.save(melanzane);
        tRepo.save(ricotta);

        // ------------------------------------------------------------------
        // Riepilogo a console
        // ------------------------------------------------------------------
        System.out.println();
        System.out.println("=========================================================");
        System.out.println(" DATI DEMO CARICATI");
        System.out.println("---------------------------------------------------------");
        System.out.println(" Utenti (password: password123):");
        System.out.println("   mario     - Mario Rossi");
        System.out.println("   luigi     - Luigi Verdi");
        System.out.println("   sinutaro  - Sinutaro Bianchi");
        System.out.println();
        System.out.println(" Eventi:");
        System.out.println("   CARBO123 (pw: risiko) - Carbonara e Risiko   [aperto]");
        System.out.println("   PIZZA456 (pw: pizza)  - Pizzata fine sessione [aperto]");
        System.out.println("   PASTA789 (pw: pasta)  - Pasta alla Norma      [SCADUTO]");
        System.out.println();
        System.out.println(" Utenti nel DB: " + uRepo.count()
                         + " | Eventi: " + eRepo.count()
                         + " | Task: " + tRepo.count());
        System.out.println("=========================================================");
        System.out.println();
    }
}