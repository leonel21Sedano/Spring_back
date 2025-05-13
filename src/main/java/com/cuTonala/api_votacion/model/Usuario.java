package com.cuTonala.api_votacion.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Schema(description = "Entidad que representa un usuario del sistema")
@Entity
public class Usuario {
    @Schema(description = "Identificador único autoincremental", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Schema(description = "Nombre del usuario", example = "Juan Carlos")
    private String nombre;
    
    @Schema(description = "Apellidos del usuario", example = "Pérez González")
    private String apellidos;
    
    @Schema(description = "Correo electrónico (único)", example = "juan.perez@alumnos.udg.mx")
    @Column(unique = true)
    private String correo;
    
    @Schema(description = "Contraseña encriptada (no se muestra en respuestas)", hidden = true)
    private String contraseña;
    
    @Schema(description = "Código de estudiante (solo para estudiantes)", example = "123456789")
    private String codigoEstudiante;
    
    @Schema(description = "Rol del usuario", example = "ESTUDIANTE")
    @Enumerated(EnumType.STRING)
    private Rol rol;
    
    @Schema(description = "Estado de la cuenta (activa/inactiva)", example = "true")
    private boolean activo = true;

    // Enum para roles
    public enum Rol {
        ADMIN, ENCARGADO, ESTUDIANTE
    }

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
}