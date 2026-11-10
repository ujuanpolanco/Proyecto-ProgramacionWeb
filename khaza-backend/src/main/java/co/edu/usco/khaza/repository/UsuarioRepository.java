package co.edu.usco.khaza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.usco.khaza.model.Usuario;

/**
 * Repositorio JPA de usuarios.
 *
 * @author Juan Manuel Polanco
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

}
