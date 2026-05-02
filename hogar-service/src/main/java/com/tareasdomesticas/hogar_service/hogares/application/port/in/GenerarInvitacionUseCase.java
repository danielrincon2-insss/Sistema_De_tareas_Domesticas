package com.tareasdomesticas.hogar_service.hogares.application.port.in;

public interface GenerarInvitacionUseCase {
    String generarInvitacion(Long hogarId, Integer administradorId);
}
