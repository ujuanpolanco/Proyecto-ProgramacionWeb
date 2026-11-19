package co.edu.usco.khaza.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Las contraseñas se cifran con BCrypt (claves encriptadas).
 *
 * Está en una clase aparte de SecurityConfig para evitar un ciclo: la
 * seguridad usa el login con Google, que usa UsuarioService, que a su vez
 * necesita este codificador.
 *
 * @author Juan Manuel Polanco
 */
@Configuration
public class PasswordConfig {

	/**
	 * @return el codificador BCrypt que usan el registro y el login
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
