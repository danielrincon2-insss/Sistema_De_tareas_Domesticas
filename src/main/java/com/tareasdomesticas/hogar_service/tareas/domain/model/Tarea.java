package com.tareasdomesticas.hogar_service.tareas.domain.model;

public class Tarea {

    private Long id;
    private String nombre;
    private String descripcion;
    private EstadoTarea estado;
    private Long usuarioAsignadoId;

    public Tarea() {
    }

    public Tarea(Long id, String nombre, String descripcion, EstadoTarea estado, Long usuarioAsignadoId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.usuarioAsignadoId = usuarioAsignadoId;
    }

    public boolean estaAsignada() {
        return usuarioAsignadoId != null;
    }

    public boolean estaEnProceso() {
        return EstadoTarea.EN_PROCESO.equals(estado);
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public Long getUsuarioAsignadoId() {
        return usuarioAsignadoId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setEstado(EstadoTarea estado) {
        this.estado = estado;
    }

    public void setUsuarioAsignadoId(Long usuarioAsignadoId) {
        this.usuarioAsignadoId = usuarioAsignadoId;
    }
}