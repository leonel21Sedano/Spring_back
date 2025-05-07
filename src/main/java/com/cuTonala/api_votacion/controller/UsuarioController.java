package com.cuTonala.api_votacion.controller;

import com.cuTonala.api_votacion.model.Usuario;
import com.cuTonala.api_votacion.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "API para gestionar usuarios (requiere rol ADMIN)")
@SecurityRequirement(name = "JWT")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(
        summary = "Obtener todos los usuarios", 
        description = "Devuelve una lista completa con todos los usuarios registrados en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Lista de usuarios recuperada con éxito",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Usuario.class, type = "array")
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = "Acceso denegado - Se requiere rol ADMIN",
                content = @Content(examples = @ExampleObject(value = "{\"error\":\"Acceso denegado\"}"))
            )
    })
    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Operation(
        summary = "Obtener usuario por ID", 
        description = "Obtiene información detallada de un usuario específico por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Usuario encontrado", 
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Usuario.class),
                    examples = @ExampleObject(value = 
                        "{\"id\":1,\"nombre\":\"Admin\",\"apellidos\":\"Sistema\",\"correo\":\"admin@academicos.udg.mx\",\"codigoEstudiante\":null,\"rol\":\"ADMIN\",\"activo\":true}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Usuario no encontrado",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "Acceso denegado - Se requiere rol ADMIN",
                content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(
            @Parameter(description = "ID del usuario a buscar", required = true, example = "1") 
            @PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(
        summary = "Buscar usuario por código de estudiante", 
        description = "Obtiene información de un usuario buscando por su código de estudiante único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Usuario encontrado", 
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Usuario.class)
                )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Usuario no encontrado",
                content = @Content
            )
    })
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Usuario> getUsuarioByCodigo(
            @Parameter(description = "Código del estudiante", required = true, example = "A12345678") 
            @PathVariable String codigo) {
        Optional<Usuario> usuario = usuarioRepository.findByCodigoEstudiante(codigo);
        return usuario.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(
        summary = "Crear nuevo usuario", 
        description = "Crea un nuevo usuario en el sistema. Solo un administrador puede crear usuarios."
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Usuario creado correctamente",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Usuario.class)
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "Datos del usuario inválidos o correo duplicado",
                content = @Content(examples = @ExampleObject(value = "{\"error\":\"El correo ya existe en el sistema\"}"))
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "Acceso denegado - Se requiere rol ADMIN",
                content = @Content
            )
    })
    @PostMapping
    public Usuario createUsuario(
            @Parameter(
                description = "Datos del nuevo usuario a crear", 
                required = true,
                schema = @Schema(implementation = Usuario.class),
                examples = {
                    @ExampleObject(
                        name = "Crear estudiante",
                        value = "{\"nombre\":\"Carlos\",\"apellidos\":\"González\",\"correo\":\"carlos@alumnos.udg.mx\",\"contraseña\":\"password123\",\"codigoEstudiante\":\"A12345678\",\"rol\":\"ESTUDIANTE\",\"activo\":true}"
                    ),
                    @ExampleObject(
                        name = "Crear administrador",
                        value = "{\"nombre\":\"María\",\"apellidos\":\"Rodríguez\",\"correo\":\"maria@academicos.udg.mx\",\"contraseña\":\"admin456\",\"rol\":\"ADMIN\",\"activo\":true}"
                    )
                }
            ) 
            @RequestBody Usuario usuario) {
        // Codificar la contraseña antes de guardar
        if (usuario.getContraseña() != null && !usuario.getContraseña().isEmpty()) {
            usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        }
        return usuarioRepository.save(usuario);
    }

    @Operation(
        summary = "Actualizar usuario", 
        description = "Actualiza los datos de un usuario existente. La contraseña solo se actualiza si se proporciona una nueva."
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Usuario actualizado correctamente",
                content = @Content(schema = @Schema(implementation = Usuario.class))
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Usuario no encontrado",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "Acceso denegado - Se requiere rol ADMIN",
                content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(
            @Parameter(description = "ID del usuario a actualizar", required = true, example = "1") 
            @PathVariable Long id,
            @Parameter(
                description = "Nuevos datos del usuario", 
                required = true,
                schema = @Schema(implementation = Usuario.class),
                examples = @ExampleObject(
                    value = "{\"nombre\":\"Carlos\",\"apellidos\":\"González Actualizado\",\"correo\":\"carlos.nuevo@alumnos.udg.mx\",\"contraseña\":\"nuevaPassword\",\"codigoEstudiante\":\"A12345678\",\"rol\":\"ESTUDIANTE\",\"activo\":true}"
                )
            ) 
            @RequestBody Usuario usuarioDetails) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuarioToUpdate = optionalUsuario.get();
            usuarioToUpdate.setNombre(usuarioDetails.getNombre());
            usuarioToUpdate.setApellidos(usuarioDetails.getApellidos());
            usuarioToUpdate.setCorreo(usuarioDetails.getCorreo());
            // Solo actualiza la contraseña si se proporciona una nueva
            if (usuarioDetails.getContraseña() != null && !usuarioDetails.getContraseña().isEmpty()) {
                usuarioToUpdate.setContraseña(passwordEncoder.encode(usuarioDetails.getContraseña()));
            }
            usuarioToUpdate.setCodigoEstudiante(usuarioDetails.getCodigoEstudiante());
            usuarioToUpdate.setRol(usuarioDetails.getRol());
            usuarioToUpdate.setActivo(usuarioDetails.isActivo());
            return ResponseEntity.ok(usuarioRepository.save(usuarioToUpdate));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Eliminar usuario", 
        description = "Elimina un usuario existente de manera permanente por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "204", 
                description = "Usuario eliminado correctamente - Sin contenido"
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Usuario no encontrado",
                content = @Content(examples = @ExampleObject(value = "{\"error\":\"El usuario con ID especificado no existe\"}"))
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "Acceso denegado - Se requiere rol ADMIN",
                content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(
            @Parameter(description = "ID del usuario a eliminar", required = true, example = "1") 
            @PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            usuarioRepository.delete(usuario.get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}