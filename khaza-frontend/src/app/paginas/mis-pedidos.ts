import { CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

import { EstadoPedido, Pedido } from '../modelos';
import { ApiService, mensajeDeError } from '../servicios/api.service';

// Historial de pedidos del cliente y en qué estado va cada uno.
@Component({
  selector: 'app-mis-pedidos',
  // Eager: Angular vuelve a revisar este componente cada vez que cambia algo (por ejemplo, cuando
  // llega la respuesta de la API). Desde Angular 22 el valor por defecto es OnPush.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [RouterLink, CurrencyPipe, DatePipe, TranslatePipe],
  templateUrl: './mis-pedidos.html',
})
export class MisPedidos implements OnInit {

  pedidos: Pedido[] = [];
  cargando = true;
  error = '';

  constructor(private api: ApiService, private traductor: TranslateService) {}

  // Formato de las fechas según el idioma: "3 nov 2026" o "Nov 3, 2026"
  formatoFecha(): string {
    return this.traductor.getCurrentLang() === 'en' ? 'en-US' : 'es-CO';
  }

  ngOnInit(): void {
    // La API sabe de quién son los pedidos por el token JWT
    this.api.misPedidos().subscribe({
      next: datos => {
        this.pedidos = datos;
        this.cargando = false;
      },
      error: (e: HttpErrorResponse) => {
        this.error = mensajeDeError(e);
        this.cargando = false;
      },
    });
  }

  // Color de la etiqueta según el estado
  colorEstado(estado: EstadoPedido): string {
    if (estado === 'PENDIENTE') return 'text-bg-warning';
    if (estado === 'ENVIADO') return 'text-bg-primary';
    return 'text-bg-success';
  }
}
