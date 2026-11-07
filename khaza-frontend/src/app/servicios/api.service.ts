import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

import { CATEGORIAS, PEDIDOS, PRODUCTOS } from '../datos-prueba';
import { Categoria, EstadoPedido, Pedido, Producto } from '../modelos';

// Todo lo que tiene que ver con los datos de la tienda: productos, categorías y pedidos.
//
// Por ahora trabaja sobre las listas de datos-prueba.ts. Cada método devuelve un Observable
// con of(...) para que en la Fase 4 solo haya que cambiar of(...) por una llamada al back-end
// (this.http.get, post, put, delete). Las páginas no tienen que cambiar nada.
@Injectable({ providedIn: 'root' })
export class ApiService {

  // ==================== PRODUCTOS ====================

  listarProductos(): Observable<Producto[]> {
    return of(PRODUCTOS);
  }

  buscarProducto(id: number): Observable<Producto | undefined> {
    return of(PRODUCTOS.find(p => p.id === id));
  }

  crearProducto(producto: Producto): Observable<Producto> {
    // Se guarda una COPIA ({ ...producto }) y no el mismo objeto del formulario; si no,
    // al limpiar el formulario también se borraría lo que quedó en la lista.
    const nuevo = { ...producto, id: this.siguienteId(PRODUCTOS) };
    PRODUCTOS.push(nuevo);
    return of(nuevo);
  }

  actualizarProducto(id: number, producto: Producto): Observable<Producto> {
    const posicion = PRODUCTOS.findIndex(p => p.id === id);
    PRODUCTOS[posicion] = { ...producto, id };
    return of(PRODUCTOS[posicion]);
  }

  eliminarProducto(id: number): Observable<void> {
    PRODUCTOS.splice(PRODUCTOS.findIndex(p => p.id === id), 1);
    return of(undefined);
  }

  // ==================== CATEGORÍAS ====================

  listarCategorias(): Observable<Categoria[]> {
    return of(CATEGORIAS);
  }

  crearCategoria(categoria: Categoria): Observable<Categoria> {
    const nueva = { ...categoria, id: this.siguienteId(CATEGORIAS) };
    CATEGORIAS.push(nueva);
    return of(nueva);
  }

  actualizarCategoria(id: number, categoria: Categoria): Observable<Categoria> {
    const posicion = CATEGORIAS.findIndex(c => c.id === id);
    CATEGORIAS[posicion] = { ...categoria, id };
    return of(CATEGORIAS[posicion]);
  }

  eliminarCategoria(id: number): Observable<void> {
    CATEGORIAS.splice(CATEGORIAS.findIndex(c => c.id === id), 1);
    return of(undefined);
  }

  // ==================== PEDIDOS ====================

  // Para el administrador: todos, del más reciente al más antiguo.
  listarPedidos(): Observable<Pedido[]> {
    return of([...PEDIDOS].reverse());
  }

  // Para el cliente: solo los suyos.
  listarPedidosDeUsuario(usuarioId: number): Observable<Pedido[]> {
    return of(PEDIDOS.filter(p => p.usuario.id === usuarioId).reverse());
  }

  crearPedido(pedido: Pedido): Observable<Pedido> {
    const nuevo = { ...pedido, id: this.siguienteId(PEDIDOS) };
    PEDIDOS.push(nuevo);

    // Simulación del trigger trg_descontar_stock. En la Fase 2 esto lo hace MySQL solo.
    for (const detalle of nuevo.detalles) {
      if (!detalle.producto.bajoPedido) {
        detalle.producto.stock -= detalle.cantidad;
      }
    }
    return of(nuevo);
  }

  cambiarEstadoPedido(id: number, estado: EstadoPedido): Observable<Pedido> {
    const pedido = PEDIDOS.find(p => p.id === id)!;
    pedido.estado = estado;
    return of(pedido);
  }

  // El id que le toca a un registro nuevo: el mayor que haya + 1 (en MySQL lo hace AUTO_INCREMENT).
  private siguienteId(lista: { id: number }[]): number {
    return Math.max(0, ...lista.map(x => x.id)) + 1;
  }
}
