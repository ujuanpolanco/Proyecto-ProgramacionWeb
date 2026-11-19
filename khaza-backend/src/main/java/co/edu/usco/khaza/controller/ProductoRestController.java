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

import co.edu.usco.khaza.model.Producto;
import co.edu.usco.khaza.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Servicio web RESTful de productos: el catálogo y el inventario.
 *
 * @author Juan Manuel Polanco
 */
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "CRUD de productos e inventario")
public class ProductoRestController {

	@Autowired
	private ProductoService productoService;

	/** @return todos los productos */
	@Operation(summary = "Listar productos", description = "Devuelve todos los productos, también los que no están visibles.")
	@ApiResponse(responseCode = "200", description = "Lista de productos")
	@GetMapping
	public List<Producto> listar() {
		return productoService.listar();
	}

	/**
	 * @param id id del producto
	 * @return el producto
	 */
	@Operation(summary = "Buscar un producto", description = "Busca un producto por su ID.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Producto encontrado"),
			@ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content) })
	@GetMapping("/{id}")
	public Producto buscar(@Parameter(description = "ID del producto", required = true) @PathVariable Integer id) {
		return productoService.buscar(id);
	}

	/**
	 * @param producto datos del producto (se validan con @Valid)
	 * @return el producto creado, con código 201
	 */
	@Operation(summary = "Crear un producto", description = "Crea un producto nuevo. La categoría se envía como {\"id\": 1}.")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Producto creado"),
			@ApiResponse(responseCode = "400", description = "Datos no válidos o la categoría no existe", content = @Content),
			@ApiResponse(responseCode = "401", description = "Falta el token o no es válido", content = @Content),
			@ApiResponse(responseCode = "403", description = "Solo el administrador", content = @Content) })
	@SecurityRequirement(name = "bearer")
	@PostMapping
	public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(producto));
	}

	/**
	 * @param id       id del producto
	 * @param producto datos nuevos
	 * @return el producto actualizado
	 */
	@Operation(summary = "Actualizar un producto", description = "Cambia los datos de un producto. Si cambia el precio, el trigger lo registra en auditoria_precios.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Producto actualizado"),
			@ApiResponse(responseCode = "400", description = "Datos no válidos o la categoría no existe", content = @Content),
			@ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content),
			@ApiResponse(responseCode = "401", description = "Falta el token o no es válido", content = @Content),
			@ApiResponse(responseCode = "403", description = "Solo el administrador", content = @Content) })
	@SecurityRequirement(name = "bearer")
	@PutMapping("/{id}")
	public Producto actualizar(@Parameter(description = "ID del producto", required = true) @PathVariable Integer id,
			@Valid @RequestBody Producto producto) {
		return productoService.actualizar(id, producto);
	}

	/**
	 * @param id id del producto
	 * @return respuesta vacía con código 204
	 */
	@Operation(summary = "Eliminar un producto", description = "Elimina un producto que todavía no se haya vendido.")
	@ApiResponses({ @ApiResponse(responseCode = "204", description = "Producto eliminado"),
			@ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content),
			@ApiResponse(responseCode = "409", description = "El producto ya tiene ventas", content = @Content),
			@ApiResponse(responseCode = "401", description = "Falta el token o no es válido", content = @Content),
			@ApiResponse(responseCode = "403", description = "Solo el administrador", content = @Content) })
	@SecurityRequirement(name = "bearer")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@Parameter(description = "ID del producto", required = true) @PathVariable Integer id) {
		productoService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
