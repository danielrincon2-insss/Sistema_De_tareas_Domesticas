package com.tareasdomesticas.hogar_service.hogares.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.tareasdomesticas.hogar_service.common.domain.model.Usuario;

public class Hogar {
    private static final int MAX_MIEMBROS = 6;

    private Integer idHogar;
    private String nombreHogar;
    private String descripcionHogar;
    private Usuario administrador;
    private List<Usuario> usuarios;

    public Hogar(Integer idHogar, String nombreHogar, String descripcionHogar, Usuario creador) {
        validarNombre(nombreHogar);

        this.idHogar = idHogar;
        this.nombreHogar = nombreHogar;
        this.descripcionHogar = descripcionHogar;
        this.usuarios = new ArrayList<>();

        creador.convertirEnAdministrador();
        this.administrador = creador;
        this.usuarios.add(creador);
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del hogar es obligatorio");
        }

        if (nombre.length() < 3 || nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre del hogar no cumple la longitud permitida");
        }
    }

    public Integer getIdHogar() {
        return idHogar;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public String getNombreHogar() {
        return nombreHogar;
    }

    public Usuario getAdministrador() {
        return administrador;
    }

    public String getDescripcionHogar() {
        return descripcionHogar;
    }

    public int getCantidadMiembros() {
        return usuarios.size();
    }

    public boolean tieneMiembroConNombre(String nombreUsuario) {
        return usuarios.stream()
                .anyMatch(u -> u.getNombreUsuario() != null && u.getNombreUsuario().equalsIgnoreCase(nombreUsuario));
    }

    public boolean puedeAgregarMiembro() {
        return usuarios.size() < MAX_MIEMBROS;
    }

    public void agregarMiembro(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        if (!puedeAgregarMiembro()) {
            throw new IllegalStateException("El hogar ya tiene 6 miembros registrados");
        }

        if (tieneMiembroConNombre(usuario.getNombreUsuario())) {
            throw new IllegalArgumentException("El nombre ya está en uso");
        }

        this.usuarios.add(usuario);
    }
}
