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

import co.edu.usco.khaza.dto.PedidosPorEstado;
import co.edu.usco.khaza.dto.VentaProducto;

/**
 * Consultas de los reportes con JDBC (como en la clase 8): Connection,
 * PreparedStatement y ResultSet. Se usa JDBC y no JPA porque son consultas con
 * JOIN, SUM y GROUP BY que no corresponden a una sola entidad.
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

	/**
	 * @return cuántos pedidos hay en cada estado y cuánto suman
	 */
	public List<PedidosPorEstado> pedidosPorEstado() {
		String sql = "SELECT estado, COUNT(*) AS cantidad, SUM(total) AS total "
				+ "FROM pedidos GROUP BY estado ORDER BY estado";

		List<PedidosPorEstado> filas = new ArrayList<>();
		try (Connection conexion = dataSource.getConnection();
				PreparedStatement consulta = conexion.prepareStatement(sql);
				ResultSet resultado = consulta.executeQuery()) {

			while (resultado.next()) {
				filas.add(new PedidosPorEstado(
						resultado.getString("estado"),
						resultado.getInt("cantidad"),
						resultado.getDouble("total")));
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error al consultar los pedidos por estado", e);
		}
		return filas;
	}
}
