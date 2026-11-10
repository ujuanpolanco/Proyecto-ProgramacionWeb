package co.edu.usco.khaza.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import co.edu.usco.khaza.dto.VentaProducto;

/**
 * Consultas del reporte de ventas con JDBC (como en la clase 8): Connection,
 * PreparedStatement y ResultSet. Se usa JDBC y no JPA porque es una consulta
 * con JOIN, SUM y GROUP BY que no corresponde a una sola entidad.
 *
 * Buenas prácticas de JDBC: try-with-resources para cerrar siempre los
 * recursos y PreparedStatement en lugar de Statement.
 *
 * @author Juan Manuel Polanco
 */
@Repository
public class ReporteDao {

	/** Spring entrega el DataSource ya configurado con los datos de application.properties. */
	@Autowired
	private DataSource dataSource;

	/**
	 * @return unidades vendidas y dinero recibido por cada producto, de mayor a menor
	 */
	public List<VentaProducto> ventasPorProducto() {
		String sql = "SELECT p.nombre, SUM(d.cantidad) AS unidades, "
				+ "SUM(d.cantidad * d.precio_unitario) AS total_vendido "
				+ "FROM detalle_pedido d "
				+ "INNER JOIN productos p ON p.id = d.producto_id "
				+ "GROUP BY p.id, p.nombre "
				+ "ORDER BY total_vendido DESC";

		List<VentaProducto> ventas = new ArrayList<>();
		// try-with-resources: al salir del bloque se cierran ResultSet, PreparedStatement y Connection
		try (Connection conexion = dataSource.getConnection();
				PreparedStatement consulta = conexion.prepareStatement(sql);
				ResultSet resultado = consulta.executeQuery()) {

			while (resultado.next()) {
				ventas.add(new VentaProducto(
						resultado.getString("nombre"),
						resultado.getInt("unidades"),
						resultado.getDouble("total_vendido")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error al consultar el reporte de ventas", e);
		}
		return ventas;
	}
}
