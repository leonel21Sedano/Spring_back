package com.cuTonala.api_votacion.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class EstudianteRegistroRequest {
    @Schema(description = "Nombre del estudiante", example = "Juan Carlos")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @Schema(description = "Apellidos del estudiante", example = "Pérez González")
    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;
    
    @Schema(description = "Correo electrónico institucional", example = "juan.perez@alumnos.udg.mx")
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    @Pattern(regexp = ".*@(alumnos|academicos)\\.udg\\.mx$", 
             message = "El correo debe ser institucional (@alumnos.udg.mx o @academicos.udg.mx)")
    private String correo;
    
    @Schema(description = "Contraseña (mínimo 6 caracteres)", example = "password123")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contraseña;
    
    @Schema(description = "Código de estudiante (9 dígitos)", example = "123456789")
    @NotBlank(message = "El código de estudiante es obligatorio")
    @Size(min = 9, max = 9, message = "El código de estudiante debe tener exactamente 9 dígitos")
    @Pattern(regexp = "\\d+", message = "El código de estudiante debe contener solo dígitos")
    private String codigoEstudiante;
    
    // Getters y setters
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
}
