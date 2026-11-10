package co.edu.usco.khaza.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA de la tabla "productos". Los nombres de los atributos en Java
 * (bajoPedido) se convierten solos a los de las columnas (bajo_pedido).
 *
 * @author Juan Manuel Polanco
 */
@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@Schema(description = "Producto de la tienda")
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "ID (lo genera la base de datos)", example = "1")
	private Integer id;

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 100, message = "El nombre tiene máximo 100 caracteres")
	@Schema(example = "Sombrero Rosa")
	private String nombre;

	@Schema(example = "Paja natural con cintas en rosa palo y perlas cosidas a mano.")
	private String descripcion;

	@NotNull(message = "El precio es obligatorio")
	@DecimalMin(value = "1000", message = "El precio mínimo es $ 1.000")
	@DecimalMax(value = "99999999", message = "El precio máximo es $ 99.999.999")
	@Schema(description = "Precio en pesos colombianos", example = "200000")
	private Double precio;

	/** Unidades disponibles. Lo descuenta el trigger trg_descontar_stock. */
	@NotNull(message = "El stock es obligatorio")
	@Min(value = 0, message = "El stock no puede ser negativo")
	@Schema(example = "5")
	private Integer stock;

	@Size(max = 50, message = "El material tiene máximo 50 caracteres")
	@Schema(example = "Paja natural")
	private String material;

	@Size(max = 100, message = "La decoración tiene máximo 100 caracteres")
	@Schema(example = "Cintas y perlas")
	private String decoracion;

	@NotBlank(message = "Las tallas son obligatorias")
	@Size(max = 50, message = "Las tallas tienen máximo 50 caracteres")
	@Schema(description = "Tallas separadas por coma", example = "S,M,L")
	private String tallas;

	@NotNull(message = "Falta indicar si es bajo pedido")
	@Schema(description = "Si es true se puede vender sin stock (se fabrica después)", example = "false")
	private Boolean bajoPedido = false;

	@Size(max = 255, message = "La ruta de la imagen tiene máximo 255 caracteres")
	@Schema(example = "img/productos/sombrero-rosa.jpg")
	private String imagen;

	@NotNull(message = "Falta indicar si está visible")
	@Schema(description = "Si es false no aparece en el catálogo", example = "true")
	private Boolean activo = true;

	@NotNull(message = "La categoría es obligatoria")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "categoria_id")
	private Categoria categoria;
}
