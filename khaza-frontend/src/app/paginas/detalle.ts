import { CurrencyPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import $ from 'jquery';

import { Producto } from '../modelos';
import { ApiService } from '../servicios/api.service';
import { CarritoService } from '../servicios/carrito.service';

// Detalle de un producto: foto, datos, talla, cantidad y botón para agregar al carrito.
@Component({
  selector: 'app-detalle',
  // Eager: Angular vuelve a revisar este componente cada vez que cambia algo (por ejemplo, cuando
  // llega la respuesta de la API). Desde Angular 22 el valor por defecto es OnPush.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [FormsModule, RouterLink, CurrencyPipe, TranslatePipe],
  templateUrl: './detalle.html',
})
export class Detalle implements OnInit {

  producto?: Producto;
  cargando = true;
  tallas: string[] = [];
  tallaElegida = '';
  cantidad = 1;
  // Si lo que ya está en el carrito más lo nuevo supera el máximo
  superaStock: { enCarrito: number; disponibles: number } | null = null;

  constructor(private route: ActivatedRoute, private api: ApiService, private carrito: CarritoService) {}

  ngOnInit(): void {
    // El id viene en la URL: /producto/3
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.api.buscarProducto(id).subscribe({
      next: producto => {
        // Un producto oculto (activo = false) no se muestra en la tienda
        if (producto.activo) {
          this.producto = producto;
          this.tallas = producto.tallas.split(',');  // "S,M,L" → ["S", "M", "L"]
          this.tallaElegida = this.tallas[0];
        }
        this.cargando = false;
      },
      // 404: el producto no existe
      error: () => {
        this.cargando = false;
      },
    });
  }

  // Se puede comprar si hay stock o si se fabrica bajo pedido.
  disponible(): boolean {
    return !!this.producto && (this.producto.bajoPedido || this.producto.stock > 0);
  }

  // Máximo que se puede pedir de una vez.
  maximo(): number {
    return this.producto!.bajoPedido ? 10 : this.producto!.stock;
  }

  // La cantidad tiene que ser un número entero entre 1 y el máximo.
  cantidadValida(): boolean {
    return Number.isInteger(this.cantidad) && this.cantidad >= 1 && this.cantidad <= this.maximo();
  }

  agregarAlCarrito(): void {
    if (!this.cantidadValida()) {
      return;
    }
    // También se cuenta lo que ya está en el carrito (de cualquier talla)
    const enCarrito = this.carrito.cantidadDe(this.producto!.id);
    if (enCarrito + this.cantidad > this.maximo()) {
      this.superaStock = { enCarrito, disponibles: this.maximo() - enCarrito };
      return;
    }
    this.superaStock = null;
    this.carrito.agregar(this.producto!, this.tallaElegida, this.cantidad);

    // jQuery: el aviso aparece, espera 2 segundos y desaparece.
    $('#aviso-carrito').stop(true, true).fadeIn(200).delay(2000).fadeOut(400);
  }
}
