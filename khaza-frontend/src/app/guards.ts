import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from './servicios/auth.service';

// Un guard decide si se puede entrar a una ruta.

// Rutas que necesitan sesión iniciada (mis pedidos).
// Si no hay sesión, manda al login y recuerda a dónde quería ir el usuario.
export const clienteGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.estaLogueado()) {
    return true;
  }
  return router.createUrlTree(['/login'], { queryParams: { volver: state.url } });
};

// Rutas del panel de administración: solo el rol ADMIN.
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  return auth.esAdmin() ? true : router.createUrlTree(['/']);
};
