package com.tareasdomesticas.hogar_service.tareas.infrastructure.adapter.out;

import com.tareasdomesticas.hogar_service.tareas.domain.model.EstadoTarea;
import com.tareasdomesticas.hogar_service.tareas.domain.model.Tarea;
import com.tareasdomesticas.hogar_service.tareas.domain.port.out.TareaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryTareaRepository implements TareaRepository {

    private final List<Tarea> tareas = new ArrayList<>();

    public InMemoryTareaRepository() {
        tareas.add(new Tarea(1L, "Lavar platos", "Lavar los platos después del almuerzo", EstadoTarea.PENDIENTE, null));
        tareas.add(new Tarea(2L, "Barrer sala", "Barrer la sala principal", EstadoTarea.EN_PROCESO, null));
        tareas.add(new Tarea(3L, "Sacar basura", "Sacar la basura en la noche", EstadoTarea.PENDIENTE, 10L));
    }

    @Override
    public Tarea guardar(Tarea tarea) {
        tareas.add(tarea);
        return tarea;
    }

    @Override
    public Optional<Tarea> buscarPorId(Long id) {
        return tareas.stream()
                .filter(tarea -> tarea.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Tarea> listar() {
        return tareas;
    }

    @Override
    public void eliminarPorId(Long id) {
        tareas.removeIf(tarea -> tarea.getId().equals(id));
    }
}