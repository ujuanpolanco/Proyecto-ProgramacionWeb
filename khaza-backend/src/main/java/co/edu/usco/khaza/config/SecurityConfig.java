package co.edu.usco.khaza.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import co.edu.usco.khaza.security.GoogleLoginExitoso;
import co.edu.usco.khaza.security.JwtFiltro;

/**
 * Configuración de Spring Security: qué rutas son públicas y qué rol necesita
 * cada una, igual que en el ejercicio de roles de clase, pero para una API REST
 * con JWT en lugar de formulario de login y sesión.
 *
 * Reglas:
 *   Público:            ver productos y categorías, login, registro, Swagger
 *   CLIENTE o ADMIN:    crear pedidos, ver sus propios pedidos
 *   Solo ADMIN:         crear/editar/eliminar productos y categorías,
 *                       ver todos los pedidos, cambiar estados, reportes
 *
 * @author Juan Manuel Polanco
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtFiltro jwtFiltro;

	@Autowired
	private GoogleLoginExitoso googleLoginExitoso;

	@Value("${khaza.frontend.url}")
	private String frontendUrl;

	/**
	 * @param http configuración de seguridad de Spring
	 * @return la cadena de filtros de seguridad
	 * @throws Exception si la configuración no es válida
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// CSRF se desactiva porque la API no usa cookies de sesión sino el token JWT
			.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))

			.authorizeHttpRequests(auth -> auth
				// Páginas de error de Spring (si no, todo error se vuelve 401)
				.requestMatchers("/error").permitAll()
				// Documentación
				.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
				// Login, registro y el flujo de Google
				.requestMatchers("/api/auth/login", "/api/auth/registro").permitAll()
				.requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
				// El catálogo lo puede ver cualquiera
				.requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/categorias/**").permitAll()
				// Pedidos del cliente
				.requestMatchers(HttpMethod.POST, "/api/pedidos").hasAnyRole("CLIENTE", "ADMIN")
				.requestMatchers("/api/pedidos/mios").hasAnyRole("CLIENTE", "ADMIN")
				// Todo lo demás de pedidos, productos, categorías y reportes es del administrador
				.requestMatchers("/api/pedidos/**", "/api/productos/**", "/api/categorias/**", "/api/reportes/**")
					.hasRole("ADMIN")
				.anyRequest().authenticated())

			// Sin token → 401 (y no una redirección a la página de Google)
			.exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))

			// Inicio de sesión con Google (OAuth 2.0)
			.oauth2Login(oauth -> oauth
				.successHandler(googleLoginExitoso)
				.failureUrl(frontendUrl + "/login?error=google"))

			// El filtro JWT va antes del filtro de login de Spring
			.addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * CORS: el front-end corre en otro puerto (4200), y el navegador solo le deja
	 * llamar a esta API si el servidor lo autoriza.
	 *
	 * @return la configuración de CORS
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of(frontendUrl, "http://localhost:4200"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		config.setExposedHeaders(List.of("Content-Disposition"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", config);
		return source;
	}
}
