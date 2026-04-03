package com.domesticas.infrastructure.adapters.repositories;

import com.domesticas.application.ports.UserRepository;
import com.domesticas.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {

    Optional<User> findByEmail(String email);

    List<User> findByGroupId(Long groupId);
}