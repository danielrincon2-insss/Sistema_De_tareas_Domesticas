package com.tareasdomesticas.hogar_service.hogares.application.port.in;

public interface RevocarInvitacionUseCase {
    void revocarInvitacion(String token, Integer administradorId);
}
