package com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.out;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.tareasdomesticas.hogar_service.hogares.domain.model.Invitacion;
import com.tareasdomesticas.hogar_service.hogares.domain.port.out.InvitacionRepository;

public class InMemoryInvitacionRepository implements InvitacionRepository {
    private final Map<String, Invitacion> invitaciones = new ConcurrentHashMap<>();

    @Override
    public Invitacion guardar(Invitacion invitacion) {
        invitaciones.put(invitacion.getToken(), invitacion);
        return invitacion;
    }

    @Override
    public Optional<Invitacion> buscarPorToken(String token) {
        return Optional.ofNullable(invitaciones.get(token));
    }

    @Override
    public void invalidar(String token) {
        invitaciones.computeIfPresent(token, (k, invitacion) -> {
            invitacion.invalidar();
            return invitacion;
        });
    }
}
