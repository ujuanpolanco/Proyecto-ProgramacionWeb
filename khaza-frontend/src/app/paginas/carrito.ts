import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

import { Pedido, PedidoNuevo } from '../modelos';
import { ApiService, mensajeDeError } from '../servicios/api.service';
import { AuthService } from '../servicios/auth.service';
import { CarritoService } from '../servicios/carrito.service';

// Número de WhatsApp de KHAZA, con el indicativo de Colombia (57), sin espacios ni signo +.
// TODO: cambiar por el número real del emprendimiento.
const WHATSAPP_KHAZA = '573000000000';

// Toda la compra en una sola página:
//   1. Arriba, los productos del carrito (cambiar cantidad, quitar, total).
//   2. Abajo, los datos de entrega y la nota de personalización (pide haber iniciado sesión).
//   3. Al confirmar, el pedido se guarda en la API y aparece el botón para avisar por WhatsApp,
//      porque KHAZA coordina el pago y el envío por ese medio.
@Component({
  selector: 'app-carrito',
  // Eager: Angular vuelve a revisar este componente cada vez que cambia algo (por ejemplo, cuando
  // llega la respuesta de la API). Desde Angular 22 el valor por defecto es OnPush.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [FormsModule, RouterLink, CurrencyPipe, TranslatePipe],
  templateUrl: './carrito.html',
})
export class Carrito {

  // Datos del formulario de entrega
  direccion = '';
  telefono = '';
  nota = '';

  enviando = false;  // evita que se envíe dos veces si el cliente hace doble clic
  error = '';

  // Se llenan cuando el pedido queda registrado
  pedidoCreado?: Pedido;
  enlaceWhatsapp = '';

  constructor(public carrito: CarritoService, public auth: AuthService, private api: ApiService) {
    this.telefono = this.auth.usuario?.telefono ?? '';
  }

  // Cuando el cliente cambia una cantidad: se deja en número entero; si es 0 o menos se quita
  // el producto, y si pasa del stock (o de 10 para bajo pedido) se deja en el máximo.
  cambiarCantidad(posicion: number): void {
    const item = this.carrito.items[posicion];
    item.cantidad = Math.floor(item.cantidad);
    if (!(item.cantidad >= 1)) {
      this.carrito.quitar(posicion);
      return;
    }
    const maximo = item.producto.bajoPedido ? 10 : item.producto.stock;
    if (item.cantidad > maximo) {
      item.cantidad = maximo;
    }
    this.carrito.guardar();
  }

  confirmar(formulario: NgForm): void {
    if (formulario.invalid || this.carrito.items.length === 0 || this.enviando) {
      return;
    }

    // A la API solo se le manda qué productos, en qué talla y cuántos.
    // Los precios y el total los calcula el servidor.
    const pedido: PedidoNuevo = {
      direccion: this.direccion,
      telefono: this.telefono,
      nota: this.nota,
      items: this.carrito.items.map(item => ({
        productoId: item.producto.id,
        talla: item.talla,
        cantidad: item.cantidad,
      })),
    };

    this.enviando = true;
    this.error = '';
    this.api.crearPedido(pedido).subscribe({
      next: creado => {
        this.pedidoCreado = creado;
        this.enlaceWhatsapp = this.armarEnlaceWhatsapp(creado);
        this.carrito.vaciar();
        this.enviando = false;
      },
      // Por ejemplo 409 "Solo quedan 2 unidades de..." si alguien compró antes
      error: (e: HttpErrorResponse) => {
        this.error = mensajeDeError(e);
        this.enviando = false;
      },
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
