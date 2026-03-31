package com.domesticas.infrastructure.adapters.repositories;

import com.domesticas.domain.model.Group;
import com.domesticas.application.ports.GroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaGroupRepository extends JpaRepository<Group, Long>, GroupRepository {
}