package com.cuTonala.api_votacion.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthResponse {
    @Schema(description = "Token JWT para autenticación", 
           example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Correo electrónico del usuario", 
           example = "estudiante@alumnos.udg.mx")
    private String correo;
    
    @Schema(description = "Nombre del usuario", 
           example = "Juan")
    private String nombre;
    
    @Schema(description = "Rol del usuario", 
           example = "ESTUDIANTE", 
           allowableValues = {"ADMIN", "ENCARGADO", "ESTUDIANTE"})
    private String rol;

    public AuthResponse() {}
    
    public AuthResponse(String token, String correo, String nombre, String rol) {
        this.token = token;
        this.correo = correo;
        this.nombre = nombre;
        this.rol = rol;
    }

    // Getters y setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}