package com.domesticas.infrastructure.adapters.repositories;

import com.domesticas.application.ports.TaskRepository;
import com.domesticas.domain.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTaskRepository extends JpaRepository<Task, Long>, TaskRepository {

    List<Task> findByGroupId(Long groupId);

    List<Task> findByAssignedToId(Long userId);

    List<Task> findByGroupIdAndStatusOrderByCreatedAtAsc(Long groupId, Task.Status status);
}