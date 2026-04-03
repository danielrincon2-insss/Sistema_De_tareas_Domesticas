package com.tareasdomesticas.hogar_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tareasdomesticas.hogar_service.application.service.CrearHogarService;
import com.tareasdomesticas.hogar_service.application.service.CrearTareaService;
import com.tareasdomesticas.hogar_service.domain.port.in.CrearHogarUseCase;
import com.tareasdomesticas.hogar_service.domain.port.in.CrearTareaUseCase;
import com.tareasdomesticas.hogar_service.domain.port.out.HogarRepository;
import com.tareasdomesticas.hogar_service.domain.port.out.TareaRepository;
import com.tareasdomesticas.hogar_service.infrastructure.adapter.out.JpaHogarRepository;
import com.tareasdomesticas.hogar_service.infrastructure.adapter.out.JpaTareaRepository;

@Configuration
public class BeanConfig {
    
    @Bean
    public CrearHogarUseCase crearHogarUseCase(JpaHogarRepository hogarRepository) {
        return new CrearHogarService(hogarRepository);
    }

    @Bean
    public CrearTareaUseCase crearTareaUseCase(JpaTareaRepository tareaRepository) {
        return new CrearTareaService(tareaRepository);
    }
}

