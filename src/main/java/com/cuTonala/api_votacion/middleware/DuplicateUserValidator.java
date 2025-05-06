package com.cuTonala.api_votacion.middleware;

import com.cuTonala.api_votacion.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class DuplicateUserValidator implements HandlerInterceptor {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Aseguramos que podamos leer el cuerpo múltiples veces
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        
        // Solo validamos en las rutas de registro y creación de usuarios
        String requestURI = wrappedRequest.getRequestURI();
        String method = wrappedRequest.getMethod();
        
        if ((requestURI.matches("/api/auth/registro/.*") || requestURI.equals("/api/usuarios/")) && method.equals("POST")) {
            return validateNoDuplicateUser(wrappedRequest, response);
        }
        
        // Para actualización de usuarios, validamos solo si el correo o código cambian
        if (requestURI.matches("/api/usuarios/\\d+") && method.equals("PUT")) {
            return validateUpdateUser(wrappedRequest, response);
        }
        
        return true;
    }
    
    private boolean validateNoDuplicateUser(ContentCachingRequestWrapper request, HttpServletResponse response) throws IOException {
        // Leer el cuerpo de la solicitud
        String body = getRequestBody(request);
        
        if (body.isEmpty()) {
            return true;
        }
        
        try {
            JsonNode jsonNode = objectMapper.readTree(body);
            
            // Extraer correo y código de estudiante
            String correo = jsonNode.has("correo") ? jsonNode.get("correo").asText() : null;
            String codigoEstudiante = jsonNode.has("codigoEstudiante") ? jsonNode.get("codigoEstudiante").asText() : null;
            
            Map<String, String> errors = checkDuplicates(correo, codigoEstudiante);
            
            if (!errors.isEmpty()) {
                // Si hay errores, enviamos una respuesta con status 400
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(objectMapper.writeValueAsString(errors));
                return false;
            }
        } catch (Exception e) {
            // Si hay un error al parsear el JSON, continuamos con la solicitud
            // El controlador se encargará de validar el formato
            return true;
        }
        
        return true;
    }
    
    private boolean validateUpdateUser(ContentCachingRequestWrapper request, HttpServletResponse response) throws IOException {
        // Obtener el ID del usuario a actualizar
        String pathId = request.getRequestURI().substring(request.getRequestURI().lastIndexOf('/') + 1);
        Long userId;
        
        try {
            userId = Long.parseLong(pathId);
        } catch (NumberFormatException e) {
            // Si el ID no es un número válido, continuamos con la solicitud
            return true;
        }
        
        // Leer el cuerpo de la solicitud
        String body = getRequestBody(request);
        
        if (body.isEmpty()) {
            return true;
        }
        
        try {
            JsonNode jsonNode = objectMapper.readTree(body);
            
            // Extraer correo y código de estudiante
            String correo = jsonNode.has("correo") ? jsonNode.get("correo").asText() : null;
            String codigoEstudiante = jsonNode.has("codigoEstudiante") ? jsonNode.get("codigoEstudiante").asText() : null;
            
            Map<String, String> errors = checkDuplicatesExcludingUser(correo, codigoEstudiante, userId);
            
            if (!errors.isEmpty()) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(objectMapper.writeValueAsString(errors));
                return false;
            }
        } catch (Exception e) {
            // Si hay un error al parsear el JSON, continuamos con la solicitud
            return true;
        }
        
        return true;
    }
    
    private Map<String, String> checkDuplicates(String correo, String codigoEstudiante) {
        Map<String, String> errors = new HashMap<>();
        
        if (correo != null && !correo.isEmpty() && usuarioRepository.findByCorreo(correo).isPresent()) {
            errors.put("correo", "El correo " + correo + " ya está registrado en el sistema");
        }
        
        if (codigoEstudiante != null && !codigoEstudiante.isEmpty() && 
                usuarioRepository.findByCodigoEstudiante(codigoEstudiante).isPresent()) {
            errors.put("codigoEstudiante", "El código de estudiante " + codigoEstudiante + " ya está registrado en el sistema");
        }
        
        return errors;
    }
    
    private Map<String, String> checkDuplicatesExcludingUser(String correo, String codigoEstudiante, Long userId) {
        Map<String, String> errors = new HashMap<>();
        
        if (correo != null && !correo.isEmpty()) {
            usuarioRepository.findByCorreo(correo).ifPresent(usuario -> {
                if (!usuario.getId().equals(userId)) {
                    errors.put("correo", "El correo " + correo + " ya está registrado para otro usuario");
                }
            });
        }
        
        if (codigoEstudiante != null && !codigoEstudiante.isEmpty()) {
            usuarioRepository.findByCodigoEstudiante(codigoEstudiante).ifPresent(usuario -> {
                if (!usuario.getId().equals(userId)) {
                    errors.put("codigoEstudiante", "El código de estudiante " + codigoEstudiante + 
                               " ya está registrado para otro usuario");
                }
            });
        }
        
        return errors;
    }
    
    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            return new String(content, StandardCharsets.UTF_8);
        }
        return "";
    }
}

@Configuration
class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private DuplicateUserValidator duplicateUserValidator;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(duplicateUserValidator);
    }
}
