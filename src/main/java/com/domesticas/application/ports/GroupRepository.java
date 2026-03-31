package com.domesticas.application.ports;

import com.domesticas.domain.model.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    Optional<Group> findById(Long id);
    List<Group> findAll();
    Group save(Group group);
    void delete(Group group);
}