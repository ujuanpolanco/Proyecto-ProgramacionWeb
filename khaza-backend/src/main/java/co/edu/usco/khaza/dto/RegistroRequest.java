package co.edu.usco.khaza.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Datos del formulario de registro de un cliente nuevo.
 *
 * @author Juan Manuel Polanco
 */
@Data
@Schema(description = "Datos para crear una cuenta de cliente")
public class RegistroRequest {

	@NotBlank(message = "El nombre es obligatorio")
	@Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
	@Schema(example = "Pedro Ruiz")
	private String nombre;

	@NotBlank(message = "El correo es obligatorio")
	@Email(message = "El correo no es válido")
	@Size(max = 100, message = "El correo tiene máximo 100 caracteres")
	@Schema(example = "pedro@correo.com")
	private String email;

	@NotBlank(message = "El celular es obligatorio")
	@Pattern(regexp = "3[0-9]{9}", message = "El celular debe tener 10 dígitos y empezar por 3")
	@Schema(example = "3201234567")
	private String telefono;

	// BCrypt solo tiene en cuenta los primeros 72 caracteres, por eso el máximo.
	@NotBlank(message = "La contraseña es obligatoria")
	@Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
	@Schema(example = "clave1234")
	private String password;

	/** Autorización de tratamiento de datos (Ley 1581 de 2012): sin ella no se crea la cuenta. */
	@NotNull(message = "Debe aceptar el tratamiento de datos")
	@AssertTrue(message = "Debe aceptar el tratamiento de datos")
	@Schema(example = "true")
	private Boolean aceptaDatos;
}
