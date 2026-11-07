import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

import { EstadoPedido, Pedido } from '../modelos';
import { ApiService } from '../servicios/api.service';
import { AuthService } from '../servicios/auth.service';

// Historial de pedidos del cliente y en qué estado va cada uno.
@Component({
  selector: 'app-mis-pedidos',
  imports: [RouterLink, CurrencyPipe, DatePipe],
  templateUrl: './mis-pedidos.html',
})
export class MisPedidos implements OnInit {

  pedidos: Pedido[] = [];

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit(): void {
    this.api.listarPedidosDeUsuario(this.auth.usuario!.id).subscribe(datos => {
      this.pedidos = datos;
    });
  }

  // Color de la etiqueta según el estado
  colorEstado(estado: EstadoPedido): string {
    if (estado === 'PENDIENTE') return 'text-bg-warning';
    if (estado === 'ENVIADO') return 'text-bg-primary';
    return 'text-bg-success';
  }
}
