import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

import { Categoria, Producto } from '../modelos';
import { ApiService, mensajeDeError } from '../servicios/api.service';

// Página de inicio: muestra los productos y deja buscarlos y filtrarlos.
@Component({
  selector: 'app-catalogo',
  // Eager: Angular vuelve a revisar este componente cada vez que cambia algo (por ejemplo, cuando
  // llega la respuesta de la API). Desde Angular 22 el valor por defecto es OnPush.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [FormsModule, RouterLink, CurrencyPipe, TranslatePipe],
  templateUrl: './catalogo.html',
})
export class Catalogo implements OnInit {

  productos: Producto[] = [];
  categorias: Categoria[] = [];
  cargando = true;
  error = '';

  // Valores de los filtros (unidos a los campos del HTML con [(ngModel)])
  busqueda = '';
  categoriaId = 0;    // 0 = todas las categorías
  precioMaximo = 0;   // 0 = cualquier precio

  constructor(private api: ApiService) {}

  // Se ejecuta una vez, cuando se abre la página.
  ngOnInit(): void {
    this.api.listarProductos().subscribe({
      next: datos => {
        this.productos = datos.filter(p => p.activo);
        this.cargando = false;
      },
      error: (e: HttpErrorResponse) => {
        this.error = mensajeDeError(e);
        this.cargando = false;
      },
    });
    this.api.listarCategorias().subscribe(datos => {
      this.categorias = datos;
    });
  }

  // El HTML la llama para dibujar las tarjetas, así que se recalcula cada vez que cambia un filtro.
  productosFiltrados(): Producto[] {
    const texto = this.busqueda.trim().toLowerCase();
    return this.productos.filter(p =>
      p.nombre.toLowerCase().includes(texto) &&
      (this.categoriaId === 0 || p.categoria.id === this.categoriaId) &&
      (this.precioMaximo === 0 || p.precio <= this.precioMaximo)
    );
  }

  limpiarFiltros(): void {
    this.busqueda = '';
    this.categoriaId = 0;
    this.precioMaximo = 0;
  }
}
