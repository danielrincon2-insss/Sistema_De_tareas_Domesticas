package com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.in.dto;

public class RevocarInvitacionResponse {
    private final String mensaje;

    public RevocarInvitacionResponse(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
