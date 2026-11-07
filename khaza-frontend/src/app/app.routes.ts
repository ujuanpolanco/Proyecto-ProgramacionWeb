import { Routes } from '@angular/router';

import { adminGuard, clienteGuard } from './guards';
import { AdminCategorias } from './paginas/admin-categorias';
import { AdminPedidos } from './paginas/admin-pedidos';
import { AdminProductos } from './paginas/admin-productos';
import { Carrito } from './paginas/carrito';
import { Catalogo } from './paginas/catalogo';
import { Detalle } from './paginas/detalle';
import { Login } from './paginas/login';
import { MisPedidos } from './paginas/mis-pedidos';
import { Privacidad } from './paginas/privacidad';
import { Registro } from './paginas/registro';

// Cada ruta une una URL con una página. "title" es el texto de la pestaña del navegador.
export const routes: Routes = [
  // Públicas
  { path: '', component: Catalogo, title: 'KHAZA | Sombreros artesanales' },
  { path: 'producto/:id', component: Detalle, title: 'Producto | KHAZA' },
  { path: 'carrito', component: Carrito, title: 'Carrito | KHAZA' },
  { path: 'login', component: Login, title: 'Iniciar sesión | KHAZA' },
  { path: 'registro', component: Registro, title: 'Crear cuenta | KHAZA' },
  { path: 'privacidad', component: Privacidad, title: 'Aviso de privacidad | KHAZA' },

  // Solo con sesión iniciada
  { path: 'mis-pedidos', component: MisPedidos, canActivate: [clienteGuard], title: 'Mis pedidos | KHAZA' },

  // Solo administrador
  { path: 'admin/productos', component: AdminProductos, canActivate: [adminGuard], title: 'Productos | Admin KHAZA' },
  { path: 'admin/categorias', component: AdminCategorias, canActivate: [adminGuard], title: 'Categorías | Admin KHAZA' },
  { path: 'admin/pedidos', component: AdminPedidos, canActivate: [adminGuard], title: 'Pedidos | Admin KHAZA' },

  // Cualquier otra URL lleva al catálogo
  { path: '**', redirectTo: '' },
];
