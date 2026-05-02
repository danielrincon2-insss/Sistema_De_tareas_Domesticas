package com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.in.dto;

public class GenerarInvitacionResponse {
    private final String enlaceInvitacion;
    private final String mensaje;

    public GenerarInvitacionResponse(String enlaceInvitacion, String mensaje) {
        this.enlaceInvitacion = enlaceInvitacion;
        this.mensaje = mensaje;
    }

    public String getEnlaceInvitacion() {
        return enlaceInvitacion;
    }

    public String getMensaje() {
        return mensaje;
    }
}
