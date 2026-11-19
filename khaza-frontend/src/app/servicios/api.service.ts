import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { Categoria, EstadoPedido, Pedido, PedidoNuevo, PedidosPorEstado, Producto, VentaProducto } from '../modelos';

// Dirección de la API de Spring Boot. Es relativa: el servidor de Angular (ng serve) reenvía
// todo lo que empieza por /api al back-end en https://localhost:8443 (ver proxy.conf.json).
// Así el navegador solo habla con https://localhost:4200.
export const API = '/api';

// Todo lo que tiene que ver con los datos de la tienda: productos, categorías, pedidos y
// reportes. Cada método hace una petición HTTP a la API y devuelve un Observable; la página
// que lo llama se suscribe para recibir la respuesta. El token JWT lo agrega solo el
// interceptor (token.interceptor.ts).
@Injectable({ providedIn: 'root' })
export class ApiService {

  constructor(private http: HttpClient) {}

  // ==================== PRODUCTOS ====================

  listarProductos(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${API}/productos`);
  }

  buscarProducto(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${API}/productos/${id}`);
  }

  crearProducto(producto: Producto): Observable<Producto> {
    return this.http.post<Producto>(`${API}/productos`, producto);
  }

  actualizarProducto(id: number, producto: Producto): Observable<Producto> {
    return this.http.put<Producto>(`${API}/productos/${id}`, producto);
  }

  eliminarProducto(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/productos/${id}`);
  }

  // ==================== CATEGORÍAS ====================

  listarCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(`${API}/categorias`);
  }

  crearCategoria(categoria: Categoria): Observable<Categoria> {
    return this.http.post<Categoria>(`${API}/categorias`, categoria);
  }

  actualizarCategoria(id: number, categoria: Categoria): Observable<Categoria> {
    return this.http.put<Categoria>(`${API}/categorias/${id}`, categoria);
  }

  eliminarCategoria(id: number): Observable<void> {
    return this.http.delete<void>(`${API}/categorias/${id}`);
  }

  // ==================== PEDIDOS ====================

  // Para el administrador: todos.
  listarPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${API}/pedidos`);
  }

  // Para el cliente: los suyos (la API sabe quién es por el token).
  misPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(`${API}/pedidos/mios`);
  }

  crearPedido(pedido: PedidoNuevo): Observable<Pedido> {
    return this.http.post<Pedido>(`${API}/pedidos`, pedido);
  }

  cambiarEstadoPedido(id: number, estado: EstadoPedido): Observable<Pedido> {
    return this.http.put<Pedido>(`${API}/pedidos/${id}/estado`, null, { params: { estado } });
  }

  // ==================== REPORTES ====================

  ventasPorProducto(): Observable<VentaProducto[]> {
    return this.http.get<VentaProducto[]>(`${API}/reportes/ventas`);
  }

  pedidosPorEstado(): Observable<PedidosPorEstado[]> {
    return this.http.get<PedidosPorEstado[]>(`${API}/reportes/estados`);
  }

  // El PDF llega como archivo (Blob), no como JSON.
  reporteVentasPdf(): Observable<Blob> {
    return this.http.get(`${API}/reportes/ventas/pdf`, { responseType: 'blob' });
  }
}

// Saca el mensaje de error que mandó la API para mostrarlo en la página (con el pipe translate).
// Los mensajes del propio front son claves de traducción ('errores.sinConexion'); los que escribe
// el back-end llegan en español y el pipe translate los deja tal cual.
// - Errores de validación (400): vienen en una lista "errors" → se juntan.
// - Otros errores (404, 409...): vienen en "message".
// - Si no hay respuesta (status 0) o el proxy no encuentra el back-end (502, 504), está apagado.
export function mensajeDeError(error: HttpErrorResponse): string {
  if (error.status === 0 || error.status === 502 || error.status === 504) {
    return 'errores.sinConexion';
  }
  if (error.error?.errors?.length) {
    return error.error.errors.map((e: { defaultMessage: string }) => e.defaultMessage).join('. ');
  }
  return error.error?.message ?? 'errores.inesperado';
}
