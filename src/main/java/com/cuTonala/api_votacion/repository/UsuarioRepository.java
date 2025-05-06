package com.cuTonala.api_votacion.repository;

import com.cuTonala.api_votacion.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByCodigoEstudiante(String codigoEstudiante);

    /**
     * Buscar usuarios por nombre (búsqueda parcial)
     */
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Buscar usuarios por apellidos (búsqueda parcial)
     */
    List<Usuario> findByApellidosContainingIgnoreCase(String apellidos);

    /**
     * Buscar usuarios por rol
     */
    List<Usuario> findByRol(Usuario.Rol rol);

    /**
     * Búsqueda avanzada con JPQL
     */
    @Query("SELECT u FROM Usuario u WHERE " +
           "(:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:apellidos IS NULL OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :apellidos, '%'))) AND " +
           "(:correo IS NULL OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :correo, '%'))) AND " +
           "(:codigoEstudiante IS NULL OR u.codigoEstudiante LIKE CONCAT('%', :codigoEstudiante, '%'))")
    List<Usuario> busquedaAvanzada(
            @Param("nombre") String nombre,
            @Param("apellidos") String apellidos,
            @Param("correo") String correo,
            @Param("codigoEstudiante") String codigoEstudiante);
}