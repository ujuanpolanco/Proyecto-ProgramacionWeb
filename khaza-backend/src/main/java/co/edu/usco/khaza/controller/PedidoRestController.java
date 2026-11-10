package co.edu.usco.khaza.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.usco.khaza.dto.PedidoRequest;
import co.edu.usco.khaza.model.Pedido;
import co.edu.usco.khaza.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Servicio web RESTful de pedidos.
 *
 * @author Juan Manuel Polanco
 */
@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Crear pedidos, consultarlos y cambiarles el estado")
public class PedidoRestController {

	@Autowired
	private PedidoService pedidoService;

	/**
	 * @param usuarioId id del cliente (opcional)
	 * @return los pedidos del cliente, o todos si no se envía usuarioId
	 */
	@Operation(summary = "Listar pedidos", description = "Sin parámetros devuelve todos (administrador). Con ?usuarioId=2 devuelve solo los de ese cliente.")
	@ApiResponse(responseCode = "200", description = "Lista de pedidos, del más reciente al más antiguo")
	@GetMapping
	public List<Pedido> listar(
			@Parameter(description = "ID del cliente (opcional)") @RequestParam(required = false) Integer usuarioId) {
		if (usuarioId != null) {
			return pedidoService.listarPorUsuario(usuarioId);
		}
		return pedidoService.listar();
	}

	/**
	 * @param pedido datos del pedido (se validan con @Valid)
	 * @return el pedido creado, con código 201
	 */
	@Operation(summary = "Crear un pedido", description = "Registra el pedido como PENDIENTE. El total lo calcula el servidor y el stock lo descuenta el trigger de MySQL.")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Pedido creado"),
			@ApiResponse(responseCode = "400", description = "Datos no válidos (producto, talla, teléfono...)", content = @Content),
			@ApiResponse(responseCode = "409", description = "No hay stock suficiente", content = @Content) })
	@PostMapping
	public ResponseEntity<Pedido> crear(@Valid @RequestBody PedidoRequest pedido) {
		return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(pedido));
	}

	/**
	 * @param id     id del pedido
	 * @param estado estado nuevo
	 * @return el pedido actualizado
	 */
	@Operation(summary = "Cambiar el estado de un pedido", description = "Ejemplo: PUT /api/pedidos/2/estado?estado=ENVIADO")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Estado actualizado"),
			@ApiResponse(responseCode = "400", description = "Estado no válido", content = @Content),
			@ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content) })
	@PutMapping("/{id}/estado")
	public Pedido cambiarEstado(@Parameter(description = "ID del pedido", required = true) @PathVariable Integer id,
			@Parameter(description = "PENDIENTE, ENVIADO o ENTREGADO", required = true) @RequestParam String estado) {
		return pedidoService.cambiarEstado(id, estado);
	}
}
