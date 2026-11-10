package co.edu.usco.khaza.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA de la tabla "roles": ADMIN o CLIENTE.
 *
 * @author Juan Manuel Polanco
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Rol {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String nombre;
}
