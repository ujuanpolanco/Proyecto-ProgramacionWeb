package co.edu.usco.khaza.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Datos generales de la documentación Swagger (título, versión, contacto) y el
 * botón "Authorize" para probar las rutas protegidas con el token JWT.
 *
 * @author Juan Manuel Polanco
 */
@Configuration
public class SwaggerConfig {

	/**
	 * @return la información que aparece arriba en /swagger-ui.html
	 */
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("API de KHAZA")
						.version("1.0.0")
						.description("API REST de la tienda KHAZA: productos, categorías, pedidos y reportes. "
								+ "Para las rutas protegidas: hacer login en /api/auth/login, copiar el token "
								+ "y pegarlo en el botón Authorize.")
						.contact(new Contact().name("Juan Manuel Polanco")))
				// Esquema "bearer": Swagger agrega el encabezado Authorization: Bearer <token>
				.components(new Components().addSecuritySchemes("bearer",
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
	}
}
