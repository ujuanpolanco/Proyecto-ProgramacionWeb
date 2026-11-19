package co.edu.usco.khaza.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Datos del formulario de inicio de sesión.
 *
 * @author Juan Manuel Polanco
 */
@Data
@Schema(description = "Correo y contraseña para iniciar sesión")
public class LoginRequest {

	@NotBlank(message = "El correo es obligatorio")
	@Schema(example = "cliente@khaza.com")
	private String email;

	@NotBlank(message = "La contraseña es obligatoria")
	@Schema(example = "cliente123")
	private String password;
}
