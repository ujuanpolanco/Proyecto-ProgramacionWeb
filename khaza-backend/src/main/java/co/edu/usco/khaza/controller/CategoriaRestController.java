package co.edu.usco.khaza.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.usco.khaza.model.Categoria;
import co.edu.usco.khaza.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Servicio web RESTful de categorías: recibe y devuelve JSON. Se prueba en
 * Swagger (https://localhost:8443/swagger-ui.html) o con Postman.
 *
 * @author Juan Manuel Polanco
 */
@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "CRUD de las categorías de productos")
public class CategoriaRestController {

	@Autowired
	private CategoriaService categoriaService;

	/** @return todas las categorías */
	@Operation(summary = "Listar categorías", description = "Devuelve todas las categorías.")
	@ApiResponse(responseCode = "200", description = "Lista de categorías")
	@GetMapping
	public List<Categoria> listar() {
		return categoriaService.listar();
	}

	/**
	 * @param id id de la categoría
	 * @return la categoría
	 */
	@Operation(summary = "Buscar una categoría", description = "Busca una categoría por su ID.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
			@ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content) })
	@GetMapping("/{id}")
	public Categoria buscar(
			@Parameter(description = "ID de la categoría", required = true) @PathVariable Integer id) {
		return categoriaService.buscar(id);
	}

	/**
	 * @param categoria datos de la categoría (se validan con @Valid)
	 * @return la categoría creada, con código 201
	 */
	@Operation(summary = "Crear una categoría", description = "Crea una categoría nueva. El ID lo genera la base de datos.")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Categoría creada"),
			@ApiResponse(responseCode = "400", description = "Datos no válidos", content = @Content),
			@ApiResponse(responseCode = "409", description = "Ya existe una categoría con ese nombre", content = @Content),
			@ApiResponse(responseCode = "401", description = "Falta el token o no es válido", content = @Content),
			@ApiResponse(responseCode = "403", description = "Solo el administrador", content = @Content) })
	@SecurityRequirement(name = "bearer")
	@PostMapping
	public ResponseEntity<Categoria> crear(@Valid @RequestBody Categoria categoria) {
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(categoria));
	}

	/**
	 * @param id        id de la categoría
	 * @param categoria datos nuevos
	 * @return la categoría actualizada
	 */
	@Operation(summary = "Actualizar una categoría", description = "Cambia el nombre y la descripción de una categoría.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Categoría actualizada"),
			@ApiResponse(responseCode = "400", description = "Datos no válidos", content = @Content),
			@ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content),
			@ApiResponse(responseCode = "409", description = "Ya existe una categoría con ese nombre", content = @Content),
			@ApiResponse(responseCode = "401", description = "Falta el token o no es válido", content = @Content),
			@ApiResponse(responseCode = "403", description = "Solo el administrador", content = @Content) })
	@SecurityRequirement(name = "bearer")
	@PutMapping("/{id}")
	public Categoria actualizar(
			@Parameter(description = "ID de la categoría", required = true) @PathVariable Integer id,
			@Valid @RequestBody Categoria categoria) {
		return categoriaService.actualizar(id, categoria);
	}

	/**
	 * @param id id de la categoría
	 * @return respuesta vacía con código 204
	 */
	@Operation(summary = "Eliminar una categoría", description = "Elimina una categoría que no tenga productos.")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Categoría eliminada"),
			@ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content),
			@ApiResponse(responseCode = "409", description = "La categoría tiene productos", content = @Content),
			@ApiResponse(responseCode = "401", description = "Falta el token o no es válido", content = @Content),
			@ApiResponse(responseCode = "403", description = "Solo el administrador", content = @Content) })
	@SecurityRequirement(name = "bearer")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(
			@Parameter(description = "ID de la categoría", required = true) @PathVariable Integer id) {
		categoriaService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
