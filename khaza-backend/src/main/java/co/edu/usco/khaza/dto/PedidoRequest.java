package co.edu.usco.khaza.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Datos que envía el front-end para crear un pedido. No es una entidad: es
 * solo lo que el cliente llena en el carrito. El cliente no viene aquí: sale
 * del token JWT de quien hace la petición. El total y los precios NO vienen
 * aquí; los calcula el servidor con los precios de la base de datos, para que
 * nadie pueda cambiarlos desde el navegador.
 *
 * @author Juan Manuel Polanco
 */
@Data
@Schema(description = "Datos para crear un pedido")
public class PedidoRequest {

	@NotBlank(message = "La dirección es obligatoria")
	@Size(min = 5, max = 200, message = "La dirección debe tener entre 5 y 200 caracteres")
	@Schema(example = "Calle 8 # 5-20, Neiva")
	private String direccion;

	@NotBlank(message = "El teléfono es obligatorio")
	@Pattern(regexp = "3[0-9]{9}", message = "El celular debe tener 10 dígitos y empezar por 3")
	@Schema(example = "3104445566")
	private String telefono;

	@Size(max = 500, message = "La nota tiene máximo 500 caracteres")
	@Schema(example = "Con las iniciales L.G. en la cinta")
	private String nota;

	// @NotNull dentro de la lista: ningún item puede venir vacío (null).
	// @Valid hace que también se validen los campos de cada item.
	@NotEmpty(message = "El pedido debe tener al menos un producto")
	private List<@NotNull(message = "Hay un producto vacío en el pedido") @Valid ItemPedidoRequest> items;
}
