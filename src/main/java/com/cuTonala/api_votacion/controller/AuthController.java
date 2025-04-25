package com.cuTonala.api_votacion.controller;

import com.cuTonala.api_votacion.model.AuthRequest;
import com.cuTonala.api_votacion.model.AuthResponse;
import com.cuTonala.api_votacion.model.EstudianteRegistroRequest;
import com.cuTonala.api_votacion.model.Usuario;
import com.cuTonala.api_votacion.service.AuthService;
import com.cuTonala.api_votacion.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
    
    @PostMapping("/registro/estudiante")
    public ResponseEntity<?> registrarEstudiante(@RequestBody EstudianteRegistroRequest request) {
        // Validar que el correo tenga dominio de la universidad
        if (!request.getCorreo().endsWith("@alumnos.udg.mx") && 
            !request.getCorreo().endsWith("@academicos.udg.mx")) {
            return ResponseEntity.badRequest().body("El correo debe ser institucional");
        }
        
        // Validar formato del código de estudiante
        if (request.getCodigoEstudiante().length() != 9) {
            return ResponseEntity.badRequest().body("El código de estudiante debe tener 9 dígitos");
        }
        
        try {
            Usuario estudiante = usuarioService.registrarEstudiante(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}