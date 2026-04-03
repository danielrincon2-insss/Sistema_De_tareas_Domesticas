package com.tareasdomesticas.hogar_service.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tareas")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(nullable = false)
    private String nombre;

    @Column
    private String fotoPath; // Path to the uploaded image

    @NotNull(message = "La fecha límite es obligatoria")
    @Future(message = "La fecha límite debe ser posterior a la fecha actual")
    @Column(nullable = false)
    private LocalDateTime fechaLimite;

    @NotNull(message = "La dificultad es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Dificultad dificultad;

    @NotNull(message = "La prioridad es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTarea estado = EstadoTarea.PENDIENTE;

    @NotNull(message = "El hogar es obligatorio")
    @Column(nullable = false)
    private Integer hogarId;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    // Constructors
    public Tarea() {}

    public Tarea(String nombre, String fotoPath, LocalDateTime fechaLimite,
                 Dificultad dificultad, Prioridad prioridad, Integer hogarId) {
        this.nombre = nombre;
        this.fotoPath = fotoPath;
        this.fechaLimite = fechaLimite;
        this.dificultad = dificultad;
        this.prioridad = prioridad;
        this.hogarId = hogarId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFotoPath() { return fotoPath; }
    public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; }

    public LocalDateTime getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(LocalDateTime fechaLimite) { this.fechaLimite = fechaLimite; }

    public Dificultad getDificultad() { return dificultad; }
    public void setDificultad(Dificultad dificultad) { this.dificultad = dificultad; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public EstadoTarea getEstado() { return estado; }
    public void setEstado(EstadoTarea estado) { this.estado = estado; }

    public Integer getHogarId() { return hogarId; }
    public void setHogarId(Integer hogarId) { this.hogarId = hogarId; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // Enums
    public enum Dificultad {
        BAJA, MEDIA, ALTA
    }

    public enum Prioridad {
        BAJA, MEDIA, ALTA
    }

    public enum EstadoTarea {
        PENDIENTE, COMPLETADA, CANCELADA
    }
}