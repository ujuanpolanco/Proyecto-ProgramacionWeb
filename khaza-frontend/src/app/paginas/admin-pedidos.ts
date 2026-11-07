import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { EstadoPedido, Pedido } from '../modelos';
import { ApiService } from '../servicios/api.service';

// Gestión de pedidos: el administrador ve todos y les cambia el estado.
@Component({
  selector: 'app-admin-pedidos',
  imports: [FormsModule, CurrencyPipe, DatePipe],
  templateUrl: './admin-pedidos.html',
})
export class AdminPedidos implements OnInit {

  pedidos: Pedido[] = [];
  estados: EstadoPedido[] = ['PENDIENTE', 'ENVIADO', 'ENTREGADO'];

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.listarPedidos().subscribe(datos => {
      this.pedidos = datos;
    });
  }

  cambiarEstado(pedido: Pedido, estado: EstadoPedido): void {
    this.api.cambiarEstadoPedido(pedido.id, estado).subscribe(actualizado => {
      pedido.estado = actualizado.estado;
    });
  }

  totalVendido(): number {
    return this.pedidos.reduce((suma, p) => suma + p.total, 0);
  }

  pendientes(): number {
    return this.pedidos.filter(p => p.estado === 'PENDIENTE').length;
  }
}
