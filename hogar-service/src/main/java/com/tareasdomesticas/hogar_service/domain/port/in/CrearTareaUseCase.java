package com.tareasdomesticas.hogar_service.domain.port.in;

import com.tareasdomesticas.hogar_service.domain.model.Tarea;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public interface CrearTareaUseCase {
    Tarea crearTarea(String nombre, MultipartFile foto, LocalDateTime fechaLimite,
                     Tarea.Dificultad dificultad, Tarea.Prioridad prioridad, Integer hogarId);
}