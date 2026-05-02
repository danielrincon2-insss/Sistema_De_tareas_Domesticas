package com.tareasdomesticas.hogar_service.hogares.application.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.tareasdomesticas.hogar_service.common.domain.model.Usuario;
import com.tareasdomesticas.hogar_service.hogares.application.port.in.GenerarInvitacionUseCase;
import com.tareasdomesticas.hogar_service.hogares.application.port.in.RevocarInvitacionUseCase;
import com.tareasdomesticas.hogar_service.hogares.application.port.in.UnirseHogarUseCase;
import com.tareasdomesticas.hogar_service.hogares.domain.model.Hogar;
import com.tareasdomesticas.hogar_service.hogares.domain.model.Invitacion;
import com.tareasdomesticas.hogar_service.hogares.domain.port.out.HogarRepository;
import com.tareasdomesticas.hogar_service.hogares.domain.port.out.InvitacionRepository;

public class GestionInvitacionService implements GenerarInvitacionUseCase, UnirseHogarUseCase, RevocarInvitacionUseCase {

    private static final String URL_BASE = "http://localhost:8080/api/hogares/invitaciones/";
    private final HogarRepository hogarRepository;
    private final InvitacionRepository invitacionRepository;

    public GestionInvitacionService(HogarRepository hogarRepository, InvitacionRepository invitacionRepository) {
        this.hogarRepository = hogarRepository;
        this.invitacionRepository = invitacionRepository;
    }

    @Override
    public String generarInvitacion(Long hogarId, Integer administradorId) {
        if (hogarId == null || administradorId == null) {
            throw new IllegalArgumentException("Hogar y administrador son requeridos");
        }

        Hogar hogar = hogarRepository.buscarPorId(hogarId)
                .orElseThrow(() -> new IllegalStateException("No se encontró el hogar con id: " + hogarId));

        if (!hogar.getAdministrador().getIdUsuario().equals(administradorId)) {
            throw new IllegalStateException("Solo el administrador puede generar la invitación");
        }

        if (!hogar.puedeAgregarMiembro()) {
            throw new IllegalStateException("El hogar ya tiene 6 miembros registrados");
        }

        Invitacion invitacion = new Invitacion(
                hogarId,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1));

        invitacionRepository.guardar(invitacion);
        return URL_BASE + invitacion.getToken();
    }

    @Override
    public Usuario unirseHogar(String token, String nombreUsuario) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token de invitación es obligatorio");
        }

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        Invitacion invitacion = invitacionRepository.buscarPorToken(token)
                .orElseThrow(() -> new IllegalStateException("La invitación no es válida"));

        if (!invitacion.esValida()) {
            throw new IllegalStateException("La invitación no es válida");
        }

        Hogar hogar = hogarRepository.buscarPorId(invitacion.getHogarId())
                .orElseThrow(() -> new IllegalStateException("No se encontró el hogar de la invitación"));

        if (!hogar.puedeAgregarMiembro()) {
            throw new IllegalStateException("Se alcanzó el límite de miembros");
        }

        if (hogar.tieneMiembroConNombre(nombreUsuario)) {
            throw new IllegalArgumentException("El nombre ya está en uso");
        }

        Usuario nuevoUsuario = new Usuario(
                generarIdUsuario(hogar),
                nombreUsuario,
                generarCorreoGenerico(nombreUsuario));

        hogar.agregarMiembro(nuevoUsuario);
        hogarRepository.guardar(hogar);

        return nuevoUsuario;
    }

    private Integer generarIdUsuario(Hogar hogar) {
        return hogar.getUsuarios().stream()
                .map(Usuario::getIdUsuario)
                .max(Integer::compareTo)
                .map(id -> id + 1)
                .orElse(1);
    }

    private String generarCorreoGenerico(String nombreUsuario) {
        String slug = nombreUsuario.trim().toLowerCase().replaceAll("\\s+", ".");
        return slug + "@hogar.local";
    }

    @Override
    public void revocarInvitacion(String token, Integer administradorId) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token de invitación es obligatorio");
        }

        if (administradorId == null) {
            throw new IllegalArgumentException("El administrador es requerido");
        }

        Invitacion invitacion = invitacionRepository.buscarPorToken(token)
                .orElseThrow(() -> new IllegalStateException("La invitación no es válida"));

        Hogar hogar = hogarRepository.buscarPorId(invitacion.getHogarId())
                .orElseThrow(() -> new IllegalStateException("No se encontró el hogar de la invitación"));

        if (!hogar.getAdministrador().getIdUsuario().equals(administradorId)) {
            throw new IllegalStateException("Solo el administrador puede revocar la invitación");
        }

        invitacionRepository.invalidar(token);
    }
}
