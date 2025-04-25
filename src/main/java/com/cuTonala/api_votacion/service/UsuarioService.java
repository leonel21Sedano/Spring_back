package com.cuTonala.api_votacion.service;

import com.cuTonala.api_votacion.model.EstudianteRegistroRequest;
import com.cuTonala.api_votacion.model.Usuario;
import com.cuTonala.api_votacion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Usuario registrarEstudiante(EstudianteRegistroRequest request) {
        // Verificar si el correo ya existe
        if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }
        
        // Verificar si el código de estudiante ya existe
        if (usuarioRepository.findByCodigoEstudiante(request.getCodigoEstudiante()).isPresent()) {
            throw new RuntimeException("El código de estudiante ya está registrado");
        }
        
        // Crear nuevo usuario con rol ESTUDIANTE
        Usuario estudiante = new Usuario();
        estudiante.setNombre(request.getNombre());
        estudiante.setApellidos(request.getApellidos());
        estudiante.setCorreo(request.getCorreo());
        estudiante.setContraseña(passwordEncoder.encode(request.getContraseña()));
        estudiante.setCodigoEstudiante(request.getCodigoEstudiante());
        estudiante.setRol(Usuario.Rol.ESTUDIANTE);
        estudiante.setActivo(true);
        
        return usuarioRepository.save(estudiante);
    }
}
