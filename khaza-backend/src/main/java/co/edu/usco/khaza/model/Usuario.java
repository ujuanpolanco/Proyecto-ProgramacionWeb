package co.edu.usco.khaza.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA de la tabla "usuarios". La relación con Rol es muchos a muchos a
 * través de la tabla intermedia "usuarios_roles", igual que en el ejercicio de
 * Spring Security de clase.
 *
 * @author Juan Manuel Polanco
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@Schema(description = "Usuario de la tienda")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Schema(example = "Laura Gómez")
	private String nombre;

	@Schema(example = "cliente@khaza.com")
	private String email;

	/** Cifrada con BCrypt. WRITE_ONLY: se puede recibir, pero nunca se envía en el JSON. */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	private String telefono;

	/** Autorizó el tratamiento de datos (Ley 1581 de 2012). */
	private Boolean aceptaDatos;

	private Boolean activo = true;

	private LocalDateTime fechaRegistro;

	// EAGER: al consultar un usuario también se traen sus roles.
	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usuarios_roles",
			joinColumns = @JoinColumn(name = "usuario_id"),
			inverseJoinColumns = @JoinColumn(name = "rol_id"))
	private Set<Rol> roles = new HashSet<>();

	/**
	 * El front-end solo necesita saber si es ADMIN o CLIENTE, así que en el JSON
	 * se envía un campo "rol" en lugar de la lista de roles.
	 *
	 * @return "ADMIN" si tiene ese rol; si no, "CLIENTE"
	 */
	public String getRol() {
		for (Rol rol : roles) {
			if (rol.getNombre().equals("ADMIN")) {
				return "ADMIN";
			}
		}
		return "CLIENTE";
	}
}
