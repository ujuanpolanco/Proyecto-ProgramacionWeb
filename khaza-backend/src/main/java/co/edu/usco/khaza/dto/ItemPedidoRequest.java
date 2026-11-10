package co.edu.usco.khaza.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Un producto dentro de un pedido: cuál, en qué talla y cuántos.
 *
 * @author Juan Manuel Polanco
 */
@Data
@Schema(description = "Producto que se quiere comprar")
public class ItemPedidoRequest {

	@NotNull(message = "El producto es obligatorio")
	@Schema(example = "2")
	private Integer productoId;

	@NotBlank(message = "La talla es obligatoria")
	@Size(max = 10, message = "La talla tiene máximo 10 caracteres")
	@Schema(example = "M")
	private String talla;

	@NotNull(message = "La cantidad es obligatoria")
	@Min(value = 1, message = "La cantidad mínima es 1")
	@Max(value = 100, message = "La cantidad máxima es 100")
	@Schema(example = "1")
	private Integer cantidad;
}
