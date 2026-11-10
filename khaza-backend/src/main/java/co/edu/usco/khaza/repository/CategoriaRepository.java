package co.edu.usco.khaza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.usco.khaza.model.Categoria;

/**
 * Repositorio JPA de categorías. Spring genera solo findAll, findById, save,
 * deleteById... y también los métodos cuyo nombre sigue la convención.
 *
 * @author Juan Manuel Polanco
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

	/**
	 * @param nombre nombre de la categoría (sin importar mayúsculas)
	 * @return true si ya existe una categoría con ese nombre
	 */
	boolean existsByNombreIgnoreCase(String nombre);
}
