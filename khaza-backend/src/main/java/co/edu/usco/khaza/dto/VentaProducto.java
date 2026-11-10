package co.edu.usco.khaza.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Una fila del reporte de ventas: cuánto se vendió de un producto.
 *
 * @author Juan Manuel Polanco
 */
@Data
@AllArgsConstructor
@Schema(description = "Ventas de un producto")
public class VentaProducto {

	@Schema(example = "Sombrero Multicolor")
	private String producto;

	@Schema(description = "Unidades vendidas", example = "2")
	private Integer unidades;

	@Schema(description = "Dinero recibido en pesos", example = "400000")
	private Double totalVendido;
}
