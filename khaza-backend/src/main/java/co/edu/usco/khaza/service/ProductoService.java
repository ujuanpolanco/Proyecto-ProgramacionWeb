package co.edu.usco.khaza.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.usco.khaza.model.Categoria;
import co.edu.usco.khaza.model.Producto;
import co.edu.usco.khaza.repository.CategoriaRepository;
import co.edu.usco.khaza.repository.DetallePedidoRepository;
import co.edu.usco.khaza.repository.ProductoRepository;

/**
 * Lógica de negocio de productos (CRUD e inventario).
 *
 * @author Juan Manuel Polanco
 */
@Service
public class ProductoService {

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private DetallePedidoRepository detallePedidoRepository;

	/** @return todos los productos, ordenados por id */
	public List<Producto> listar() {
		return productoRepository.findAll(Sort.by("id"));
	}

	/**
	 * @param id id del producto
	 * @return el producto
	 * @throws ResponseStatusException 404 si no existe
	 */
	public Producto buscar(Integer id) {
		return productoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
	}

	/**
	 * Crea un producto nuevo (el id lo genera la base de datos).
	 *
	 * @param producto datos del producto
	 * @return el producto guardado, ya con su id
	 * @throws ResponseStatusException 400 si la categoría no existe
	 */
	public Producto crear(Producto producto) {
		producto.setId(null);
		producto.setCategoria(buscarCategoria(producto.getCategoria()));
		return productoRepository.save(producto);
	}

	/**
	 * Actualiza un producto existente. Si cambia el precio, el trigger
	 * trg_auditar_precio guarda el cambio en auditoria_precios.
	 *
	 * @param id    id del producto a cambiar
	 * @param datos datos nuevos
	 * @return el producto actualizado
	 * @throws ResponseStatusException 404 si no existe, 400 si la categoría no existe
	 */
	public Producto actualizar(Integer id, Producto datos) {
		Producto producto = buscar(id);
		producto.setNombre(datos.getNombre());
		producto.setDescripcion(datos.getDescripcion());
		producto.setPrecio(datos.getPrecio());
		producto.setStock(datos.getStock());
		producto.setMaterial(datos.getMaterial());
		producto.setDecoracion(datos.getDecoracion());
		producto.setTallas(datos.getTallas());
		producto.setBajoPedido(datos.getBajoPedido());
		producto.setImagen(datos.getImagen());
		producto.setActivo(datos.getActivo());
		producto.setCategoria(buscarCategoria(datos.getCategoria()));
		return productoRepository.save(producto);
	}

	/**
	 * Elimina un producto. Si ya se vendió no se puede borrar, porque se
	 * perdería el historial de pedidos; en ese caso hay que desactivarlo.
	 *
	 * @param id id del producto
	 * @throws ResponseStatusException 404 si no existe, 409 si ya tiene ventas
	 */
	public void eliminar(Integer id) {
		buscar(id);
		if (detallePedidoRepository.existsByProductoId(id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"No se puede eliminar: el producto ya tiene ventas. Desmarca \"Visible en el catálogo\".");
		}
		productoRepository.deleteById(id);
	}

	/**
	 * El JSON trae la categoría como objeto ({"id": 1}); aquí se busca la
	 * categoría real en la base de datos.
	 */
	private Categoria buscarCategoria(Categoria categoria) {
		if (categoria.getId() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría es obligatoria");
		}
		return categoriaRepository.findById(categoria.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría no existe"));
	}
}
