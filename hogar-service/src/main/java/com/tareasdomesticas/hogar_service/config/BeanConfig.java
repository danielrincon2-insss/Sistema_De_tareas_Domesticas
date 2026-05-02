package com.tareasdomesticas.hogar_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tareasdomesticas.hogar_service.hogares.application.service.CrearHogarService;
import com.tareasdomesticas.hogar_service.hogares.application.service.GestionInvitacionService;
import com.tareasdomesticas.hogar_service.hogares.application.port.in.CrearHogarUseCase;
import com.tareasdomesticas.hogar_service.hogares.application.port.in.GenerarInvitacionUseCase;
import com.tareasdomesticas.hogar_service.hogares.application.port.in.RevocarInvitacionUseCase;
import com.tareasdomesticas.hogar_service.hogares.application.port.in.UnirseHogarUseCase;
import com.tareasdomesticas.hogar_service.hogares.domain.port.out.HogarRepository;
import com.tareasdomesticas.hogar_service.hogares.domain.port.out.InvitacionRepository;
import com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.out.InMemoryHogarRepository;
import com.tareasdomesticas.hogar_service.hogares.infrastructure.adapter.out.InMemoryInvitacionRepository;
import com.tareasdomesticas.hogar_service.tareas.application.port.in.AsignarTareaUseCase;
import com.tareasdomesticas.hogar_service.tareas.application.port.in.CrearTareaUseCase;
import com.tareasdomesticas.hogar_service.tareas.application.service.AsignarTareaService;
import com.tareasdomesticas.hogar_service.tareas.application.service.CrearTareaService;
import com.tareasdomesticas.hogar_service.tareas.domain.port.out.TareaRepository;
import com.tareasdomesticas.hogar_service.tareas.infrastructure.adapter.out.InMemoryTareaRepository;
import com.tareasdomesticas.hogar_service.tareas.application.port.in.ListarTareasUseCase;
import com.tareasdomesticas.hogar_service.tareas.application.service.ListarTareasService;

@Configuration
public class BeanConfig {
    @Bean
    public HogarRepository hogarRepository() {
        return new InMemoryHogarRepository();
    }

    @Bean
    public CrearHogarUseCase crearHogarUseCase(HogarRepository repo) {
        return new CrearHogarService(repo);
    }

    @Bean
    public InvitacionRepository invitacionRepository() {
        return new InMemoryInvitacionRepository();
    }

    @Bean
    public GenerarInvitacionUseCase generarInvitacionUseCase(HogarRepository hogarRepo, InvitacionRepository invitacionRepo) {
        return new GestionInvitacionService(hogarRepo, invitacionRepo);
    }

    @Bean
    public UnirseHogarUseCase unirseHogarUseCase(HogarRepository hogarRepo, InvitacionRepository invitacionRepo) {
        return new GestionInvitacionService(hogarRepo, invitacionRepo);
    }

    @Bean
    public RevocarInvitacionUseCase revocarInvitacionUseCase(HogarRepository hogarRepo, InvitacionRepository invitacionRepo) {
        return new GestionInvitacionService(hogarRepo, invitacionRepo);
    }

    @Bean
    public TareaRepository tareaRepository() {
        return new InMemoryTareaRepository();
    }

    @Bean
    public CrearTareaUseCase crearTareaUseCase(TareaRepository repo) {
        return new CrearTareaService(repo);
    }
    @Bean
    public AsignarTareaUseCase asignarTareaUseCase(TareaRepository tareaRepo, HogarRepository hogarRepo) {
        return new AsignarTareaService(tareaRepo, hogarRepo);
    }
    @Bean
    public ListarTareasUseCase listarTareasUseCase(TareaRepository repo) {
        return new ListarTareasService(repo);
    }
}