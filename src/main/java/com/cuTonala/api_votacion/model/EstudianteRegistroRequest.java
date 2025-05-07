package com.cuTonala.api_votacion.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Solicitud para registrar un nuevo estudiante")
public class EstudianteRegistroRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del estudiante", example = "María", required = true)
    private String nombre;
    
    @NotBlank(message = "Los apellidos son obligatorios")
    @Schema(description = "Apellidos del estudiante", example = "López Gómez", required = true)
    private String apellidos;
    
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    @Schema(description = "Correo electrónico institucional", example = "maria.lopez@alumnos.udg.mx", required = true)
    private String correo;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Schema(description = "Contraseña para la cuenta", example = "password123", required = true, minLength = 8)
    private String contraseña;
    
    @NotBlank(message = "El código de estudiante es obligatorio")
    @Pattern(regexp = "^[A-Z][0-9]{8}$", message = "El código debe tener formato válido (por ejemplo A12345678)")
    @Schema(description = "Código único de estudiante", example = "A12345678", required = true, pattern = "^[A-Z][0-9]{8}$")
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
