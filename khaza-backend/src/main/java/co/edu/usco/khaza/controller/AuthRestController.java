package co.edu.usco.khaza.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.usco.khaza.dto.AuthResponse;
import co.edu.usco.khaza.dto.LoginRequest;
import co.edu.usco.khaza.dto.RegistroRequest;
import co.edu.usco.khaza.model.Usuario;
import co.edu.usco.khaza.security.JwtUtil;
import co.edu.usco.khaza.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Servicio web de autenticación: registro, inicio de sesión y datos del
 * usuario que tiene la sesión iniciada.
 *
 * @author Juan Manuel Polanco
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro, login con JWT y usuario actual")
public class AuthRestController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * @param datos correo y contraseña
	 * @return el token JWT y el usuario
	 */
	@Operation(summary = "Iniciar sesión", description = "Revisa el correo y la contraseña (BCrypt) y devuelve un token JWT.")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Datos correctos: token y usuario"),
			@ApiResponse(responseCode = "400", description = "Faltan datos", content = @Content),
			@ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos", content = @Content),
			@ApiResponse(responseCode = "403", description = "Cuenta desactivada", content = @Content) })
	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest datos) {
		Usuario usuario = usuarioService.login(datos);
		return new AuthResponse(jwtUtil.generarToken(usuario), usuario);
	}

	/**
	 * @param datos datos del formulario de registro
	 * @return el token JWT y el usuario creado, con código 201
	 */
	@Operation(summary = "Crear cuenta", description = "Crea una cuenta de cliente (contraseña cifrada con BCrypt) y deja la sesión iniciada.")
	@ApiResponses({ @ApiResponse(responseCode = "201", description = "Cuenta creada: token y usuario"),
			@ApiResponse(responseCode = "400", description = "Datos no válidos", content = @Content),
			@ApiResponse(responseCode = "409", description = "Ya existe una cuenta con ese correo", content = @Content) })
	@PostMapping("/registro")
	public ResponseEntity<AuthResponse> registro(@Valid @RequestBody RegistroRequest datos) {
		Usuario usuario = usuarioService.registrar(datos);
		return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(jwtUtil.generarToken(usuario), usuario));
	}

	/**
	 * Lo usa el front-end después de entrar con Google: ya tiene el token, pero
	 * le faltan los datos del usuario.
	 *
	 * @param autenticacion usuario del token (lo llena el JwtFiltro)
	 * @return los datos del usuario que tiene la sesión iniciada
	 */
	@Operation(summary = "Usuario actual", description = "Devuelve los datos del dueño del token.")
	@SecurityRequirement(name = "bearer")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "Datos del usuario"),
			@ApiResponse(responseCode = "401", description = "Falta el token o no es válido", content = @Content) })
	@GetMapping("/yo")
	public Usuario yo(Authentication autenticacion) {
		return usuarioService.buscarPorEmail(autenticacion.getName());
	}
}
