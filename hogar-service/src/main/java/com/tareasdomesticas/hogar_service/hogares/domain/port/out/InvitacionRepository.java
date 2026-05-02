package com.tareasdomesticas.hogar_service.hogares.domain.port.out;

import java.util.Optional;

import com.tareasdomesticas.hogar_service.hogares.domain.model.Invitacion;

public interface InvitacionRepository {
    Invitacion guardar(Invitacion invitacion);
    Optional<Invitacion> buscarPorToken(String token);
    void invalidar(String token);
}
