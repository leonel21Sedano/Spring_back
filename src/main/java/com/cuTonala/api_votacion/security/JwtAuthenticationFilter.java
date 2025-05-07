package com.cuTonala.api_votacion.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        System.out.println("URL solicitada: " + request.getRequestURI());
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);
        
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            
            if (jwtService.validateToken(jwt, userDetails)) {
                // Extraer el rol directamente del token (nuevo método)
                String rol = null;
                try {
                    rol = jwtService.extractRole(jwt);
                    System.out.println("Rol extraído del token: " + rol);
                } catch (Exception e) {
                    System.err.println("No se pudo extraer el rol del token: " + e.getMessage());
                }
                
                // Crear una lista de autoridades basada en el rol del token
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (rol != null && !rol.isEmpty()) {
                    authorities.add(new SimpleGrantedAuthority(rol));
                    // También agregar con prefijo ROLE_ para compatibilidad total
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + rol));
                    System.out.println("Autoridades asignadas desde token: " + authorities);
                }
                
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    authorities.isEmpty() ? userDetails.getAuthorities() : authorities
                );
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                System.out.println("Autoridades finales: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
