import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { AuthService } from './auth.service';

// Un interceptor revisa TODAS las peticiones HTTP antes de que salgan.
// 1. Si hay sesión, agrega el encabezado "Authorization: Bearer <token>".
// 2. Si la API responde 401 (token vencido o inválido), cierra la sesión y manda al login.
//    Solo la primera vez (auth.estaLogueado()): si dos peticiones fallan juntas, no se repite.
export const tokenInterceptor: HttpInterceptorFn = (peticion, siguiente) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const token = auth.token();
  if (token) {
    peticion = peticion.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }

  return siguiente(peticion).pipe(
    catchError((error: HttpErrorResponse) => {
      // El 401 del login significa "clave incorrecta" y el de /auth/yo lo maneja la página de
      // Google: esos dos casos los resuelve cada página.
      const lomanejaLaPagina = peticion.url.endsWith('/auth/login') || peticion.url.endsWith('/auth/yo');
      if (error.status === 401 && token && auth.estaLogueado() && !lomanejaLaPagina) {
        auth.logout();
        router.navigate(['/login'], { queryParams: { volver: router.url } });
      }
      return throwError(() => error);
    }),
  );
};
