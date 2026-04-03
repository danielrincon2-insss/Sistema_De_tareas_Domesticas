package com.tareasdomesticas.hogar_service.domain.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hogares")
public class Hogar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHogar;

    @Column(nullable = false)
    private String nombreHogar;

    @Column
    private String descripcionHogar;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "hogar_id")
    private List<Usuario> usuarios;

    // Constructor vacío para JPA
    public Hogar() {}

    public Hogar(Integer idHogar, String nombreHogar, String descripcionHogar, Usuario creador) {
        validarNombre(nombreHogar);

        this.idHogar = idHogar;
        this.nombreHogar = nombreHogar;
        this.descripcionHogar = descripcionHogar;
        this.usuarios = new ArrayList<>();

        creador.convertirEnAdministrador();
        this.usuarios.add(creador);
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (nombre.length() < 3 || nombre.length() > 50) {
            throw new IllegalArgumentException("El nombre no cumple la longitud permitida");
        }
    }

    public Integer getIdHogar() {
        return idHogar;
    }

    public void setIdHogar(Integer idHogar) {
        this.idHogar = idHogar;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public String getNombreHogar() {
        return nombreHogar;
    }

    public void setNombreHogar(String nombreHogar) {
        this.nombreHogar = nombreHogar;
    }

    public String getDescripcionHogar() {
        return descripcionHogar;
    }

    public void setDescripcionHogar(String descripcionHogar) {
        this.descripcionHogar = descripcionHogar;
    }
}
