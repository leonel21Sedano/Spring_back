package com.cuTonala.api_votacion.service;

import com.cuTonala.api_votacion.model.AuthRequest;
import com.cuTonala.api_votacion.model.AuthResponse;
import com.cuTonala.api_votacion.model.Usuario;
import com.cuTonala.api_votacion.repository.UsuarioRepository;
import com.cuTonala.api_votacion.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getCorreo(), 
                request.getContraseña()
            )
        );
        
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        // Cargar el UserDetails para obtener las autoridades
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getCorreo());
        
        // Añadir explícitamente el rol en extraClaims
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("rol", usuario.getRol().toString());
        
        // Generar token con las autoridades
        String jwtToken = jwtService.generateToken(extraClaims, userDetails);
        
        // Extraer token y verificar contenido para debugging
        try {
            String tokenClaim = jwtService.extractRole(jwtToken);
            System.out.println("Verificando rol en token generado: " + tokenClaim);
        } catch (Exception e) {
            System.err.println("Error al extraer rol del token: " + e.getMessage());
        }
        
        // Crear y devolver respuesta sin usar el patrón builder
        AuthResponse response = new AuthResponse();
        response.setToken(jwtToken);
        response.setCorreo(usuario.getCorreo());
        response.setNombre(usuario.getNombre());
        response.setRol(usuario.getRol().toString());
        
        return response;
    }
}