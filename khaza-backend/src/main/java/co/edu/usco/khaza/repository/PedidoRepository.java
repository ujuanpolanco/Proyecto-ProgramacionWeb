package co.edu.usco.khaza.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.usco.khaza.model.Pedido;

/**
 * Repositorio JPA de pedidos.
 *
 * @author Juan Manuel Polanco
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

	/**
	 * Spring arma la consulta a partir del nombre del método:
	 * SELECT ... WHERE usuario_id = ? ORDER BY fecha DESC
	 *
	 * @param usuarioId id del cliente
	 * @return los pedidos del cliente, del más reciente al más antiguo
	 */
	List<Pedido> findByUsuarioIdOrderByFechaDesc(Integer usuarioId);

	/**
	 * @return todos los pedidos, del más reciente al más antiguo
	 */
	List<Pedido> findAllByOrderByFechaDesc();
}
