import { CurrencyPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import $ from 'jquery';

import { Producto } from '../modelos';
import { ApiService } from '../servicios/api.service';
import { CarritoService } from '../servicios/carrito.service';

// Detalle de un producto: foto, datos, talla, cantidad y botón para agregar al carrito.
@Component({
  selector: 'app-detalle',
  imports: [FormsModule, RouterLink, CurrencyPipe],
  templateUrl: './detalle.html',
})
export class Detalle implements OnInit {

  producto?: Producto;
  tallas: string[] = [];
  tallaElegida = '';
  cantidad = 1;

  constructor(private route: ActivatedRoute, private api: ApiService, private carrito: CarritoService) {}

  ngOnInit(): void {
    // El id viene en la URL: /producto/3
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.api.buscarProducto(id).subscribe(producto => {
      this.producto = producto;
      if (producto) {
        this.tallas = producto.tallas.split(',');  // "S,M,L" → ["S", "M", "L"]
        this.tallaElegida = this.tallas[0];
      }
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

  agregarAlCarrito(): void {
    if (this.cantidad < 1 || this.cantidad > this.maximo()) {
      return;
    }
    this.carrito.agregar(this.producto!, this.tallaElegida, this.cantidad);

    // jQuery: el aviso aparece, espera 2 segundos y desaparece.
    $('#aviso-carrito').stop(true, true).fadeIn(200).delay(2000).fadeOut(400);
  }
}
