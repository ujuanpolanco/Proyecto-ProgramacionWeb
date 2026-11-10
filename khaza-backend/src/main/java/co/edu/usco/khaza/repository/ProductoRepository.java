package co.edu.usco.khaza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.usco.khaza.model.Producto;

/**
 * Repositorio JPA de productos.
 *
 * @author Juan Manuel Polanco
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

	/**
	 * @param categoriaId id de la categoría
	 * @return true si hay algún producto en esa categoría
	 */
	boolean existsByCategoriaId(Integer categoriaId);
}
