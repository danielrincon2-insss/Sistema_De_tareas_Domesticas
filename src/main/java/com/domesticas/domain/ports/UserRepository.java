package com.domesticas.domain.ports;

import com.domesticas.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    User save(User user);
    void delete(User user);
}