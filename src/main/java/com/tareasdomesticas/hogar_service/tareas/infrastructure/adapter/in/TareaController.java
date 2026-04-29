package com.tareasdomesticas.hogar_service.tareas.infrastructure.adapter.in;

import com.tareasdomesticas.hogar_service.tareas.application.port.in.EliminarTareaUseCase;
import com.tareasdomesticas.hogar_service.tareas.domain.model.Tarea;
import com.tareasdomesticas.hogar_service.tareas.domain.port.out.TareaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final EliminarTareaUseCase eliminarTareaUseCase;
    private final TareaRepository tareaRepository;

    public TareaController(EliminarTareaUseCase eliminarTareaUseCase, TareaRepository tareaRepository) {
        this.eliminarTareaUseCase = eliminarTareaUseCase;
        this.tareaRepository = tareaRepository;
    }

    @GetMapping
    public ResponseEntity<List<Tarea>> listarTareas() {
        return ResponseEntity.ok(tareaRepository.listar());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarTarea(@PathVariable Long id) {
        try {
            eliminarTareaUseCase.eliminar(id);
            return ResponseEntity.ok("Tarea eliminada correctamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}