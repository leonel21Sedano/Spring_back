package com.cuTonala.api_votacion.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthRequest {
    @Schema(description = "Correo electrónico del usuario", example = "estudiante@alumnos.udg.mx", required = true)
    private String correo;
    
    @Schema(description = "Contraseña del usuario", example = "password123", required = true)
    private String contraseña;
    
    // Getters y setters
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
}