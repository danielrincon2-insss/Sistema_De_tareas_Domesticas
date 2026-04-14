package com.tareasdomesticas.hogar_service.tareas.application.dto;

public class UsuarioAsignadoDTO {

    private final Long idUsuario;
    private final String etiqueta;

    private UsuarioAsignadoDTO(Long idUsuario, String etiqueta) {
        this.idUsuario = idUsuario;
        this.etiqueta = etiqueta;
    }

    public static UsuarioAsignadoDTO asignado(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("idUsuario no puede ser nulo para un usuario asignado");
        }
        return new UsuarioAsignadoDTO(idUsuario, null);
    }

    public static UsuarioAsignadoDTO sinAsignar() {
        return new UsuarioAsignadoDTO(null, "Sin asignar");
    }

    public boolean estaAsignado() {
        return idUsuario != null;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getEtiqueta() {
        return estaAsignado() ? String.valueOf(idUsuario) : etiqueta;
    }
}
