package co.edu.usco.khaza.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import co.edu.usco.khaza.dto.LoginRequest;
import co.edu.usco.khaza.dto.RegistroRequest;
import co.edu.usco.khaza.model.Rol;
import co.edu.usco.khaza.model.Usuario;
import co.edu.usco.khaza.repository.RolRepository;
import co.edu.usco.khaza.repository.UsuarioRepository;

/**
 * Registro e inicio de sesión de usuarios.
 *
 * Las contraseñas nunca se guardan tal cual: se cifran con BCrypt. Al iniciar
 * sesión no se "descifra" nada; BCrypt vuelve a cifrar lo que escribió el
 * usuario y compara.
 *
 * @author Juan Manuel Polanco
 */
@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private RolRepository rolRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Crea una cuenta de cliente con la contraseña cifrada con BCrypt.
	 *
	 * @param datos datos del formulario de registro
	 * @return el usuario creado
	 * @throws ResponseStatusException 409 si ya existe una cuenta con ese correo
	 */
	public Usuario registrar(RegistroRequest datos) {
		String email = datos.getEmail().trim().toLowerCase();
		if (usuarioRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo");
		}
		Usuario usuario = new Usuario();
		usuario.setNombre(datos.getNombre().trim());
		usuario.setEmail(email);
		usuario.setTelefono(datos.getTelefono());
		usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
		usuario.setAceptaDatos(true);
		usuario.setActivo(true);
		usuario.setFechaRegistro(LocalDateTime.now());
		usuario.getRoles().add(rolCliente());
		return usuarioRepository.save(usuario);
	}

	/**
	 * Revisa el correo y la contraseña.
	 *
	 * @param datos correo y contraseña
	 * @return el usuario, si los datos son correctos
	 * @throws ResponseStatusException 401 si son incorrectos, 403 si la cuenta está desactivada
	 */
	public Usuario login(LoginRequest datos) {
		Usuario usuario = usuarioRepository.findByEmail(datos.getEmail().trim().toLowerCase()).orElse(null);

		// Mismo mensaje si el correo no existe o si la contraseña está mal, para no dar
		// pistas de qué correos están registrados. password es null si la cuenta es de Google.
		if (usuario == null || usuario.getPassword() == null
				|| !passwordEncoder.matches(datos.getPassword(), usuario.getPassword())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
		}
		if (!usuario.getActivo()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La cuenta está desactivada");
		}
		return usuario;
	}

	/**
	 * Inicio de sesión con Google (OAuth 2.0). Google ya comprobó quién es la
	 * persona; si es la primera vez que entra, se le crea una cuenta de cliente
	 * sin contraseña.
	 *
	 * Si ya existe una cuenta con ese correo creada con contraseña, NO se usa:
	 * el registro no comprueba que el correo sea de quien se registra, así que
	 * alguien pudo crearla antes a nombre de otra persona.
	 *
	 * @param email  correo que entregó Google
	 * @param nombre nombre que entregó Google
	 * @return el usuario (nuevo o existente), o null si el correo ya tiene una cuenta con contraseña
	 */
	public Usuario loginConGoogle(String email, String nombre) {
		String correo = email.trim().toLowerCase();
		Usuario existente = usuarioRepository.findByEmail(correo).orElse(null);
		if (existente != null) {
			return existente.getPassword() == null ? existente : null;
		}

		Usuario usuario = new Usuario();
		usuario.setNombre(nombre != null ? nombre : correo);
		usuario.setEmail(correo);
		usuario.setPassword(null);
		// En la pantalla de login se informa que al continuar con Google se acepta el aviso de privacidad.
		usuario.setAceptaDatos(true);
		usuario.setActivo(true);
		usuario.setFechaRegistro(LocalDateTime.now());
		usuario.getRoles().add(rolCliente());
		return usuarioRepository.save(usuario);
	}

	/**
	 * @param email correo del usuario que tiene la sesión iniciada (sale del token)
	 * @return el usuario
	 * @throws ResponseStatusException 401 si ya no existe
	 */
	public Usuario buscarPorEmail(String email) {
		return usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario ya no existe"));
	}

	private Rol rolCliente() {
		return rolRepository.findByNombre("CLIENTE")
				.orElseThrow(() -> new IllegalStateException("Falta el rol CLIENTE en la base de datos"));
	}
}
