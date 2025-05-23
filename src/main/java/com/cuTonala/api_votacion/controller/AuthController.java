package com.cuTonala.api_votacion.controller;

import com.cuTonala.api_votacion.model.AuthRequest;
import com.cuTonala.api_votacion.model.AuthResponse;
import com.cuTonala.api_votacion.model.EstudianteRegistroRequest;
import com.cuTonala.api_votacion.model.Usuario;
import com.cuTonala.api_votacion.service.AuthService;
import com.cuTonala.api_votacion.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Operaciones de autenticación y registro de usuarios")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UsuarioService usuarioService;

    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica a un usuario con su correo y contraseña, y devuelve un token JWT para usar en futuras peticiones"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa", 
                   content = @Content(mediaType = "application/json", 
                   schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Credenciales inválidas",
                   content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
    
    @Operation(
        summary = "Registrar estudiante",
        description = "Registra un nuevo usuario con rol ESTUDIANTE en el sistema"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Estudiante registrado correctamente",
                   content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existente",
                   content = @Content(mediaType = "application/json"))
    })
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