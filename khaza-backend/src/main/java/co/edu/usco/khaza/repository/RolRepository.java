package co.edu.usco.khaza.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.usco.khaza.model.Rol;

/**
 * Repositorio JPA de roles.
 *
 * @author Juan Manuel Polanco
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

	/**
	 * @param nombre "ADMIN" o "CLIENTE"
	 * @return el rol con ese nombre
	 */
	Optional<Rol> findByNombre(String nombre);
}
