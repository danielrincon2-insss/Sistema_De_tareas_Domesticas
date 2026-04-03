package com.tareasdomesticas.hogar_service.domain.port.out;

import com.tareasdomesticas.hogar_service.domain.model.Tarea;
import java.util.List;
import java.util.Optional;

public interface TareaRepository {
    Tarea save(Tarea tarea);
    Optional<Tarea> findById(Long id);
    List<Tarea> findByHogarId(Integer hogarId);
    void deleteById(Long id);
}