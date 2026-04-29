package com.tareasdomesticas.hogar_service.tareas.domain.port.out;

import com.tareasdomesticas.hogar_service.tareas.domain.model.Tarea;

import java.util.List;
import java.util.Optional;

public interface TareaRepository {

    Tarea guardar(Tarea tarea);

    Optional<Tarea> buscarPorId(Long id);

    List<Tarea> listar();

    void eliminarPorId(Long id);
}