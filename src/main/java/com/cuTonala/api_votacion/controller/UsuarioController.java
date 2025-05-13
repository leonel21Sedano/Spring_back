package com.cuTonala.api_votacion.controller;

import com.cuTonala.api_votacion.model.Usuario;
import com.cuTonala.api_votacion.model.UsuarioUpdateRequest;
import com.cuTonala.api_votacion.repository.UsuarioRepository;
import com.cuTonala.api_votacion.service.UsuarioService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestión de usuarios")
@SecurityRequirement(name = "Bearer Authentication")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private UsuarioService usuarioService;

    @Operation(
        summary = "Obtener todos los usuarios",
        description = "Retorna una lista con todos los usuarios registrados en el sistema"
    )
    @ApiResponse(responseCode = "200", description = "Lista de usuarios recuperada correctamente")
    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioService.buscarUsuarios(null, null, null, null, null);
    }

    @Operation(summary = "Obtener un usuario por su ID", 
              description = "Retorna los datos completos de un usuario según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                   content = @Content(mediaType = "application/json", 
                   schema = @Schema(implementation = Usuario.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createUsuario(@Valid @RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Usuario creado correctamente");
            response.put("usuario", nuevoUsuario);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuarioById(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuarioById(@PathVariable Long id) {
        try {
            boolean eliminado = usuarioService.eliminarUsuario(id);
            if (eliminado) {
                Map<String, String> mensaje = new HashMap<>();
                mensaje.put("mensaje", "Usuario eliminado con éxito");
                return ResponseEntity.ok(mensaje);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al eliminar: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstadoUsuario(
            @PathVariable Long id, 
            @RequestParam boolean activo) {
        try {
            Usuario usuario = usuarioService.cambiarEstadoUsuario(id, activo);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/cambiarPassword")
    public ResponseEntity<?> cambiarContraseña(
            @PathVariable Long id,
            @RequestParam String contraseñaActual,
            @RequestParam String nuevaContraseña) {
        try {
            Usuario usuario = usuarioService.cambiarContraseña(id, contraseñaActual, nuevaContraseña);
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Contraseña actualizada con éxito");
            return ResponseEntity.ok(respuesta);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/codigo/{codigoEstudiante}")
    public ResponseEntity<?> getUsuarioByCodigo(@PathVariable String codigoEstudiante) {
        Optional<Usuario> usuario = usuarioRepository.findByCodigoEstudiante(codigoEstudiante);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se encontró un usuario con el código: " + codigoEstudiante);
            response.put("sugerencia", "Verifique el código o intente buscar por correo electrónico");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/busqueda-flexible")
    public ResponseEntity<?> busquedaFlexible(
            @RequestParam(required = false) String termino) {
        
        if (termino == null || termino.trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Por favor ingrese un término de búsqueda (código, correo o nombre)");
            return ResponseEntity.badRequest().body(response);
        }
        
        List<Usuario> resultados = usuarioService.busquedaFlexible(termino);
        
        if (resultados.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se encontraron usuarios con el término: " + termino);
            response.put("sugerencia", "Intente con otro término o verifique si el usuario está registrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<?> getUsuarioByCorreo(@PathVariable String correo) {
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(correo);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "No se encontró un usuario con el correo: " + correo);
            response.put("sugerencia", "Verifique el correo o intente buscar por código de estudiante");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/codigo/{codigoEstudiante}")
    public ResponseEntity<?> updateUsuarioByCodigo(
            @PathVariable String codigoEstudiante, 
            @Valid @RequestBody UsuarioUpdateRequest request) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarUsuarioPorCodigo(codigoEstudiante, request);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Usuario>> buscarUsuarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellidos,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String codigoEstudiante,
            @RequestParam(required = false) String rol) {
        
        List<Usuario> usuarios = usuarioService.buscarUsuarios(nombre, apellidos, correo, codigoEstudiante, rol);
        
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(usuarios);
    }
    
    @GetMapping("/verificar-codigo/{codigoEstudiante}")
    public ResponseEntity<Boolean> verificarCodigoEstudiante(@PathVariable String codigoEstudiante) {
        boolean existe = usuarioRepository.findByCodigoEstudiante(codigoEstudiante).isPresent();
        return ResponseEntity.ok(existe);
    }
    
    @GetMapping("/verificar-correo/{correo}")
    public ResponseEntity<Boolean> verificarCorreo(@PathVariable String correo) {
        boolean existe = usuarioRepository.findByCorreo(correo).isPresent();
        return ResponseEntity.ok(existe);
    }

    @GetMapping("/busqueda-publica")
    public ResponseEntity<?> busquedaPublica(
            @RequestParam(required = false) String termino,
            @RequestParam(required = false, defaultValue = "10") int limite) {
        
        if (termino == null || termino.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("mensaje", "Por favor ingrese un término de búsqueda");
            response.put("sugerencia", "Puede buscar por código, nombre, apellidos o correo");
            return ResponseEntity.badRequest().body(response);
        }
        
        List<Usuario> resultados = usuarioService.busquedaFlexible(termino);
        
        if (resultados.size() > limite) {
            resultados = resultados.subList(0, limite);
        }
        
        if (resultados.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "empty");
            response.put("mensaje", "No se encontraron usuarios con: " + termino);
            response.put("sugerencia", "Intente con otro término de búsqueda");
            return ResponseEntity.ok(response);
        }
        
        List<Map<String, Object>> usuariosFormateados = resultados.stream()
            .map(u -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", u.getId());
                map.put("nombre", u.getNombre());
                map.put("apellidos", u.getApellidos());
                map.put("correo", u.getCorreo());
                map.put("codigoEstudiante", u.getCodigoEstudiante());
                map.put("rol", u.getRol().toString());
                map.put("activo", u.isActivo());
                return map;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("resultados", usuariosFormateados);
        response.put("total", resultados.size());
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(
        summary = "Buscar usuarios con criterios avanzados",
        description = "Permite buscar usuarios con múltiples criterios de filtrado"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Búsqueda exitosa"),
        @ApiResponse(responseCode = "204", description = "No se encontraron resultados")
    })
    @GetMapping("/busqueda-avanzada")
    public ResponseEntity<?> busquedaAvanzada(
            @Parameter(description = "Filtrar por nombre") @RequestParam(required = false) String nombre,
            @Parameter(description = "Filtrar por apellidos") @RequestParam(required = false) String apellidos,
            @Parameter(description = "Filtrar por correo electrónico") @RequestParam(required = false) String correo,
            @Parameter(description = "Filtrar por código de estudiante") @RequestParam(required = false) String codigoEstudiante,
            @Parameter(description = "Filtrar por rol (ADMIN, ENCARGADO, ESTUDIANTE)") @RequestParam(required = false) String rol,
            @Parameter(description = "Mostrar solo usuarios activos") @RequestParam(required = false, defaultValue = "true") boolean soloActivos) {
        
        List<Usuario> usuarios = usuarioService.buscarUsuariosAvanzado(
                nombre, apellidos, correo, codigoEstudiante, rol, soloActivos);
        
        if (usuarios.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "empty");
            response.put("mensaje", "No se encontraron usuarios con los criterios especificados");
            return ResponseEntity.ok(response);
        }
        
        List<Map<String, Object>> usuariosFormateados = usuarios.stream()
            .map(u -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", u.getId());
                map.put("nombre", u.getNombre());
                map.put("apellidos", u.getApellidos());
                map.put("nombreCompleto", u.getNombre() + " " + u.getApellidos());
                map.put("correo", u.getCorreo());
                map.put("codigoEstudiante", u.getCodigoEstudiante());
                map.put("rol", u.getRol().toString());
                map.put("activo", u.isActivo());
                return map;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("resultados", usuariosFormateados);
        response.put("total", usuarios.size());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginados")
    public ResponseEntity<Map<String, Object>> getUsuariosPaginados(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamaño,
            @RequestParam(required = false) String ordenarPor,
            @RequestParam(defaultValue = "asc") String direccion) {
        
        Map<String, Object> respuesta = usuarioService.obtenerTodosUsuariosPaginados(
                pagina, tamaño, ordenarPor, direccion);
        
        return ResponseEntity.ok(respuesta);
    }
}