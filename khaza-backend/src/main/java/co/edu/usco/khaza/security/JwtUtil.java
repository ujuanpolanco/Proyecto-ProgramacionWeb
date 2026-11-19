package co.edu.usco.khaza.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import co.edu.usco.khaza.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Crea y lee los tokens JWT.
 *
 * Un JWT tiene tres partes separadas por puntos: encabezado, datos (el correo,
 * el rol y la fecha en que vence) y una firma hecha con la clave secreta del
 * servidor. Si alguien cambia los datos, la firma ya no coincide y el token se
 * rechaza.
 *
 * @author Juan Manuel Polanco
 */
@Component
public class JwtUtil {

	/** Clave secreta (está en application.properties). */
	@Value("${khaza.jwt.secreto}")
	private String secreto;

	/** Cuántas horas dura un token. */
	@Value("${khaza.jwt.horas}")
	private int horas;

	/**
	 * @param usuario usuario que inició sesión
	 * @return el token firmado, con el correo como "subject" y el rol
	 */
	public String generarToken(Usuario usuario) {
		Date ahora = new Date();
		Date vence = new Date(ahora.getTime() + horas * 60L * 60L * 1000L);
		return Jwts.builder()
				.subject(usuario.getEmail())
				.claim("rol", usuario.getRol())
				.issuedAt(ahora)
				.expiration(vence)
				.signWith(clave())
				.compact();
	}

	/**
	 * Revisa la firma y la fecha de vencimiento del token.
	 *
	 * @param token el token que envió el front-end
	 * @return los datos del token
	 * @throws io.jsonwebtoken.JwtException si la firma no es válida o el token ya venció
	 */
	public Claims leerToken(String token) {
		return Jwts.parser()
				.verifyWith(clave())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private SecretKey clave() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto));
	}
}
