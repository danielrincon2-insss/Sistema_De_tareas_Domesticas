package com.domesticas.application;

import com.domesticas.domain.model.*;
import com.domesticas.application.ports.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, GroupRepository groupRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public Task createTask(String title, String description, Task.Priority priority, LocalDateTime dueDate, Long assignedToId, Long groupId) {
        User assignedTo = userRepository.findById(assignedToId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        Task task = new Task(title, description, priority, dueDate, assignedTo, group);
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByGroup(Long groupId) {
        return taskRepository.findByGroupId(groupId);
    }

    public List<Task> getTasksByUser(Long userId) {
        return taskRepository.findByAssignedToId(userId);
    }

    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.findById(id).ifPresent(taskRepository::delete);
    }

    public Task updateTaskStatus(Long taskId, Task.Status status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }
}