package it.unical.cenetta.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unical.cenetta.dto.TaskActionRequest;
import it.unical.cenetta.dto.TaskDto;
import it.unical.cenetta.model.User;
import it.unical.cenetta.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService tService;

    public TaskController(TaskService tService) {
        this.tService = tService;
    }

    @PostMapping("/{taskId}/claim")
    public TaskDto claim(@PathVariable Long taskId, @RequestBody TaskActionRequest request, @AuthenticationPrincipal User user) {
        return tService.claim(taskId, request.version(), user);
    }

    @PostMapping("/{taskId}/release")
    public TaskDto release(@PathVariable Long taskId, @RequestBody TaskActionRequest request, @AuthenticationPrincipal User user) {
        return tService.release(taskId, request.version(), user);
    }
    
    @PostMapping("/{taskId}/approve")
    public TaskDto approve(@PathVariable Long taskId, @RequestBody TaskActionRequest request, @AuthenticationPrincipal User user) {
        return tService.approve(taskId, request.version(), user);
    }

    @PostMapping("/{taskId}/reject")
    public TaskDto reject(@PathVariable Long taskId, @RequestBody TaskActionRequest request, @AuthenticationPrincipal User user) {
        return tService.reject(taskId, request.version(), user);
    }

    @PostMapping("/{taskId}/complete")
    public TaskDto complete(@PathVariable Long taskId, @RequestBody TaskActionRequest request, @AuthenticationPrincipal User user) {
        return tService.complete(taskId, request.version(), user);
    }
}


