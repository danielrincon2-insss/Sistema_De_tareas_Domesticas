package com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.in.dto;

public class UnirseHogarResponse {
    private final Integer usuarioId;
    private final String nombreUsuario;
    private final String mensaje;

    public UnirseHogarResponse(Integer usuarioId, String nombreUsuario, String mensaje) {
        this.usuarioId = usuarioId;
        this.nombreUsuario = nombreUsuario;
        this.mensaje = mensaje;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getMensaje() {
        return mensaje;
    }
}
