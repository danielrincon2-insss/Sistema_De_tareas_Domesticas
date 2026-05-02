package com.tareasdomesticas.hogar_service.hogares.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Invitacion {

    private final String token;
    private final Long hogarId;
    private final LocalDateTime creadoEn;
    private final LocalDateTime expiraEn;
    private boolean valida;

    public Invitacion(Long hogarId, LocalDateTime creadoEn, LocalDateTime expiraEn) {
        this.token = UUID.randomUUID().toString();
        this.hogarId = hogarId;
        this.creadoEn = creadoEn;
        this.expiraEn = expiraEn;
        this.valida = true;
    }

    public String getToken() {
        return token;
    }

    public Long getHogarId() {
        return hogarId;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public LocalDateTime getExpiraEn() {
        return expiraEn;
    }

    public boolean esValida() {
        return valida && LocalDateTime.now().isBefore(expiraEn);
    }

    public void invalidar() {
        this.valida = false;
    }
}
