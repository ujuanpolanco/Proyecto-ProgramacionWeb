import { Routes } from '@angular/router';

import { adminGuard, clienteGuard } from './guards';
import { AdminCategorias } from './paginas/admin-categorias';
import { AdminPedidos } from './paginas/admin-pedidos';
import { AdminProductos } from './paginas/admin-productos';
import { AdminReportes } from './paginas/admin-reportes';
import { Carrito } from './paginas/carrito';
import { Catalogo } from './paginas/catalogo';
import { Detalle } from './paginas/detalle';
import { Login } from './paginas/login';
import { MisPedidos } from './paginas/mis-pedidos';
import { Oauth2 } from './paginas/oauth2';
import { Privacidad } from './paginas/privacidad';
import { Registro } from './paginas/registro';

// Cada ruta une una URL con una página. "title" es la clave del título de la pestaña del navegador
// (lo traduce titulo-traducido.ts).
export const routes: Routes = [
  // Públicas
  { path: '', component: Catalogo, title: 'titulos.catalogo' },
  { path: 'producto/:id', component: Detalle, title: 'titulos.producto' },
  { path: 'carrito', component: Carrito, title: 'titulos.carrito' },
  { path: 'login', component: Login, title: 'titulos.login' },
  { path: 'registro', component: Registro, title: 'titulos.registro' },
  { path: 'privacidad', component: Privacidad, title: 'titulos.privacidad' },
  // A esta ruta vuelve el back-end después de iniciar sesión con Google
  { path: 'oauth2', component: Oauth2, title: 'titulos.oauth2' },

  // Solo con sesión iniciada
  { path: 'mis-pedidos', component: MisPedidos, canActivate: [clienteGuard], title: 'titulos.misPedidos' },

  // Solo administrador
  { path: 'admin/productos', component: AdminProductos, canActivate: [adminGuard], title: 'titulos.adminProductos' },
  { path: 'admin/categorias', component: AdminCategorias, canActivate: [adminGuard], title: 'titulos.adminCategorias' },
  { path: 'admin/pedidos', component: AdminPedidos, canActivate: [adminGuard], title: 'titulos.adminPedidos' },
  { path: 'admin/reportes', component: AdminReportes, canActivate: [adminGuard], title: 'titulos.adminReportes' },

  // Cualquier otra URL lleva al catálogo
  { path: '**', redirectTo: '' },
];
