package com.tareasdomesticas.hogar_service.infrastructure.adapter.out;

import com.tareasdomesticas.hogar_service.domain.model.Tarea;
import com.tareasdomesticas.hogar_service.domain.port.out.TareaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTareaRepository extends JpaRepository<Tarea, Long>, TareaRepository {
    List<Tarea> findByHogarId(Integer hogarId);
}