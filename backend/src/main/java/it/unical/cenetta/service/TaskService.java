package it.unical.cenetta.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unical.cenetta.dto.CreateTaskRequest;
import it.unical.cenetta.dto.TaskDto;
import it.unical.cenetta.model.Event;
import it.unical.cenetta.model.Task;
import it.unical.cenetta.model.TaskStatus;
import it.unical.cenetta.model.User;
import it.unical.cenetta.repository.EventRepository;
import it.unical.cenetta.repository.TaskRepository;

@Service
@Transactional
public class TaskService {

    private final EventRepository eRepo;
    private final TaskRepository tRepo;
    private final DtoMapper dtoMapper;

    public TaskService(EventRepository eRepo, TaskRepository tRepo, DtoMapper dtoMapper) {
        this.eRepo = eRepo;
        this.dtoMapper = dtoMapper;
        this.tRepo = tRepo;
    }

    public TaskDto create(CreateTaskRequest tRequest, Long eventId, User proposer) {
        
        Event event = eRepo.findById(eventId).orElseThrow(() -> new IllegalArgumentException("L'evento non esiste"));
        if(!event.isMember(proposer)) {
            throw new IllegalArgumentException("L'Utente non appartiene all'evento"); }

        TaskStatus ts = event.isOrganizer(proposer) ? TaskStatus.FREE : TaskStatus.PENDING_APPROVAL;
        Task t = new Task(tRequest.title(), tRequest.note(), ts, event, proposer);
        event.addTask(t);
        tRepo.save(t);

        return dtoMapper.toTaskDto(t);
    }

    public TaskDto claim(Long taskId, User user) {

        Task task = tRepo.findById(taskId).orElseThrow(() -> new IllegalArgumentException("La task non esiste"));
        Event event = task.getEvent();

        if(!event.isMember(user)) {
            throw new IllegalArgumentException("Utente non membro dell'evento");
        }

        if(!(task.getStatus() == TaskStatus.FREE)) {
            throw new IllegalArgumentException("Task non più libera"); 
        }

        task.assignUser(user);
        task.setStatus(TaskStatus.ASSIGNED);
        tRepo.save(task);

        return dtoMapper.toTaskDto(task);
    }

    public TaskDto release(Long taskId, User user) {

        Task task = tRepo.findById(taskId).orElseThrow(() -> new IllegalArgumentException("La task non esiste"));
        Event event = task.getEvent();

        if(!event.isMember(user)) {
            throw new IllegalArgumentException("Utente non membro dell'evento");
        }

        if(task.getStatus() == TaskStatus.FREE) {
            throw new IllegalArgumentException("Task già libera"); 
        }

        task.deAssignUser();
        task.setStatus(TaskStatus.FREE);
        tRepo.save(task);

        return dtoMapper.toTaskDto(task);
    }

    public TaskDto approve(Long taskId, User user) {

        Task t = tRepo.findById(taskId).orElseThrow(() -> new IllegalArgumentException("La Task non esiste"));

        if(t.getStatus() != TaskStatus.PENDING_APPROVAL) {
            throw new IllegalArgumentException("Task non in attesa di approvazione");
        }
        if(!t.getEvent().isOrganizer(user)) {
            throw new IllegalArgumentException("Task approvabile solo dall'organizzatore");
        }

        t.setStatus(TaskStatus.FREE);
        tRepo.save(t);
        return dtoMapper.toTaskDto(t);
    }

    public TaskDto reject(Long taskId, User user) {

        Task t = tRepo.findById(taskId).orElseThrow(() -> new IllegalArgumentException("La Task non esiste"));

        if(t.getStatus() != TaskStatus.PENDING_APPROVAL) {
            throw new IllegalArgumentException("Task non in attesa di approvazione");
        }
        if(!t.getEvent().isOrganizer(user)) {
            throw new IllegalArgumentException("Task rifiutabile solo dall'organizzatore");
        }

        t.setStatus(TaskStatus.REJECTED);
        tRepo.save(t);
        return dtoMapper.toTaskDto(t);
    }

    public TaskDto complete(Long taskId, User user) {

        Task t = tRepo.findById(taskId).orElseThrow(() -> new IllegalArgumentException("La Task non esiste"));
        
        if(t.getStatus() != TaskStatus.ASSIGNED) {
            throw new IllegalArgumentException("Task non assegnata");
        }
        if(!t.getAssignedUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Solo l'assegnatario può completare");
        }

        t.setStatus(TaskStatus.COMPLETED);
        tRepo.save(t);
        return dtoMapper.toTaskDto(t);
    }


}