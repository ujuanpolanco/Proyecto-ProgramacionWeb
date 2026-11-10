package co.edu.usco.khaza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.usco.khaza.model.DetallePedido;

/**
 * Repositorio JPA de las líneas de pedido.
 *
 * @author Juan Manuel Polanco
 */
@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

	/**
	 * @param productoId id del producto
	 * @return true si el producto aparece en algún pedido (ya se vendió)
	 */
	boolean existsByProductoId(Integer productoId);
}
