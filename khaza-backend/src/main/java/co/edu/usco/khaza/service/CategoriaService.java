package co.edu.usco.khaza.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.usco.khaza.model.Categoria;
import co.edu.usco.khaza.repository.CategoriaRepository;
import co.edu.usco.khaza.repository.ProductoRepository;

/**
 * Lógica de negocio de categorías. Los controladores usan este servicio, y el
 * servicio usa los repositorios.
 *
 * Cuando algo no se puede hacer, se lanza ResponseStatusException con el código
 * HTTP y el mensaje que le llega al front-end (404 no existe, 409 conflicto).
 *
 * @author Juan Manuel Polanco
 */
@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private ProductoRepository productoRepository;

	/** @return todas las categorías, ordenadas por id */
	public List<Categoria> listar() {
		return categoriaRepository.findAll(Sort.by("id"));
	}

	/**
	 * @param id id de la categoría
	 * @return la categoría
	 * @throws ResponseStatusException 404 si no existe
	 */
	public Categoria buscar(Integer id) {
		return categoriaRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
	}

	/**
	 * Crea una categoría nueva (el id lo genera la base de datos).
	 *
	 * @param categoria datos de la categoría
	 * @return la categoría guardada, ya con su id
	 * @throws ResponseStatusException 409 si ya existe una con ese nombre
	 */
	public Categoria crear(Categoria categoria) {
		if (categoriaRepository.existsByNombreIgnoreCase(categoria.getNombre())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre");
		}
		categoria.setId(null);
		return categoriaRepository.save(categoria);
	}

	/**
	 * Actualiza una categoría existente.
	 *
	 * @param id    id de la categoría a cambiar
	 * @param datos datos nuevos
	 * @return la categoría actualizada
	 * @throws ResponseStatusException 404 si no existe, 409 si el nombre ya lo usa otra
	 */
	public Categoria actualizar(Integer id, Categoria datos) {
		Categoria categoria = buscar(id);
		boolean cambioNombre = !categoria.getNombre().equalsIgnoreCase(datos.getNombre());
		if (cambioNombre && categoriaRepository.existsByNombreIgnoreCase(datos.getNombre())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre");
		}
		categoria.setNombre(datos.getNombre());
		categoria.setDescripcion(datos.getDescripcion());
		return categoriaRepository.save(categoria);
	}

	/**
	 * Elimina una categoría. No se permite si tiene productos, porque los
	 * productos quedarían sin categoría.
	 *
	 * @param id id de la categoría
	 * @throws ResponseStatusException 404 si no existe, 409 si tiene productos
	 */
	public void eliminar(Integer id) {
		buscar(id);
		if (productoRepository.existsByCategoriaId(id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"No se puede eliminar: la categoría tiene productos");
		}
		categoriaRepository.deleteById(id);
	}
}
