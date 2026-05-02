package com.tareasdomesticas.hogar_service.hogares.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tareasdomesticas.hogar_service.common.domain.model.Usuario;
import com.tareasdomesticas.hogar_service.hogares.domain.model.Hogar;
import com.tareasdomesticas.hogar_service.hogares.domain.port.out.HogarRepository;
import com.tareasdomesticas.hogar_service.hogares.domain.port.out.InvitacionRepository;
import com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.out.InMemoryHogarRepository;
import com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.out.InMemoryInvitacionRepository;

class GestionInvitacionServiceTest {

    private HogarRepository hogarRepository;
    private InvitacionRepository invitacionRepository;
    private GestionInvitacionService gestionInvitacionService;

    @BeforeEach
    void setup() {
        hogarRepository = new InMemoryHogarRepository();
        invitacionRepository = new InMemoryInvitacionRepository();
        gestionInvitacionService = new GestionInvitacionService(hogarRepository, invitacionRepository);

        Usuario administrador = new Usuario(1, "Admin Prueba", "admin@prueba.com");
        Hogar hogar = new Hogar(1, "Casa Test", "Hogar para pruebas", administrador);
        hogarRepository.guardar(hogar);
    }

    @Test
    void debeGenerarInvitacionValida() {
        String enlace = gestionInvitacionService.generarInvitacion(1L, 1);

        assertNotNull(enlace);
        assertTrue(enlace.contains("/api/hogares/invitaciones/"));
    }

    @Test
    void debeUnirseHogarConNombreValido() {
        String enlace = gestionInvitacionService.generarInvitacion(1L, 1);
        String token = enlace.substring(enlace.lastIndexOf('/') + 1);

        var usuario = gestionInvitacionService.unirseHogar(token, "Juan Pérez");

        assertNotNull(usuario);
        assertEquals("Juan Pérez", usuario.getNombreUsuario());
        assertEquals(2, hogarRepository.buscarPorId(1L).get().getCantidadMiembros());
    }

    @Test
    void noPermiteUnirseSinNombre() {
        String enlace = gestionInvitacionService.generarInvitacion(1L, 1);
        String token = enlace.substring(enlace.lastIndexOf('/') + 1);

        assertThrows(IllegalArgumentException.class, () -> gestionInvitacionService.unirseHogar(token, ""));
    }

    @Test
    void noPermiteUnirseConNombreDuplicado() {
        String enlace = gestionInvitacionService.generarInvitacion(1L, 1);
        String token = enlace.substring(enlace.lastIndexOf('/') + 1);

        gestionInvitacionService.unirseHogar(token, "Ana");

        assertThrows(IllegalArgumentException.class, () -> gestionInvitacionService.unirseHogar(token, "Ana"));
    }

    @Test
    void noPermiteUnirseConTokenInvalido() {
        assertThrows(IllegalStateException.class, () -> gestionInvitacionService.unirseHogar("token-no-valido", "Pedro"));
    }

    @Test
    void revocarInvitacionInvalidaElToken() {
        String enlace = gestionInvitacionService.generarInvitacion(1L, 1);
        String token = enlace.substring(enlace.lastIndexOf('/') + 1);

        gestionInvitacionService.revocarInvitacion(token, 1);

        assertThrows(IllegalStateException.class, () -> gestionInvitacionService.unirseHogar(token, "Carla"));
    }

    @Test
    void noPermiteGenerarInvitacionSiHogarEstaLleno() {
        var hogar = hogarRepository.buscarPorId(1L).get();
        hogar.agregarMiembro(new Usuario(2, "Uno", "uno@prueba.com"));
        hogar.agregarMiembro(new Usuario(3, "Dos", "dos@prueba.com"));
        hogar.agregarMiembro(new Usuario(4, "Tres", "tres@prueba.com"));
        hogar.agregarMiembro(new Usuario(5, "Cuatro", "cuatro@prueba.com"));
        hogar.agregarMiembro(new Usuario(6, "Cinco", "cinco@prueba.com"));
        hogarRepository.guardar(hogar);

        assertThrows(IllegalStateException.class, () -> gestionInvitacionService.generarInvitacion(1L, 1));
    }
}
