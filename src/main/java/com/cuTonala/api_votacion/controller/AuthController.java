package com.cuTonala.api_votacion.controller;

import com.cuTonala.api_votacion.model.AuthRequest;
import com.cuTonala.api_votacion.model.AuthResponse;
import com.cuTonala.api_votacion.model.EstudianteRegistroRequest;
import com.cuTonala.api_votacion.model.Usuario;
import com.cuTonala.api_votacion.service.AuthService;
import com.cuTonala.api_votacion.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<?> registrarEstudiante(@Valid @RequestBody EstudianteRegistroRequest request) {
        try {
            Usuario estudiante = usuarioService.registrarEstudiante(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Estudiante registrado correctamente");
            response.put("usuario", estudiante);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}