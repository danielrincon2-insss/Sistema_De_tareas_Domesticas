package com.tareasdomesticas.hogar_service.infrastructure.adapter.in;

import com.tareasdomesticas.hogar_service.application.service.CrearTareaService;
import com.tareasdomesticas.hogar_service.domain.model.Tarea;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tareas")
@Validated
public class TareaController {

    private final CrearTareaService crearTareaService;

    public TareaController(CrearTareaService crearTareaService) {
        this.crearTareaService = crearTareaService;
    }

    @PostMapping
    public ResponseEntity<?> crearTarea(
            @RequestParam("nombre") @NotBlank(message = "El nombre es obligatorio") @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres") String nombre,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            @RequestParam("fechaLimite") @NotBlank(message = "La fecha límite es obligatoria") String fechaLimiteStr,
            @RequestParam("dificultad") @NotBlank(message = "La dificultad es obligatoria") String dificultadStr,
            @RequestParam("prioridad") @NotBlank(message = "La prioridad es obligatoria") String prioridadStr,
            @RequestParam("hogarId") @NotNull(message = "El hogar es obligatorio") Integer hogarId) {

        try {
            LocalDateTime fechaLimite = LocalDateTime.parse(fechaLimiteStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            if (fechaLimite.isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body(Map.of("error", "La fecha límite debe ser posterior a la fecha actual"));
            }

            Tarea.Dificultad dificultad = Tarea.Dificultad.valueOf(dificultadStr.toUpperCase());
            Tarea.Prioridad prioridad = Tarea.Prioridad.valueOf(prioridadStr.toUpperCase());

            Tarea tarea = crearTareaService.crearTarea(nombre, foto, fechaLimite, dificultad, prioridad, hogarId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Tarea creada exitosamente.");
            response.put("tarea", tarea);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Algo salió mal, inténtelo de nuevo."));
        }
    }

    @GetMapping("/hogar/{hogarId}")
    public ResponseEntity<?> listarTareasPorHogar(@PathVariable Integer hogarId) {
        try {
            return ResponseEntity.ok().body(Map.of("message", "Endpoint para listar tareas por hogar", "hogarId", hogarId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al listar tareas"));
        }
    }
}