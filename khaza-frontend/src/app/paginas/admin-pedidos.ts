import { CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

import { EstadoPedido, Pedido } from '../modelos';
import { ApiService, mensajeDeError } from '../servicios/api.service';

// Gestión de pedidos: el administrador ve todos y les cambia el estado.
@Component({
  selector: 'app-admin-pedidos',
  // Eager: Angular vuelve a revisar este componente cada vez que cambia algo (por ejemplo, cuando
  // llega la respuesta de la API). Desde Angular 22 el valor por defecto es OnPush.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [FormsModule, CurrencyPipe, DatePipe, TranslatePipe],
  templateUrl: './admin-pedidos.html',
})
export class AdminPedidos implements OnInit {

  pedidos: Pedido[] = [];
  estados: EstadoPedido[] = ['PENDIENTE', 'ENVIADO', 'ENTREGADO'];
  error = '';
  mensaje = '';  // "Pedido #3 marcado como Enviado"

  constructor(private api: ApiService, private traductor: TranslateService) {}

  // Formato de las fechas según el idioma: "3 nov 2026" o "Nov 3, 2026"
  formatoFecha(): string {
    return this.traductor.getCurrentLang() === 'en' ? 'en-US' : 'es-CO';
  }

  ngOnInit(): void {
    this.api.listarPedidos().subscribe({
      next: datos => {
        this.pedidos = datos;
      },
      error: (e: HttpErrorResponse) => {
        this.error = mensajeDeError(e);
      },
    });
  }

  cambiarEstado(pedido: Pedido, estado: EstadoPedido, selector: HTMLSelectElement): void {
    // Pregunta antes de guardar; si el propietario cancela, el selector vuelve a como estaba
    const pregunta = this.traductor.instant('admin.pedidos.confirmar',
      { id: pedido.id, estado: this.traductor.instant('estado.' + estado) });
    if (!confirm(pregunta)) {
      selector.value = pedido.estado;
      return;
    }
    const anterior = pedido.estado;
    pedido.estado = estado;
    this.error = '';
    this.mensaje = '';
    this.api.cambiarEstadoPedido(pedido.id, estado).subscribe({
      next: () => {
        this.mensaje = this.traductor.instant('admin.pedidos.guardado',
          { id: pedido.id, estado: this.traductor.instant('estado.' + estado) });
      },
      error: (e: HttpErrorResponse) => {
        pedido.estado = anterior; // si falla, el selector vuelve a como estaba
        this.error = mensajeDeError(e);
      },
    });
  }

  totalVendido(): number {
    return this.pedidos.reduce((suma, p) => suma + p.total, 0);
  }

  pendientes(): number {
    return this.pedidos.filter(p => p.estado === 'PENDIENTE').length;
  }
}
