import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { RespuestaLogin, Usuario } from '../modelos';
import { API } from './api.service';

// Inicio de sesión con la API.
// Al entrar, la API responde un token JWT y los datos del usuario. Los dos se guardan en
// localStorage: el token lo agrega el interceptor a cada petición, y el usuario sirve para
// mostrar su nombre y decidir qué menús se ven.
@Injectable({ providedIn: 'root' })
export class AuthService {

  usuario: Usuario | null = JSON.parse(localStorage.getItem('usuario') ?? 'null');

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<RespuestaLogin> {
    return this.http.post<RespuestaLogin>(`${API}/auth/login`, { email, password })
      .pipe(tap(respuesta => this.guardarSesion(respuesta.token, respuesta.usuario)));
  }

  registrar(nombre: string, email: string, telefono: string, password: string, aceptaDatos: boolean): Observable<RespuestaLogin> {
    return this.http.post<RespuestaLogin>(`${API}/auth/registro`, { nombre, email, telefono, password, aceptaDatos })
      .pipe(tap(respuesta => this.guardarSesion(respuesta.token, respuesta.usuario)));
  }

  // Después de entrar con Google llega solo el token; con él se piden los datos del usuario.
  completarLoginGoogle(token: string): Observable<Usuario> {
    localStorage.setItem('token', token);
    return this.http.get<Usuario>(`${API}/auth/yo`)
      .pipe(tap(usuario => this.guardarSesion(token, usuario)));
  }

  // Dirección a la que hay que ir para iniciar sesión con Google (OAuth 2.0).
  // También pasa por el proxy de Angular hacia el back-end.
  urlLoginGoogle(): string {
    return '/oauth2/authorization/google';
  }

  logout(): void {
    this.usuario = null;
    localStorage.removeItem('usuario');
    localStorage.removeItem('token');
  }

  token(): string | null {
    return localStorage.getItem('token');
  }

  estaLogueado(): boolean {
    return this.usuario !== null;
  }

  esAdmin(): boolean {
    return this.usuario?.rol === 'ADMIN';
  }

  private guardarSesion(token: string, usuario: Usuario): void {
    this.usuario = usuario;
    localStorage.setItem('token', token);
    localStorage.setItem('usuario', JSON.stringify(usuario));
  }
}
