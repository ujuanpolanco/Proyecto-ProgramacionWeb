package co.edu.usco.khaza.repository;

import java.util.Optional;

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

	/**
	 * @param email correo del usuario
	 * @return el usuario con ese correo, si existe
	 */
	Optional<Usuario> findByEmail(String email);

	/**
	 * @param email correo a revisar
	 * @return true si ya hay una cuenta con ese correo
	 */
	boolean existsByEmail(String email);
}
