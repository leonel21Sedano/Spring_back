package com.cuTonala.api_votacion.controller;

import com.cuTonala.api_votacion.model.AuthRequest;
import com.cuTonala.api_votacion.model.AuthResponse;
import com.cuTonala.api_votacion.model.EstudianteRegistroRequest;
import com.cuTonala.api_votacion.model.Usuario;
import com.cuTonala.api_votacion.service.AuthService;
import com.cuTonala.api_votacion.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "API para autenticación y registro de usuarios en el sistema de votación")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UsuarioService usuarioService;

    @Operation(
        summary = "Iniciar sesión", 
        description = "Autentica al usuario utilizando correo y contraseña, devolviendo un token JWT válido para acceder a recursos protegidos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Autenticación exitosa. Se devuelve token JWT y datos básicos del usuario", 
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponse.class),
                    examples = {
                        @ExampleObject(
                            name = "Respuesta de login exitoso",
                            value = "{\"token\":\"eyJhbGciOiJIUzI1NiJ9...\",\"correo\":\"estudiante@alumnos.udg.mx\",\"nombre\":\"Juan\",\"rol\":\"ESTUDIANTE\"}"
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "Credenciales inválidas - Usuario no encontrado o contraseña incorrecta",
                content = @Content(examples = @ExampleObject(value = "{\"mensaje\":\"Usuario o contraseña incorrectos\"}"))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Solicitud inválida - Datos incompletos o formato incorrecto",
                content = @Content
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Parameter(
                description = "Credenciales de autenticación (correo y contraseña)", 
                required = true,
                schema = @Schema(implementation = AuthRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Credenciales de estudiante",
                        value = "{\"correo\":\"estudiante@alumnos.udg.mx\",\"contraseña\":\"password123\"}"
                    ),
                    @ExampleObject(
                        name = "Credenciales de administrador",
                        value = "{\"correo\":\"admin@academicos.udg.mx\",\"contraseña\":\"admin123\"}"
                    )
                }
            ) 
            @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
    
    @Operation(
        summary = "Registrar estudiante", 
        description = "Registra un nuevo usuario con rol ESTUDIANTE en el sistema de votación"
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Registro exitoso - El estudiante ha sido creado correctamente"
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "Datos de registro inválidos o usuario ya existente",
                content = @Content(examples = {
                    @ExampleObject(
                        name = "Error por correo duplicado",
                        value = "{\"error\":\"El correo ya está registrado en el sistema\"}"
                    ),
                    @ExampleObject(
                        name = "Error por código duplicado",
                        value = "{\"error\":\"El código de estudiante ya está registrado\"}"
                    )
                })
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor",
                content = @Content
            )
    })
    @PostMapping("/registro/estudiante")
    public ResponseEntity<?> registrarEstudiante(
            @Parameter(
                description = "Datos de registro del estudiante (nombre, apellidos, correo, etc)", 
                required = true,
                schema = @Schema(implementation = EstudianteRegistroRequest.class),
                example = "{\"nombre\":\"María\",\"apellidos\":\"López Gómez\",\"correo\":\"maria.lopez@alumnos.udg.mx\",\"contraseña\":\"secreta123\",\"codigoEstudiante\":\"A12345789\"}"
            ) 
            @Valid @RequestBody EstudianteRegistroRequest request) {
        Usuario estudiante = usuarioService.registrarEstudiante(request);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Estudiante registrado correctamente");
        return ResponseEntity.ok().body(response);
    }
}