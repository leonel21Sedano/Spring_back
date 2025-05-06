package com.cuTonala.api_votacion.service;

import com.cuTonala.api_votacion.model.EstudianteRegistroRequest;
import com.cuTonala.api_votacion.model.Usuario;
import com.cuTonala.api_votacion.model.UsuarioUpdateRequest;
import com.cuTonala.api_votacion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    
    public Usuario actualizarUsuarioPorCodigo(String codigoEstudiante, UsuarioUpdateRequest request) {
        // Verificar si el usuario existe
        Usuario usuario = usuarioRepository.findByCodigoEstudiante(codigoEstudiante)
                .orElseThrow(() -> new RuntimeException("No se encontró un usuario con el código " + codigoEstudiante));
        
        // Verificar si el correo ya existe y no es del mismo usuario
        if (!usuario.getCorreo().equals(request.getCorreo()) && 
                usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo " + request.getCorreo() + " ya está registrado para otro usuario");
        }
        
        // Actualizar datos
        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());
        usuario.setCorreo(request.getCorreo());
        
        // Actualizar contraseña solo si se solicita
        if (request.isActualizarContraseña() && request.getContraseña() != null && !request.getContraseña().trim().isEmpty()) {
            usuario.setContraseña(passwordEncoder.encode(request.getContraseña()));
        }
        
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Buscar usuarios por diferentes criterios
     */
    public List<Usuario> buscarUsuarios(String nombre, String apellidos, String correo, 
                                         String codigoEstudiante, String rol) {
        List<Usuario> todosUsuarios = usuarioRepository.findAll();
        
        // Aplicar filtros si se proporcionaron
        Stream<Usuario> stream = todosUsuarios.stream();
        
        if (nombre != null && !nombre.trim().isEmpty()) {
            stream = stream.filter(u -> u.getNombre().toLowerCase()
                    .contains(nombre.toLowerCase()));
        }
        
        if (apellidos != null && !apellidos.trim().isEmpty()) {
            stream = stream.filter(u -> u.getApellidos().toLowerCase()
                    .contains(apellidos.toLowerCase()));
        }
        
        if (correo != null && !correo.trim().isEmpty()) {
            stream = stream.filter(u -> u.getCorreo().toLowerCase()
                    .contains(correo.toLowerCase()));
        }
        
        if (codigoEstudiante != null && !codigoEstudiante.trim().isEmpty()) {
            stream = stream.filter(u -> u.getCodigoEstudiante() != null && 
                    u.getCodigoEstudiante().contains(codigoEstudiante));
        }
        
        if (rol != null && !rol.trim().isEmpty()) {
            try {
                Usuario.Rol rolEnum = Usuario.Rol.valueOf(rol.toUpperCase());
                stream = stream.filter(u -> u.getRol() == rolEnum);
            } catch (IllegalArgumentException e) {
                // Ignorar filtro de rol si no es un valor válido
            }
        }
        
        return stream.collect(Collectors.toList());
    }
    
    /**
     * Método de búsqueda avanzada con múltiples criterios y filtro de usuarios activos/inactivos
     */
    public List<Usuario> buscarUsuariosAvanzado(String nombre, String apellidos, String correo, 
                                       String codigoEstudiante, String rol, boolean soloActivos) {
        List<Usuario> todosUsuarios = usuarioRepository.findAll();
        
        // Aplicar filtros si se proporcionaron
        Stream<Usuario> stream = todosUsuarios.stream();
        
        if (nombre != null && !nombre.trim().isEmpty()) {
            stream = stream.filter(u -> u.getNombre() != null && 
                    u.getNombre().toLowerCase().contains(nombre.toLowerCase()));
        }
        
        if (apellidos != null && !apellidos.trim().isEmpty()) {
            stream = stream.filter(u -> u.getApellidos() != null && 
                    u.getApellidos().toLowerCase().contains(apellidos.toLowerCase()));
        }
        
        if (correo != null && !correo.trim().isEmpty()) {
            stream = stream.filter(u -> u.getCorreo() != null && 
                    u.getCorreo().toLowerCase().contains(correo.toLowerCase()));
        }
        
        if (codigoEstudiante != null && !codigoEstudiante.trim().isEmpty()) {
            stream = stream.filter(u -> u.getCodigoEstudiante() != null && 
                    u.getCodigoEstudiante().contains(codigoEstudiante));
        }
        
        if (rol != null && !rol.trim().isEmpty()) {
            try {
                Usuario.Rol rolEnum = Usuario.Rol.valueOf(rol.toUpperCase());
                stream = stream.filter(u -> u.getRol() == rolEnum);
            } catch (IllegalArgumentException e) {
                // Ignorar filtro de rol si no es un valor válido
            }
        }
        
        // Filtrar por estado activo/inactivo
        if (soloActivos) {
            stream = stream.filter(Usuario::isActivo);
        }
        
        return stream.collect(Collectors.toList());
    }
    
    /**
     * Versión mejorada de la búsqueda flexible
     * Optimizada para encontrar resultados más relevantes primero
     */
    public List<Usuario> busquedaFlexible(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Limpiar y normalizar el término de búsqueda
        String terminoLimpio = termino.trim().toLowerCase();
        
        // Lista para guardar resultados en orden de relevancia
        List<Usuario> resultados = new ArrayList<>();
        Map<Long, Boolean> usuariosAgregados = new HashMap<>();
        
        // 1. Primero buscar coincidencias exactas (prioridad máxima)
        Optional<Usuario> porCodigo = usuarioRepository.findByCodigoEstudiante(terminoLimpio);
        if (porCodigo.isPresent()) {
            resultados.add(porCodigo.get());
            usuariosAgregados.put(porCodigo.get().getId(), true);
        }
        
        Optional<Usuario> porCorreo = usuarioRepository.findByCorreo(terminoLimpio);
        if (porCorreo.isPresent() && !usuariosAgregados.containsKey(porCorreo.get().getId())) {
            resultados.add(porCorreo.get());
            usuariosAgregados.put(porCorreo.get().getId(), true);
        }
        
        // 2. Buscar coincidencias parciales por nombre, apellidos, correo y código
        List<Usuario> todos = usuarioRepository.findAll();
        
        // Coincidencias parciales por código (prioridad alta)
        todos.stream()
            .filter(u -> u.getCodigoEstudiante() != null && 
                    u.getCodigoEstudiante().contains(terminoLimpio) && 
                    !usuariosAgregados.containsKey(u.getId()))
            .forEach(u -> {
                resultados.add(u);
                usuariosAgregados.put(u.getId(), true);
            });
        
        // Coincidencias por correo (prioridad alta)
        todos.stream()
            .filter(u -> u.getCorreo() != null && 
                    u.getCorreo().toLowerCase().contains(terminoLimpio) && 
                    !usuariosAgregados.containsKey(u.getId()))
            .forEach(u -> {
                resultados.add(u);
                usuariosAgregados.put(u.getId(), true);
            });
        
        // Coincidencias por nombre completo (nombre + apellidos)
        todos.stream()
            .filter(u -> {
                if (u.getNombre() == null || u.getApellidos() == null) return false;
                String nombreCompleto = (u.getNombre() + " " + u.getApellidos()).toLowerCase();
                return nombreCompleto.contains(terminoLimpio) && !usuariosAgregados.containsKey(u.getId());
            })
            .forEach(u -> {
                resultados.add(u);
                usuariosAgregados.put(u.getId(), true);
            });
        
        // Coincidencias solo por nombre o solo por apellidos (prioridad más baja)
        todos.stream()
            .filter(u -> 
                (u.getNombre() != null && u.getNombre().toLowerCase().contains(terminoLimpio) || 
                 u.getApellidos() != null && u.getApellidos().toLowerCase().contains(terminoLimpio)) && 
                !usuariosAgregados.containsKey(u.getId())
            )
            .forEach(u -> resultados.add(u));
        
        return resultados;
    }
    
    /**
     * Método para gestionar la creación de un usuario desde el frontend
     * @param usuario Datos del nuevo usuario
     * @return Usuario creado
     */
    public Usuario crearUsuario(Usuario usuario) {
        // Verificar si el correo ya existe
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }
        
        // Verificar si el código de estudiante ya existe (si se proporciona)
        if (usuario.getCodigoEstudiante() != null && !usuario.getCodigoEstudiante().trim().isEmpty() && 
            usuarioRepository.findByCodigoEstudiante(usuario.getCodigoEstudiante()).isPresent()) {
            throw new RuntimeException("El código de estudiante ya está registrado");
        }
        
        // Encriptar la contraseña
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        
        // Si no se especifica un rol, asignar ESTUDIANTE por defecto
        if (usuario.getRol() == null) {
            usuario.setRol(Usuario.Rol.ESTUDIANTE);
        }
        
        // Si no se especifica estado activo, ponerlo como activo por defecto
        usuario.setActivo(true);
        
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Método para actualizar un usuario (compatible con el frontend)
     * @param id ID del usuario a actualizar
     * @param usuarioDetails Datos actualizados
     * @return Usuario actualizado
     */
    public Usuario actualizarUsuario(Long id, Usuario usuarioDetails) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró el usuario con ID: " + id));
        
        // Verificar si el correo nuevo ya existe y no es del mismo usuario
        if (!usuario.getCorreo().equals(usuarioDetails.getCorreo()) &&
            usuarioRepository.findByCorreo(usuarioDetails.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado para otro usuario");
        }
        
        // Verificar si el código nuevo ya existe y no es del mismo usuario
        if (usuarioDetails.getCodigoEstudiante() != null && !usuarioDetails.getCodigoEstudiante().trim().isEmpty() &&
            !usuarioDetails.getCodigoEstudiante().equals(usuario.getCodigoEstudiante()) &&
            usuarioRepository.findByCodigoEstudiante(usuarioDetails.getCodigoEstudiante()).isPresent()) {
            throw new RuntimeException("El código de estudiante ya está registrado para otro usuario");
        }
        
        // Actualizar datos básicos
        usuario.setNombre(usuarioDetails.getNombre());
        usuario.setApellidos(usuarioDetails.getApellidos());
        usuario.setCorreo(usuarioDetails.getCorreo());
        
        // Actualizar código de estudiante si se proporciona
        if (usuarioDetails.getCodigoEstudiante() != null) {
            usuario.setCodigoEstudiante(usuarioDetails.getCodigoEstudiante());
        }
        
        // Actualizar rol si se proporciona
        if (usuarioDetails.getRol() != null) {
            usuario.setRol(usuarioDetails.getRol());
        }
        
        // Actualizar estado activo si se especifica
        if (usuarioDetails.isActivo() != usuario.isActivo()) {
            usuario.setActivo(usuarioDetails.isActivo());
        }
        
        // Actualizar contraseña solo si se proporciona una nueva
        if (usuarioDetails.getContraseña() != null && !usuarioDetails.getContraseña().trim().isEmpty()) {
            usuario.setContraseña(passwordEncoder.encode(usuarioDetails.getContraseña()));
        }
        
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Método para eliminar un usuario
     * @param id ID del usuario a eliminar
     * @return true si el usuario fue eliminado, false si no existía
     */
    public boolean eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Método para cambiar el estado activo/inactivo de un usuario
     * @param id ID del usuario
     * @param activo Nuevo estado (true=activo, false=inactivo)
     * @return Usuario actualizado
     */
    public Usuario cambiarEstadoUsuario(Long id, boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró el usuario con ID: " + id));
        
        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Método para cambiar la contraseña de un usuario
     * @param id ID del usuario
     * @param contraseñaActual Contraseña actual (para verificación)
     * @param nuevaContraseña Nueva contraseña
     * @return Usuario actualizado
     */
    public Usuario cambiarContraseña(Long id, String contraseñaActual, String nuevaContraseña) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró el usuario con ID: " + id));
        
        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(contraseñaActual, usuario.getContraseña())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        
        // Actualizar a la nueva contraseña
        usuario.setContraseña(passwordEncoder.encode(nuevaContraseña));
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Método para obtener un usuario por su ID (usado por el frontend)
     * @param id ID del usuario
     * @return Usuario encontrado
     */
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró el usuario con ID: " + id));
    }
    
    /**
     * Método para obtener todos los usuarios con paginación y ordenamiento
     * @param pagina Número de página (0-based)
     * @param tamaño Tamaño de página
     * @param ordenarPor Campo para ordenar
     * @param direccion Dirección del ordenamiento (asc/desc)
     * @return Lista de usuarios paginada
     */
    public Map<String, Object> obtenerTodosUsuariosPaginados(int pagina, int tamaño, String ordenarPor, String direccion) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        
        // Aplicar ordenamiento
        if (ordenarPor != null && !ordenarPor.isEmpty()) {
            Comparator<Usuario> comparator = null;
            
            switch (ordenarPor.toLowerCase()) {
                case "nombre":
                    comparator = Comparator.comparing(Usuario::getNombre, Comparator.nullsLast(String::compareTo));
                    break;
                case "apellidos":
                    comparator = Comparator.comparing(Usuario::getApellidos, Comparator.nullsLast(String::compareTo));
                    break;
                case "correo":
                    comparator = Comparator.comparing(Usuario::getCorreo, Comparator.nullsLast(String::compareTo));
                    break;
                case "rol":
                    comparator = Comparator.comparing(u -> u.getRol().toString(), Comparator.nullsLast(String::compareTo));
                    break;
                case "activo":
                    comparator = Comparator.comparing(Usuario::isActivo);
                    break;
                default:
                    comparator = Comparator.comparing(Usuario::getId);
            }
            
            // Invertir si la dirección es descendente
            if (direccion != null && direccion.equalsIgnoreCase("desc")) {
                comparator = comparator.reversed();
            }
            
            usuarios.sort(comparator);
        }
        
        // Aplicar paginación
        int total = usuarios.size();
        int inicio = pagina * tamaño;
        int fin = Math.min(inicio + tamaño, total);
        
        List<Usuario> usuariosPaginados = new ArrayList<>();
        if (inicio < total) {
            usuariosPaginados = usuarios.subList(inicio, fin);
        }
        
        // Crear respuesta paginada
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("usuarios", usuariosPaginados);
        respuesta.put("paginaActual", pagina);
        respuesta.put("totalElementos", total);
        respuesta.put("totalPaginas", (total + tamaño - 1) / tamaño);
        
        return respuesta;
    }
}
