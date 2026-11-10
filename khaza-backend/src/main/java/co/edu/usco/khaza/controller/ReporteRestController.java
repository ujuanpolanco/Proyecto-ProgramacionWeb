package co.edu.usco.khaza.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.usco.khaza.dao.ReporteDao;
import co.edu.usco.khaza.dto.VentaProducto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Servicio web RESTful de reportes. Los datos salen de ReporteDao, que usa
 * JDBC directamente (sin JPA).
 *
 * @author Juan Manuel Polanco
 */
@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Reportes de ventas para el administrador")
public class ReporteRestController {

	@Autowired
	private ReporteDao reporteDao;

	/** @return unidades y dinero vendidos por cada producto */
	@Operation(summary = "Ventas por producto", description = "Unidades vendidas y dinero recibido por cada producto, de mayor a menor.")
	@ApiResponse(responseCode = "200", description = "Reporte de ventas")
	@GetMapping("/ventas")
	public List<VentaProducto> ventasPorProducto() {
		return reporteDao.ventasPorProducto();
	}
}
