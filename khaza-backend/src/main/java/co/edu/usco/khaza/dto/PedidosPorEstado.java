package co.edu.usco.khaza.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Una fila del reporte: cuántos pedidos hay en un estado y cuánto suman.
 *
 * @author Juan Manuel Polanco
 */
@Data
@AllArgsConstructor
@Schema(description = "Pedidos en un estado")
public class PedidosPorEstado {

	@Schema(example = "PENDIENTE")
	private String estado;

	@Schema(example = "3")
	private Integer cantidad;

	@Schema(description = "Suma de los totales en pesos", example = "590000")
	private Double total;
}
