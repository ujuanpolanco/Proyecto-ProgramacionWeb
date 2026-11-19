package co.edu.usco.khaza.dto;

import co.edu.usco.khaza.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Respuesta del login y del registro: el token JWT y los datos del usuario.
 * El front-end guarda el token y lo envía en cada petición.
 *
 * @author Juan Manuel Polanco
 */
@Data
@AllArgsConstructor
@Schema(description = "Token JWT y datos del usuario que inició sesión")
public class AuthResponse {

	@Schema(description = "Token JWT. Se envía en el encabezado: Authorization: Bearer <token>")
	private String token;

	private Usuario usuario;
}
