import { Injectable } from '@angular/core';

import { ItemCarrito, Producto } from '../modelos';

// El carrito vive en el navegador (localStorage), no en la base de datos.
// Así no se pierde al recargar la página y no hace falta iniciar sesión para llenarlo.
@Injectable({ providedIn: 'root' })
export class CarritoService {

  items: ItemCarrito[] = JSON.parse(localStorage.getItem('carrito') ?? '[]');

  agregar(producto: Producto, talla: string, cantidad: number): void {
    // Si ya está el mismo producto con la misma talla, solo se suma la cantidad.
    const existente = this.items.find(i => i.producto.id === producto.id && i.talla === talla);
    if (existente) {
      existente.cantidad += cantidad;
    } else {
      this.items.push({ producto, talla, cantidad });
    }
    this.guardar();
  }

  quitar(posicion: number): void {
    this.items.splice(posicion, 1);
    this.guardar();
  }

  vaciar(): void {
    this.items = [];
    this.guardar();
  }

  total(): number {
    return this.items.reduce((suma, i) => suma + i.producto.precio * i.cantidad, 0);
  }

  cantidadDeProductos(): number {
    return this.items.reduce((suma, i) => suma + i.cantidad, 0);
  }

  guardar(): void {
    localStorage.setItem('carrito', JSON.stringify(this.items));
  }
}
