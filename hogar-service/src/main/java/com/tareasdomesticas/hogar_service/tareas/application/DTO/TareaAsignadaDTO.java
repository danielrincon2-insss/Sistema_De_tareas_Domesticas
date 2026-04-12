package com.tareasdomesticas.hogar_service.tareas.application.DTO;

import com.tareasdomesticas.hogar_service.tareas.domain.model.DificultadTarea;
import com.tareasdomesticas.hogar_service.tareas.domain.model.EstadoTarea;

public class TareaAsignadaDTO {
    private Long idTarea;
    private String nombre;
    private DificultadTarea dificultad;
    private Long usuarioAsignado;
    private EstadoTarea estado;

    public TareaAsignadaDTO(Long idTarea, String nombre,
            DificultadTarea dificultad, Long usuarioAsignado, EstadoTarea estado) {
        this.idTarea = idTarea;
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.usuarioAsignado = usuarioAsignado;
        this.estado = estado;
    }

    public Long getIdTarea() { return idTarea; }
    public String getNombre() { return nombre; }
    public DificultadTarea getDificultad() { return dificultad; }
    public Long getUsuarioAsignado() { return usuarioAsignado; }
    public EstadoTarea getEstado() { return estado; }
}