package co.edu.usco.khaza.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * Datos generales de la documentación Swagger (título, versión, contacto).
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
		return new OpenAPI().info(new Info()
				.title("API de KHAZA")
				.version("1.0.0")
				.description("API REST de la tienda KHAZA: productos, categorías, pedidos y reportes.")
				.contact(new Contact().name("Juan Manuel Polanco")));
	}
}
