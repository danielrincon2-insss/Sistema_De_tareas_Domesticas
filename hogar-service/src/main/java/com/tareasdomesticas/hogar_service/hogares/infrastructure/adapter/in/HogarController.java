package com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.in;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tareasdomesticas.hogar_service.common.domain.model.Usuario;
import com.tareasdomesticas.hogar_service.hogares.application.port.in.CrearHogarUseCase;
import com.tareasdomesticas.hogar_service.hogares.application.port.in.GenerarInvitacionUseCase;
import com.tareasdomesticas.hogar_service.hogares.application.port.in.RevocarInvitacionUseCase;
import com.tareasdomesticas.hogar_service.hogares.application.port.in.UnirseHogarUseCase;
import com.tareasdomesticas.hogar_service.hogares.domain.model.Hogar;
import com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.in.dto.CrearHogarRequest;
import com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.in.dto.CrearHogarResponse;
import com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.in.dto.GenerarInvitacionRequest;
import com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.in.dto.GenerarInvitacionResponse;
import com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.in.dto.RevocarInvitacionRequest;
import com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.in.dto.RevocarInvitacionResponse;
import com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.in.dto.UnirseHogarRequest;
import com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.in.dto.UnirseHogarResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/hogares")
public class HogarController {
    private final CrearHogarUseCase crearHogarUseCase;
    private final GenerarInvitacionUseCase generarInvitacionUseCase;
    private final UnirseHogarUseCase unirseHogarUseCase;
    private final RevocarInvitacionUseCase revocarInvitacionUseCase;
    private static final Logger logger = LoggerFactory.getLogger(HogarController.class);

    public HogarController(CrearHogarUseCase crearHogarUseCase,
            GenerarInvitacionUseCase generarInvitacionUseCase,
            UnirseHogarUseCase unirseHogarUseCase,
            RevocarInvitacionUseCase revocarInvitacionUseCase) {
        this.crearHogarUseCase = crearHogarUseCase;
        this.generarInvitacionUseCase = generarInvitacionUseCase;
        this.unirseHogarUseCase = unirseHogarUseCase;
        this.revocarInvitacionUseCase = revocarInvitacionUseCase;
    }

    @PostMapping
    public ResponseEntity<CrearHogarResponse> crearHogar(@RequestBody CrearHogarRequest request) {
        if (request == null) {
            logger.warn("Request para crear hogar es null");
            CrearHogarResponse resp = new CrearHogarResponse(null, null, null, null, "Request inválido");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        }

        try {
            logger.info("Creando hogar: {} por usuario id={}", request.getNombreHogar(), request.getUsuarioId());

            Usuario usuario = new Usuario(
                    request.getUsuarioId(),
                    request.getNombreUsuario(),
                    request.getCorreoUsuario());

            Hogar hogarCreado = crearHogarUseCase.crearHogar(
                request.getNombreHogar(),
                request.getDescripcion(),
                usuario);

            CrearHogarResponse resp = new CrearHogarResponse(
                    hogarCreado.getIdHogar(), 
                    hogarCreado.getNombreHogar(),
                    hogarCreado.getDescripcionHogar(),
                    request.getUsuarioId(),
                    "Hogar creado exitosamente"
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(resp);

        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación: {}", e.getMessage());
            CrearHogarResponse resp = new CrearHogarResponse(null, request != null ? request.getNombreHogar() : null,
                    request != null ? request.getDescripcion() : null, request != null ? request.getUsuarioId() : null,
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);

        } catch (IllegalStateException e) {
            logger.warn("Conflicto al crear hogar: {}", e.getMessage());
            CrearHogarResponse resp = new CrearHogarResponse(null, request != null ? request.getNombreHogar() : null,
                    request != null ? request.getDescripcion() : null, request != null ? request.getUsuarioId() : null,
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);

        } catch (Exception e) {
            logger.error("Error inesperado al crear hogar", e);
            CrearHogarResponse resp = new CrearHogarResponse(null, request != null ? request.getNombreHogar() : null,
                    request != null ? request.getDescripcion() : null, request != null ? request.getUsuarioId() : null,
                    "Algo salió mal, inténtelo de nuevo");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    @PostMapping("/{hogarId}/invitaciones")
    public ResponseEntity<?> generarInvitacion(@PathVariable Long hogarId,
            @RequestBody GenerarInvitacionRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Request inválido"));
        }

        try {
            String enlace = generarInvitacionUseCase.generarInvitacion(hogarId, request.getAdministradorId());
            return ResponseEntity.ok(new GenerarInvitacionResponse(enlace, "Invitación generada correctamente"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al generar invitación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado al generar invitación"));
        }
    }

    @PostMapping("/invitaciones/{token}/unirse")
    public ResponseEntity<?> unirseHogar(@PathVariable String token, @RequestBody UnirseHogarRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Request inválido"));
        }

        try {
            var usuario = unirseHogarUseCase.unirseHogar(token, request.getNombreUsuario());
            return ResponseEntity.ok(new UnirseHogarResponse(usuario.getIdUsuario(), usuario.getNombreUsuario(),
                    "Usuario agregado al hogar correctamente"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al unirse al hogar", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado al procesar la invitación"));
        }
    }

    @DeleteMapping("/invitaciones/{token}")
    public ResponseEntity<?> revocarInvitacion(@PathVariable String token,
            @RequestBody RevocarInvitacionRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Request inválido"));
        }

        try {
            revocarInvitacionUseCase.revocarInvitacion(token, request.getAdministradorId());
            return ResponseEntity.ok(new RevocarInvitacionResponse("Invitación revocada correctamente"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al revocar invitación", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado al revocar la invitación"));
        }
    }
}
