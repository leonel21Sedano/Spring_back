package com.cuTonala.api_votacion.service;

import com.cuTonala.api_votacion.model.Usuario;
import com.cuTonala.api_votacion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        System.out.println("DEBUG - loadUserByUsername: " + correo);
        
        Usuario usuario;
        try {
            usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));
        } catch (Exception e) {
            System.err.println("DEBUG - Error buscando usuario: " + e.getMessage());
            throw e;
        }
        
        // Añadir el rol como autoridad
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(usuario.getRol().toString()));
        
        System.out.println("DEBUG - Usuario encontrado: " + usuario.getCorreo());
        System.out.println("DEBUG - Rol: " + usuario.getRol().toString());
        
        return new User(usuario.getCorreo(), usuario.getContraseña(), authorities);
    }
}
