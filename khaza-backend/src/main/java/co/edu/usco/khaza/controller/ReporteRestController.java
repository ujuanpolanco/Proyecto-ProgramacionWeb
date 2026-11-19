package co.edu.usco.khaza.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.usco.khaza.dao.ReporteDao;
import co.edu.usco.khaza.dto.PedidosPorEstado;
import co.edu.usco.khaza.dto.VentaProducto;
import co.edu.usco.khaza.service.ReportePdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Servicio web RESTful de reportes (solo el administrador). Los datos salen de
 * ReporteDao, que usa JDBC directamente (sin JPA).
 *
 * @author Juan Manuel Polanco
 */
@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Reportes de ventas para el administrador")
@SecurityRequirement(name = "bearer")
public class ReporteRestController {

	@Autowired
	private ReporteDao reporteDao;

	@Autowired
	private ReportePdfService reportePdfService;

	/** @return unidades y dinero vendidos por cada producto */
	@Operation(summary = "Ventas por producto", description = "Unidades vendidas y dinero recibido por cada producto, de mayor a menor.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Reporte de ventas"),
			@ApiResponse(responseCode = "401", description = "Falta el token o no es válido", content = @Content),
			@ApiResponse(responseCode = "403", description = "Solo el administrador", content = @Content) })
	@GetMapping("/ventas")
	public List<VentaProducto> ventasPorProducto() {
		return reporteDao.ventasPorProducto();
	}

	/** @return cuántos pedidos hay en cada estado */
	@Operation(summary = "Pedidos por estado", description = "Cantidad de pedidos y total en pesos de cada estado.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Pedidos por estado"),
			@ApiResponse(responseCode = "401", description = "Falta el token o no es válido", content = @Content),
			@ApiResponse(responseCode = "403", description = "Solo el administrador", content = @Content) })
	@GetMapping("/estados")
	public List<PedidosPorEstado> pedidosPorEstado() {
		return reporteDao.pedidosPorEstado();
	}

	/** @return el reporte de ventas en PDF, listo para descargar */
	@Operation(summary = "Reporte de ventas en PDF", description = "Descarga el reporte de ventas (por producto y por estado) en PDF, hecho con OpenPDF.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Archivo PDF",
			content = @Content(mediaType = "application/pdf")),
			@ApiResponse(responseCode = "401", description = "Falta el token o no es válido", content = @Content),
			@ApiResponse(responseCode = "403", description = "Solo el administrador", content = @Content) })
	@GetMapping("/ventas/pdf")
	public ResponseEntity<byte[]> ventasPdf() {
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_PDF)
				// "attachment" hace que el navegador lo descargue con ese nombre
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte-ventas-khaza.pdf\"")
				.body(reportePdfService.generarReporteVentas());
	}
}
