package co.edu.usco.khaza.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA de la tabla "pedidos". Un pedido tiene varias líneas de detalle
 * (relación uno a muchos con DetallePedido).
 *
 * @author Juan Manuel Polanco
 */
@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@Schema(description = "Pedido de un cliente")
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	private LocalDateTime fecha;

	@Schema(description = "PENDIENTE, ENVIADO o ENTREGADO", example = "PENDIENTE")
	private String estado;

	private Double total;

	private String direccion;

	private String telefono;

	@Schema(description = "Nota de personalización del cliente")
	private String nota;

	// cascade ALL: al guardar el pedido se guardan también sus líneas de detalle.
	@OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<DetallePedido> detalles = new ArrayList<>();
}
