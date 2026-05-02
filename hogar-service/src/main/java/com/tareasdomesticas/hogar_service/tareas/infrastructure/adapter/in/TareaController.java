package com.tareasdomesticas.hogar_service.tareas.infrastructure.adapter.in;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tareasdomesticas.hogar_service.tareas.application.port.in.CrearTareaUseCase;
import com.tareasdomesticas.hogar_service.tareas.application.port.in.AsignarTareaUseCase;
import com.tareasdomesticas.hogar_service.tareas.infrastructure.adapter.in.dto.CrearTareaResponse;
import com.tareasdomesticas.hogar_service.tareas.application.dto.AsignacionSemanalResponse;
import com.tareasdomesticas.hogar_service.tareas.application.port.in.ListarTareasUseCase;
import com.tareasdomesticas.hogar_service.tareas.domain.model.Tarea;

import jakarta.validation.Valid;

import com.tareasdomesticas.hogar_service.tareas.infrastructure.adapter.in.dto.CrearTareaRequest;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {
    private final CrearTareaUseCase crearTareaUseCase;
    private final AsignarTareaUseCase asignarTareaUseCase;
    private final ListarTareasUseCase listarTareasUseCase;

    public TareaController(CrearTareaUseCase crearTareaUseCase,
        AsignarTareaUseCase asignarTareaUseCase,
        ListarTareasUseCase listarTareasUseCase) {
            this.crearTareaUseCase = crearTareaUseCase;
            this.asignarTareaUseCase = asignarTareaUseCase;
            this.listarTareasUseCase = listarTareasUseCase;
    }

    @PostMapping
    public ResponseEntity<?> crearTarea(@Valid @RequestBody CrearTareaRequest request) {
        try {
            Tarea tarea = crearTareaUseCase.crearTarea(
                    request.getHogarId(),
                    request.getNombre(),
                    request.getFoto(),
                    request.getFechaLimite(),
                    request.getDificultad(),
                    request.getPrioridad());
            return ResponseEntity.ok(
                    new CrearTareaResponse(
                            "Tarea creada exitosamente",
                            tarea.getIdTarea(),
                            tarea.getNombreTarea(),
                            tarea.getFotoTarea(),
                            tarea.getFechaLimite(),
                            tarea.getDificultad() != null ? tarea.getDificultad().name() : null,
                            tarea.getPrioridad() != null ? tarea.getPrioridad().name() : null,
                            tarea.getEstado() != null ? tarea.getEstado().name() : null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError()
                    .body("Error inesperado: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listarTodasLasTareas() {
        return ResponseEntity.ok(listarTareasUseCase.listarTodas());
    }

    @GetMapping("/hogares/{hogarId}")
    public ResponseEntity<?> listarTareasPorHogar(@PathVariable Long hogarId) {
        return ResponseEntity.ok(listarTareasUseCase.listarPorHogar(hogarId));
    }

    @PostMapping("/hogares/{hogarId}/asignacion-semanal")
    public ResponseEntity<?> asignarTareas(@PathVariable Long hogarId) {
        try {
            AsignacionSemanalResponse resultado = asignarTareaUseCase.asignarTareasSemanales(hogarId);
            return ResponseEntity.ok(resultado);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Error inesperado: " + (e.getMessage() != null ? e.getMessage() : "Error desconocido")));
        }
    }

}