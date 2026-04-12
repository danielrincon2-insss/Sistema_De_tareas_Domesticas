package com.tareasdomesticas.hogar_service.tareas.application.DTO;

import java.time.LocalDateTime;
import com.tareasdomesticas.hogar_service.tareas.domain.model.DificultadTarea;
import com.tareasdomesticas.hogar_service.tareas.domain.model.EstadoTarea;
import com.tareasdomesticas.hogar_service.tareas.domain.model.PrioridadTarea;

public class TareaListadoDTO {
    private Long idTarea;
    private Long idHogar;
    private String nombre;
    private DificultadTarea dificultad;
    private PrioridadTarea prioridad;
    private EstadoTarea estado;
    private Object usuarioAsignado;
    private LocalDateTime fechaLimite;

    public TareaListadoDTO(Long idTarea, Long idHogar, String nombre,
            DificultadTarea dificultad, PrioridadTarea prioridad,
            EstadoTarea estado, Object usuarioAsignado, LocalDateTime fechaLimite) {
        this.idTarea = idTarea;
        this.idHogar = idHogar;
        this.nombre = nombre;
        this.dificultad = dificultad;
        this.prioridad = prioridad;
        this.estado = estado;
        this.usuarioAsignado = usuarioAsignado;
        this.fechaLimite = fechaLimite;
    }

    public Long getIdTarea() { return idTarea; }
    public Long getIdHogar() { return idHogar; }
    public String getNombre() { return nombre; }
    public DificultadTarea getDificultad() { return dificultad; }
    public PrioridadTarea getPrioridad() { return prioridad; }
    public EstadoTarea getEstado() { return estado; }
    public Object getUsuarioAsignado() { return usuarioAsignado; }
    public LocalDateTime getFechaLimite() { return fechaLimite; }
}