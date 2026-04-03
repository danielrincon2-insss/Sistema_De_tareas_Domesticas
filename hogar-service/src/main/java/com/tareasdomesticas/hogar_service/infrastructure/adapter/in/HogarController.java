package com.tareasdomesticas.hogar_service.infrastructure.adapter.in;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tareasdomesticas.hogar_service.domain.model.Usuario;
import com.tareasdomesticas.hogar_service.domain.port.in.CrearHogarUseCase;
import com.tareasdomesticas.hogar_service.infrastructure.adapter.in.DTO.CrearHogarRequest;
import com.tareasdomesticas.hogar_service.infrastructure.adapter.out.JpaUsuarioRepository;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/hogares")
public class HogarController {
    private final CrearHogarUseCase crearHogarUseCase;
    private final JpaUsuarioRepository usuarioRepository;

    public HogarController(CrearHogarUseCase crearHogarUseCase, JpaUsuarioRepository usuarioRepository) {
        this.crearHogarUseCase = crearHogarUseCase;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public String crearHogar(@RequestBody CrearHogarRequest request) {
        Usuario usuario = new Usuario(
                null,
                request.getNombreUsuario(),
                request.getCorreoUsuario());

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        crearHogarUseCase.crearHogar(
                request.getNombreHogar(),
                request.getDescripcion(),
                usuarioGuardado);

        return "Hogar creado exitosamente";
    }

}
