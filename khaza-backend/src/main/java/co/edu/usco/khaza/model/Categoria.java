package co.edu.usco.khaza.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA de la tabla "categorias" (Sombreros, Ropa...).
 *
 * @author Juan Manuel Polanco
 */
@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@Schema(description = "Categoría de productos")
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "ID (lo genera la base de datos)", example = "1")
	private Integer id;

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 50, message = "El nombre tiene máximo 50 caracteres")
	@Schema(example = "Sombreros")
	private String nombre;

	@Size(max = 255, message = "La descripción tiene máximo 255 caracteres")
	@Schema(example = "Sombreros artesanales decorados a mano")
	private String descripcion;
}
