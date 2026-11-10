package co.edu.usco.khaza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del back-end de KHAZA. Al ejecutarla arranca el servidor en
 * http://localhost:8080 y la documentación en /swagger-ui.html.
 *
 * @author Juan Manuel Polanco
 */
@SpringBootApplication
public class KhazaApplication {

	/**
	 * Arranca la aplicación Spring Boot.
	 *
	 * @param args argumentos de la línea de comandos (no se usan)
	 */
	public static void main(String[] args) {
		SpringApplication.run(KhazaApplication.class, args);
	}
}
