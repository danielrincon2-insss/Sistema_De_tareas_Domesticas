package com.tareasdomesticas.hogar_service.tareas.application.DTO;

import java.util.List;

public class AsignacionSemanalResponse {

    private String mensaje;
    private Long hogarId;
    private List<TareaAsignadaDTO> tareasAsignadas;
    private List<TareaExcedenteDTO> tareasExcedentes;

    public AsignacionSemanalResponse(String mensaje, Long hogarId,
            List<TareaAsignadaDTO> tareasAsignadas,
            List<TareaExcedenteDTO> tareasExcedentes) {
        this.mensaje = mensaje;
        this.hogarId = hogarId;
        this.tareasAsignadas = tareasAsignadas;
        this.tareasExcedentes = tareasExcedentes;
    }

    public String getMensaje() { return mensaje; }
    public Long getHogarId() { return hogarId; }
    public List<TareaAsignadaDTO> getTareasAsignadas() { return tareasAsignadas; }
    public List<TareaExcedenteDTO> getTareasExcedentes() { return tareasExcedentes; }
}