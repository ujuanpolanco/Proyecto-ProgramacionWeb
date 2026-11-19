import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

import { PedidosPorEstado, VentaProducto } from '../modelos';
import { ApiService, mensajeDeError } from '../servicios/api.service';

// Reporte de ventas para el administrador: las mismas tablas del PDF, en pantalla,
// y un botón para descargar el PDF que genera el back-end con OpenPDF.
@Component({
  selector: 'app-admin-reportes',
  // Eager: Angular vuelve a revisar este componente cada vez que cambia algo (por ejemplo, cuando
  // llega la respuesta de la API). Desde Angular 22 el valor por defecto es OnPush.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [CurrencyPipe, TranslatePipe],
  templateUrl: './admin-reportes.html',
})
export class AdminReportes implements OnInit {

  ventas: VentaProducto[] = [];
  estados: PedidosPorEstado[] = [];
  descargando = false;
  error = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.ventasPorProducto().subscribe({
      next: datos => {
        this.ventas = datos;
      },
      error: (e: HttpErrorResponse) => {
        this.error = mensajeDeError(e);
      },
    });
    this.api.pedidosPorEstado().subscribe(datos => {
      this.estados = datos;
    });
  }

  totalUnidades(): number {
    return this.ventas.reduce((suma, v) => suma + v.unidades, 0);
  }

  totalVendido(): number {
    return this.ventas.reduce((suma, v) => suma + v.totalVendido, 0);
  }

  // El PDF se pide con HttpClient (para que lleve el token) y llega como Blob.
  // Para descargarlo se crea un enlace temporal que apunta a ese Blob y se le hace clic.
  descargarPdf(): void {
    this.descargando = true;
    this.api.reporteVentasPdf().subscribe({
      next: archivo => {
        const url = URL.createObjectURL(archivo);
        const enlace = document.createElement('a');
        enlace.href = url;
        enlace.download = 'reporte-ventas-khaza.pdf';
        enlace.click();
        URL.revokeObjectURL(url);
        this.descargando = false;
      },
      error: (e: HttpErrorResponse) => {
        this.error = mensajeDeError(e);
        this.descargando = false;
      },
    });
  }
}
