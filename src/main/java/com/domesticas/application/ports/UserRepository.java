package com.domesticas.application.ports;

import com.domesticas.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findByGroupId(Long groupId);
    User save(User user);
    void delete(User user);
}