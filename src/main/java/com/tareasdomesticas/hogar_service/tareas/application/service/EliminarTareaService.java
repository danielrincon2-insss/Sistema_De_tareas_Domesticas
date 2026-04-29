package com.tareasdomesticas.hogar_service.tareas.application.service;

import com.tareasdomesticas.hogar_service.tareas.application.port.in.EliminarTareaUseCase;
import com.tareasdomesticas.hogar_service.tareas.domain.model.Tarea;
import com.tareasdomesticas.hogar_service.tareas.domain.port.out.TareaRepository;
import org.springframework.stereotype.Service;

@Service
public class EliminarTareaService implements EliminarTareaUseCase {

    private final TareaRepository tareaRepository;

    public EliminarTareaService(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    @Override
    public void eliminar(Long tareaId) {
        Tarea tarea = tareaRepository.buscarPorId(tareaId)
                .orElseThrow(() -> new IllegalArgumentException("La tarea no existe"));

        if (tarea.estaAsignada()) {
            throw new IllegalStateException("No se puede eliminar la tarea porque ya fue asignada a un miembro del hogar");
        }

        if (tarea.estaEnProceso()) {
            throw new IllegalStateException("No se puede eliminar la tarea porque se encuentra en estado EN_PROCESO");
        }

        tareaRepository.eliminarPorId(tareaId);
    }
}