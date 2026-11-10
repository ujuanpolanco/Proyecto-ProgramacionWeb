package co.edu.usco.khaza.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entidad JPA de la tabla "detalle_pedido": una línea de un pedido (qué
 * producto, qué talla, cuántos y a qué precio). Al insertar una línea, el
 * trigger trg_descontar_stock resta el stock del producto.
 *
 * @author Juan Manuel Polanco
 */
@Entity
@Table(name = "detalle_pedido")
@Data
@NoArgsConstructor
public class DetallePedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// @JsonIgnore y los Exclude evitan un ciclo infinito:
	// el pedido tiene detalles, y cada detalle tiene su pedido.
	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne
	@JoinColumn(name = "pedido_id")
	private Pedido pedido;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "producto_id")
	private Producto producto;

	private String talla;

	private Integer cantidad;

	/** Precio al momento de la compra, aunque después el producto cambie de precio. */
	private Double precioUnitario;
}
