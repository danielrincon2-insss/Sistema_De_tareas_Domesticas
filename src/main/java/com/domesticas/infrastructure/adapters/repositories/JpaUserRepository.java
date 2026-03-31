package com.domesticas.infrastructure.adapters.repositories;

import com.domesticas.domain.model.User;
import com.domesticas.application.ports.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {
    Optional<User> findByEmail(String email);
}