package com.tareasdomesticas.hogar_service.tareas.application.service;

import java.util.List;
import java.util.stream.Collectors;

import com.tareasdomesticas.hogar_service.tareas.application.dto.TareaListadoDTO;
import com.tareasdomesticas.hogar_service.tareas.application.dto.UsuarioAsignadoDTO;
import com.tareasdomesticas.hogar_service.tareas.application.port.in.ListarTareasUseCase;
import com.tareasdomesticas.hogar_service.tareas.domain.port.out.TareaRepository;

public class ListarTareasService implements ListarTareasUseCase {

    private final TareaRepository tareaRepository;

    public ListarTareasService(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    @Override
    public List<TareaListadoDTO> listarTodas() {
        return tareaRepository.listar().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TareaListadoDTO> listarPorHogar(Long hogarId) {
        return tareaRepository.listarPorHogar(hogarId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private TareaListadoDTO toDTO(com.tareasdomesticas.hogar_service.tareas.domain.model.Tarea t) {
        UsuarioAsignadoDTO usuario = t.getIdUsuarioAsignado() != null
                ? UsuarioAsignadoDTO.asignado(t.getIdUsuarioAsignado())
                : UsuarioAsignadoDTO.sinAsignar();
        return new TareaListadoDTO(
                t.getIdTarea(), t.getIdHogar(), t.getNombreTarea(),
                t.getDificultad(), t.getPrioridad(), t.getEstado(),
                usuario, t.getFechaLimite());
    }
}