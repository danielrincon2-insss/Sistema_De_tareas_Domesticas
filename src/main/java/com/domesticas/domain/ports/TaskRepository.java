package com.domesticas.domain.ports;

import com.domesticas.domain.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Optional<Task> findById(Long id);
    List<Task> findByGroupId(Long groupId);
    List<Task> findByAssignedToId(Long userId);
    Task save(Task task);
    void delete(Task task);
}