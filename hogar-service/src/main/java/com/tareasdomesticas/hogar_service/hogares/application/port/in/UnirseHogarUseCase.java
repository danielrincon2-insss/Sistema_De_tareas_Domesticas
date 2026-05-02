package com.tareasdomesticas.hogar_service.hogares.application.port.in;

import com.tareasdomesticas.hogar_service.common.domain.model.Usuario;

public interface UnirseHogarUseCase {
    Usuario unirseHogar(String token, String nombreUsuario);
}
