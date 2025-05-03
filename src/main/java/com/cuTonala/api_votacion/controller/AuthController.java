package com.cuTonala.api_votacion.controller;

import com.cuTonala.api_votacion.model.AuthRequest;
import com.cuTonala.api_votacion.model.AuthResponse;
import com.cuTonala.api_votacion.model.EstudianteRegistroRequest;
import com.cuTonala.api_votacion.model.Usuario;
import com.cuTonala.api_votacion.service.AuthService;
import com.cuTonala.api_votacion.service.UsuarioService;

import jakarta.validation.Valid;

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
    public ResponseEntity<?> registrarEstudiante(@Valid @RequestBody EstudianteRegistroRequest request) {
        Usuario estudiante = usuarioService.registrarEstudiante(request);
        return ResponseEntity.ok().build();
    }
}