package com.tareasdomesticas.hogar_service.tareas.application.port.in;
import com.tareasdomesticas.hogar_service.tareas.application.DTO.AsignacionSemanalResponse;
public interface AsignarTareaUseCase {
    AsignacionSemanalResponse asignarTareasSemanales(Long hogarId);
}