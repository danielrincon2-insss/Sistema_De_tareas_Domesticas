package com.tareasdomesticas.hogar_service.tareas.application.DTO;

import com.tareasdomesticas.hogar_service.tareas.domain.model.DificultadTarea;
import com.tareasdomesticas.hogar_service.tareas.domain.model.EstadoTarea;

public class TareaExcedenteDTO {
    private Long idTarea;
    private String nombre;
    private DificultadTarea dificultad;
    private EstadoTarea estado;

    public TareaExcedenteDTO(Long idTarea, String nombre,
            DificultadTarea dificultad, EstadoTarea estado) {
        this.idTarea = idTarea;
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.estado = estado;
    }

    public Long getIdTarea() { return idTarea; }
    public String getNombre() { return nombre; }
    public DificultadTarea getDificultad() { return dificultad; }
    public EstadoTarea getEstado() { return estado; }
}
