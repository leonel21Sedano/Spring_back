package com.cuTonala.api_votacion.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "usuarios")
@Schema(description = "Entidad que representa a un usuario en el sistema")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del usuario", example = "1")
    private Long id;
    
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String nombre;
    
    @Schema(description = "Apellidos del usuario", example = "Pérez López")
    private String apellidos;
    
    @Column(unique = true)
    @Schema(description = "Correo electrónico (identificador único)", example = "juan.perez@alumnos.udg.mx")
    private String correo;
    
    @Schema(description = "Contraseña del usuario (codificada)", example = "[contraseña codificada]", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String contraseña;
    
    @Column(unique = true)
    @Schema(description = "Código de estudiante para alumnos", example = "A12345678", nullable = true)
    private String codigoEstudiante;
    
    @Enumerated(EnumType.STRING)
    @Schema(description = "Rol del usuario (ADMIN, ENCargado, ESTUDIANTE)", example = "ESTUDIANTE")
    private Rol rol;
    
    @Schema(description = "Indica si el usuario está activo en el sistema", example = "true")
    private boolean activo;
    
    @Schema(description = "Fecha de último acceso", example = "2023-05-07T14:30:00", nullable = true)
    private Date ultimoAcceso;
    
    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }

    public void setCodigoEstudiante(String codigoEstudiante) {
        this.codigoEstudiante = codigoEstudiante;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Date getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(Date ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    // Enum para roles
    public enum Rol {
        @Schema(description = "Administrador del sistema")
        ADMIN,
        
        @Schema(description = "Encargado de administrar opciones de votación")
        ENCARGADO,
        
        @Schema(description = "Estudiante que puede votar")
        ESTUDIANTE
    }
}