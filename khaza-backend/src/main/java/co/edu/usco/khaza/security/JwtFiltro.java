package co.edu.usco.khaza.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro que se ejecuta una vez en cada petición, antes de llegar al
 * controlador. Si la petición trae el encabezado "Authorization: Bearer token"
 * y el token es válido, le dice a Spring Security quién es el usuario y qué rol
 * tiene. Si no trae token o es inválido, la petición sigue sin usuario, y
 * Spring Security responde 401 si la ruta lo exige.
 *
 * @author Juan Manuel Polanco
 */
@Component
public class JwtFiltro extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String encabezado = request.getHeader("Authorization");
		if (encabezado != null && encabezado.startsWith("Bearer ")) {
			String token = encabezado.substring(7);
			try {
				Claims datos = jwtUtil.leerToken(token);
				String email = datos.getSubject();
				String rol = datos.get("rol", String.class);

				// Spring Security espera los roles con el prefijo ROLE_ (ROLE_ADMIN, ROLE_CLIENTE)
				UsernamePasswordAuthenticationToken autenticacion = new UsernamePasswordAuthenticationToken(
						email, null, List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
				SecurityContextHolder.getContext().setAuthentication(autenticacion);
			} catch (JwtException | IllegalArgumentException e) {
				// Token vencido o modificado: se ignora y la petición sigue sin usuario.
			}
		}
		chain.doFilter(request, response);
	}
}
