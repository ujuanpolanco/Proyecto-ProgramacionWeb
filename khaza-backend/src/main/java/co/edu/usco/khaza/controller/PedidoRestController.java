package co.edu.usco.khaza.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import co.edu.usco.khaza.model.Usuario;
import co.edu.usco.khaza.service.PedidoService;
import co.edu.usco.khaza.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Servicio web RESTful de pedidos. Todas las rutas necesitan el token JWT. El
 * cliente que hace el pedido sale del token, así nadie puede comprar a nombre
 * de otro.
 *
 * @author Juan Manuel Polanco
 */
@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Crear pedidos, consultarlos y cambiarles el estado")
@SecurityRequirement(name = "bearer")
public class PedidoRestController {

	@Autowired
	private PedidoService pedidoService;

	@Autowired
	private UsuarioService usuarioService;

	/** @return todos los pedidos (solo el administrador) */
	@Operation(summary = "Listar todos los pedidos", description = "Para el administrador: todos los pedidos, del más reciente al más antiguo.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Lista de pedidos"),
			@ApiResponse(responseCode = "401", description = "Falta el token o no es válido", content = @Content),
			@ApiResponse(responseCode = "403", description = "Solo el administrador", content = @Content) })
	@GetMapping
	public List<Pedido> listar() {
		return pedidoService.listar();
	}

	/**
	 * @param autenticacion usuario del token
	 * @return los pedidos del usuario que tiene la sesión iniciada
	 */
	@Operation(summary = "Mis pedidos", description = "Los pedidos del usuario dueño del token (historial del cliente).")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Lista de pedidos del cliente"),
			@ApiResponse(responseCode = "401", description = "Falta el token o no es válido", content = @Content) })
	@GetMapping("/mios")
	public List<Pedido> misPedidos(Authentication autenticacion) {
		Usuario usuario = usuarioService.buscarPorEmail(autenticacion.getName());
		return pedidoService.listarPorUsuario(usuario.getId());
	}

	/**
	 * @param pedido        datos del pedido (se validan con @Valid)
	 * @param autenticacion usuario del token
	 * @return el pedido creado, con código 201
	 */
	@Operation(summary = "Crear un pedido", description = "Registra el pedido como PENDIENTE a nombre del dueño del token. El total lo calcula el servidor y el stock lo descuenta el trigger de MySQL.")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Pedido creado"),
			@ApiResponse(responseCode = "400", description = "Datos no válidos (producto, talla, teléfono...)", content = @Content),
			@ApiResponse(responseCode = "401", description = "Falta el token o no es válido", content = @Content),
			@ApiResponse(responseCode = "409", description = "No hay stock suficiente", content = @Content) })
	@PostMapping
	public ResponseEntity<Pedido> crear(@Valid @RequestBody PedidoRequest pedido, Authentication autenticacion) {
		Usuario usuario = usuarioService.buscarPorEmail(autenticacion.getName());
		return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(usuario, pedido));
	}

	/**
	 * @param id     id del pedido
	 * @param estado estado nuevo
	 * @return el pedido actualizado
	 */
	@Operation(summary = "Cambiar el estado de un pedido", description = "Solo el administrador. Ejemplo: PUT /api/pedidos/2/estado?estado=ENVIADO")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Estado actualizado"),
			@ApiResponse(responseCode = "400", description = "Estado no válido", content = @Content),
			@ApiResponse(responseCode = "401", description = "Falta el token o no es válido", content = @Content),
			@ApiResponse(responseCode = "403", description = "Solo el administrador", content = @Content),
			@ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content) })
	@PutMapping("/{id}/estado")
	public Pedido cambiarEstado(@Parameter(description = "ID del pedido", required = true) @PathVariable Integer id,
			@Parameter(description = "PENDIENTE, ENVIADO o ENTREGADO", required = true) @RequestParam String estado) {
		return pedidoService.cambiarEstado(id, estado);
	}
}
