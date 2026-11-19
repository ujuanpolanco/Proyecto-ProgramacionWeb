package co.edu.usco.khaza.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import co.edu.usco.khaza.model.Usuario;
import co.edu.usco.khaza.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Se ejecuta cuando Google confirma el inicio de sesión (OAuth 2.0).
 *
 * El flujo completo es:
 * 1. El front-end manda al usuario a /oauth2/authorization/google.
 * 2. Spring lo redirige a Google, donde la persona inicia sesión.
 * 3. Google vuelve a /login/oauth2/code/google con un código.
 * 4. Spring cambia ese código por los datos del usuario (correo y nombre).
 * 5. Esta clase busca o crea el usuario, genera nuestro JWT y vuelve al
 *    front-end con el token en la URL: /oauth2?token=...
 *
 * @author Juan Manuel Polanco
 */
@Component
public class GoogleLoginExitoso implements AuthenticationSuccessHandler {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private JwtUtil jwtUtil;

	@Value("${khaza.frontend.url}")
	private String frontendUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		OAuth2User datosGoogle = (OAuth2User) authentication.getPrincipal();
		String email = datosGoogle.getAttribute("email");
		String nombre = datosGoogle.getAttribute("name");

		Usuario usuario = usuarioService.loginConGoogle(email, nombre);
		if (usuario == null) {
			// Ese correo ya tiene una cuenta con contraseña: debe entrar con correo y contraseña
			response.sendRedirect(frontendUrl + "/login?error=clave");
			return;
		}
		if (!usuario.getActivo()) {
			response.sendRedirect(frontendUrl + "/login?error=desactivada");
			return;
		}

		// La sesión solo hacía falta mientras se hablaba con Google; desde aquí se usa el JWT.
		request.getSession().invalidate();
		response.sendRedirect(frontendUrl + "/oauth2?token=" + jwtUtil.generarToken(usuario));
	}
}
