package com.cuTonala.api_votacion.service;

import com.cuTonala.api_votacion.model.AuthRequest;
import com.cuTonala.api_votacion.model.AuthResponse;
import com.cuTonala.api_votacion.model.Usuario;
import com.cuTonala.api_votacion.repository.UsuarioRepository;
import com.cuTonala.api_votacion.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse authenticate(AuthRequest request) {
        // Buscar usuario por correo
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(request.getCorreo());
        
        if (usuarioOpt.isEmpty()) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verificar contraseña
        if (!passwordEncoder.matches(request.getContraseña(), usuario.getContraseña())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        
        // Generar token JWT
        String token = generateToken(usuario);
        
        // Crear y devolver respuesta
        return new AuthResponse(token, usuario.getCorreo(), usuario.getNombre(), usuario.getRol().toString());
    }
    
    private String generateToken(Usuario usuario) {
        // Usar el método existente en JwtService o implementar uno nuevo
        return jwtService.generateToken(usuario.getCorreo());
    }
}