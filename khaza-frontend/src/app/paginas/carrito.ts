import { CurrencyPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Pedido } from '../modelos';
import { ApiService } from '../servicios/api.service';
import { AuthService } from '../servicios/auth.service';
import { CarritoService } from '../servicios/carrito.service';

// Número de WhatsApp de KHAZA, con el indicativo de Colombia (57), sin espacios ni signo +.
// TODO: cambiar por el número real del emprendimiento.
const WHATSAPP_KHAZA = '573000000000';

// Toda la compra en una sola página:
//   1. Arriba, los productos del carrito (cambiar cantidad, quitar, total).
//   2. Abajo, los datos de entrega y la nota de personalización (pide haber iniciado sesión).
//   3. Al confirmar, el pedido se guarda y aparece el botón para avisar por WhatsApp,
//      porque KHAZA coordina el pago y el envío por ese medio.
@Component({
  selector: 'app-carrito',
  imports: [FormsModule, RouterLink, CurrencyPipe],
  templateUrl: './carrito.html',
})
export class Carrito {

  // Datos del formulario de entrega
  direccion = '';
  telefono = '';
  nota = '';

  // Se llenan cuando el pedido queda registrado
  pedidoCreado?: Pedido;
  enlaceWhatsapp = '';

  constructor(public carrito: CarritoService, public auth: AuthService, private api: ApiService) {
    this.telefono = this.auth.usuario?.telefono ?? '';
  }

  // Si el cliente escribe 0 o menos en la cantidad, el producto se quita.
  cambiarCantidad(posicion: number): void {
    if (this.carrito.items[posicion].cantidad < 1) {
      this.carrito.quitar(posicion);
    } else {
      this.carrito.guardar();
    }
  }

  confirmar(formulario: NgForm): void {
    if (formulario.invalid || this.carrito.items.length === 0) {
      return;
    }

    const pedido: Pedido = {
      id: 0, // lo asigna el servidor
      usuario: this.auth.usuario!,
      fecha: new Date().toISOString(),
      estado: 'PENDIENTE',
      total: this.carrito.total(),
      direccion: this.direccion,
      telefono: this.telefono,
      nota: this.nota,
      // Cada producto del carrito se convierte en una línea del pedido
      detalles: this.carrito.items.map(item => ({
        producto: item.producto,
        talla: item.talla,
        cantidad: item.cantidad,
        precioUnitario: item.producto.precio,
      })),
    };

    this.api.crearPedido(pedido).subscribe(creado => {
      this.pedidoCreado = creado;
      this.enlaceWhatsapp = this.armarEnlaceWhatsapp(creado);
      this.carrito.vaciar();
    });
  }

  // Arma el enlace wa.me con el mensaje ya escrito. encodeURIComponent convierte los
  // espacios, tildes y saltos de línea a un formato válido dentro de una URL.
  armarEnlaceWhatsapp(pedido: Pedido): string {
    let mensaje = `Hola KHAZA, acabo de hacer el pedido #${pedido.id}:\n`;
    for (const detalle of pedido.detalles) {
      mensaje += `- ${detalle.cantidad} x ${detalle.producto.nombre} (talla ${detalle.talla})\n`;
    }
    mensaje += `Total: $ ${pedido.total.toLocaleString('es-CO')}\n`;
    if (pedido.nota) {
      mensaje += `Personalización: ${pedido.nota}\n`;
    }
    mensaje += `Entregar en: ${pedido.direccion}`;
    return `https://wa.me/${WHATSAPP_KHAZA}?text=${encodeURIComponent(mensaje)}`;
  }
}
