package com.cuTonala.api_votacion.model;

import jakarta.validation.constraints.*;

public class UsuarioUpdateRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;
    
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    // Modificamos la expresión regular para hacerla más flexible durante el desarrollo
    // Se pueden incluir dominios adicionales específicos si es necesario
    // @Pattern(regexp = ".*@(alumnos|academicos)\\.udg\\.mx$", 
    //          message = "El correo debe ser institucional (@alumnos.udg.mx o @academicos.udg.mx)")
    private String correo;
    
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contraseña;
    
    // La contraseña es opcional para actualizaciones
    private boolean actualizarContraseña = false;
    
    // Campo para facilitar la búsqueda en el frontend
    // Se puede usar para buscar usuarios antes de actualizarlos
    private String terminoBusqueda;
    
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
    
    public boolean isActualizarContraseña() {
        return actualizarContraseña;
    }
    
    public void setActualizarContraseña(boolean actualizarContraseña) {
        this.actualizarContraseña = actualizarContraseña;
    }
    
    /**
     * Obtiene el término de búsqueda usado para encontrar usuarios
     * @return El término de búsqueda
     */
    public String getTerminoBusqueda() {
        return terminoBusqueda;
    }
    
    /**
     * Establece el término de búsqueda para encontrar usuarios
     * Este campo no se guarda en la base de datos
     * @param terminoBusqueda El término para buscar usuarios (código, nombre, correo, etc.)
     */
    public void setTerminoBusqueda(String terminoBusqueda) {
        this.terminoBusqueda = terminoBusqueda;
    }
}
