package com.tareasdomesticas.hogar_service.infrastructure.adapter.out;

import com.tareasdomesticas.hogar_service.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUsuarioRepository extends JpaRepository<Usuario, Integer> {
}