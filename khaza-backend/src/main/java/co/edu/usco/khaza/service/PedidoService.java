package co.edu.usco.khaza.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.usco.khaza.dto.ItemPedidoRequest;
import co.edu.usco.khaza.dto.PedidoRequest;
import co.edu.usco.khaza.model.DetallePedido;
import co.edu.usco.khaza.model.Pedido;
import co.edu.usco.khaza.model.Producto;
import co.edu.usco.khaza.model.Usuario;
import co.edu.usco.khaza.repository.PedidoRepository;
import co.edu.usco.khaza.repository.ProductoRepository;

/**
 * Lógica de negocio de pedidos: crearlos, listarlos y cambiarles el estado.
 *
 * @author Juan Manuel Polanco
 */
@Service
public class PedidoService {

	/** Los únicos estados que acepta un pedido (los mismos del CHECK de la tabla). */
	public static final List<String> ESTADOS = Arrays.asList("PENDIENTE", "ENVIADO", "ENTREGADO");

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private ProductoRepository productoRepository;

	/** @return todos los pedidos, del más reciente al más antiguo (para el administrador) */
	public List<Pedido> listar() {
		return pedidoRepository.findAllByOrderByFechaDesc();
	}

	/**
	 * @param usuarioId id del cliente
	 * @return los pedidos de ese cliente (para "Mis pedidos")
	 */
	public List<Pedido> listarPorUsuario(Integer usuarioId) {
		return pedidoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
	}

	/**
	 * Crea un pedido con estado PENDIENTE.
	 *
	 * El precio de cada producto y el total se calculan aquí con los precios de
	 * la base de datos. El stock NO se descuenta en Java: lo hace el trigger
	 * trg_descontar_stock al insertar cada línea de detalle.
	 *
	 * Lleva @Transactional: si falla una línea, no se guarda nada del pedido.
	 *
	 * Ojo: como el trigger cambia el stock directamente en MySQL, el stock de los
	 * productos que vienen en la respuesta es el de antes de la compra. Para ver el
	 * stock nuevo hay que volver a consultar GET /api/productos.
	 *
	 * @param usuario el cliente que tiene la sesión iniciada
	 * @param datos   lo que envió el cliente desde el carrito
	 * @return el pedido guardado, con su id y su total
	 * @throws ResponseStatusException 400 si algún dato no es válido, 409 si no alcanza el stock
	 */
	@Transactional
	public Pedido crear(Usuario usuario, PedidoRequest datos) {
		Pedido pedido = new Pedido();
		pedido.setUsuario(usuario);
		pedido.setFecha(LocalDateTime.now());
		pedido.setEstado("PENDIENTE");
		pedido.setDireccion(datos.getDireccion());
		pedido.setTelefono(datos.getTelefono());
		pedido.setNota(datos.getNota());

		double total = 0;
		for (ItemPedidoRequest item : datos.getItems()) {
			Producto producto = validarItem(item);

			DetallePedido detalle = new DetallePedido();
			detalle.setPedido(pedido);
			detalle.setProducto(producto);
			detalle.setTalla(item.getTalla());
			detalle.setCantidad(item.getCantidad());
			detalle.setPrecioUnitario(producto.getPrecio());
			pedido.getDetalles().add(detalle);

			total += producto.getPrecio() * item.getCantidad();
		}
		pedido.setTotal(total);

		try {
			// saveAndFlush manda el INSERT de una vez. Si MySQL lo rechaza (lo más común: el stock
			// no alcanza y el CHECK (stock >= 0) lo impide, por ejemplo si dos clientes compran el
			// último producto al mismo tiempo), Spring lo entrega como una DataAccessException.
			return pedidoRepository.saveAndFlush(pedido);
		} catch (DataAccessException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "No se pudo guardar el pedido: no hay stock suficiente o los datos no son válidos");
		}
	}

	/**
	 * Cambia el estado de un pedido (lo hace el administrador).
	 *
	 * @param id     id del pedido
	 * @param estado PENDIENTE, ENVIADO o ENTREGADO
	 * @return el pedido actualizado
	 * @throws ResponseStatusException 404 si el pedido no existe, 400 si el estado no es válido
	 */
	public Pedido cambiarEstado(Integer id, String estado) {
		if (!ESTADOS.contains(estado)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no válido. Use: " + ESTADOS);
		}
		Pedido pedido = pedidoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
		pedido.setEstado(estado);
		return pedidoRepository.save(pedido);
	}

	/**
	 * Revisa que el producto exista, esté a la venta, tenga esa talla y alcance
	 * el stock (salvo que sea bajo pedido).
	 *
	 * @return el producto, si todo está bien
	 */
	private Producto validarItem(ItemPedidoRequest item) {
		Producto producto = productoRepository.findById(item.getProductoId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"El producto " + item.getProductoId() + " no existe"));

		if (!producto.getActivo()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					producto.getNombre() + " ya no está a la venta");
		}
		List<String> tallas = Arrays.asList(producto.getTallas().split(","));
		if (!tallas.contains(item.getTalla())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"La talla " + item.getTalla() + " no está disponible para " + producto.getNombre());
		}
		if (!producto.getBajoPedido() && item.getCantidad() > producto.getStock()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Solo quedan " + producto.getStock() + " unidades de " + producto.getNombre());
		}
		return producto;
	}
}
