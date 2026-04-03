package com.domesticas.presentation.adapters.controllers;

import com.domesticas.application.TaskService;
import com.domesticas.domain.model.Task;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskRequest request) {
        Task task = taskService.createTask(
                request.title,
                request.description,
                request.priority,
                request.difficulty,
                request.dueDate,
                request.assignedToId,
                request.groupId
        );
        return ResponseEntity.ok(task);
    }

    @PostMapping("/group/{groupId}/assign-weekly")
    public ResponseEntity<TaskService.WeeklyAssignmentResult> assignWeeklyTasks(@PathVariable Long groupId) {
        TaskService.WeeklyAssignmentResult result = taskService.assignWeeklyTasks(groupId);

        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().body(result);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Task>> getTasksByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(taskService.getTasksByGroup(groupId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getTasksByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        task.setId(id);
        return ResponseEntity.ok(taskService.updateTask(task));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        Task task = taskService.updateTaskStatus(id, request.status);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    public static class CreateTaskRequest {
        public String title;
        public String description;
        public Task.Priority priority;
        public Task.Difficulty difficulty;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        public LocalDateTime dueDate;

        public Long assignedToId; // opcional
        public Long groupId;
    }

    public static class UpdateStatusRequest {
        public Task.Status status;
    }
}