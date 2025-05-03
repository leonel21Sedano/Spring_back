package com.cuTonala.api_votacion.model;

import jakarta.validation.constraints.*;

public class EstudianteRegistroRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;
    
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    @Pattern(regexp = ".*@(alumnos|academicos)\\.udg\\.mx$", 
             message = "El correo debe ser institucional (@alumnos.udg.mx o @academicos.udg.mx)")
    private String correo;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contraseña;
    
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
