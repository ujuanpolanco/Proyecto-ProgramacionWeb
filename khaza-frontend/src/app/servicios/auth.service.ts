import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';

import { USUARIOS } from '../datos-prueba';
import { Usuario } from '../modelos';

// Inicio de sesión SIMULADO (Fase 1): compara contra la lista de usuarios de prueba.
// En la Fase 5 login() llamará al back-end, que responde con un token JWT.
@Injectable({ providedIn: 'root' })
export class AuthService {

  usuario: Usuario | null = JSON.parse(localStorage.getItem('usuario') ?? 'null');

  login(email: string, password: string): Observable<Usuario> {
    const encontrado = USUARIOS.find(u => u.email === email && u.password === password);
    if (!encontrado) {
      return throwError(() => new Error('Correo o contraseña incorrectos'));
    }
    // Se guarda el usuario SIN la contraseña.
    const usuario: Usuario = {
      id: encontrado.id,
      nombre: encontrado.nombre,
      email: encontrado.email,
      telefono: encontrado.telefono,
      rol: encontrado.rol,
    };
    this.guardarSesion(usuario);
    return of(usuario);
  }

  registrar(nombre: string, email: string, telefono: string, password: string): Observable<Usuario> {
    if (USUARIOS.some(u => u.email === email)) {
      return throwError(() => new Error('Ya existe una cuenta con ese correo'));
    }
    const usuario: Usuario = { id: USUARIOS.length + 1, nombre, email, telefono, rol: 'CLIENTE' };
    USUARIOS.push({ ...usuario, password });
    this.guardarSesion(usuario);
    return of(usuario);
  }

  logout(): void {
    this.usuario = null;
    localStorage.removeItem('usuario');
  }

  estaLogueado(): boolean {
    return this.usuario !== null;
  }

  esAdmin(): boolean {
    return this.usuario?.rol === 'ADMIN';
  }

  private guardarSesion(usuario: Usuario): void {
    this.usuario = usuario;
    localStorage.setItem('usuario', JSON.stringify(usuario));
  }
}
