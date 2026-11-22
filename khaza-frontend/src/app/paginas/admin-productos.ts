import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

import { Categoria, Producto } from '../modelos';
import { ApiService, mensajeDeError } from '../servicios/api.service';

// CRUD de productos y control de inventario, en una sola página:
//   - Arriba, un formulario que sirve para crear y para editar.
//   - Abajo, la tabla con todos los productos; los de poco stock se resaltan.
// Funciona igual que la página de categorías.
@Component({
  selector: 'app-admin-productos',
  // Eager: Angular vuelve a revisar este componente cada vez que cambia algo (por ejemplo, cuando
  // llega la respuesta de la API). Desde Angular 22 el valor por defecto es OnPush.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [FormsModule, CurrencyPipe, TranslatePipe],
  templateUrl: './admin-productos.html',
})
export class AdminProductos implements OnInit {

  productos: Producto[] = [];
  categorias: Categoria[] = [];

  producto: Producto = this.nuevo(); // el que está en el formulario
  categoriaId = 0;                   // categoría elegida en el formulario
  error = '';
  mensaje = '';

  constructor(private api: ApiService, private traductor: TranslateService) {}

  ngOnInit(): void {
    this.cargar();
    this.api.listarCategorias().subscribe(datos => {
      this.categorias = datos;
    });
  }

  cargar(): void {
    this.api.listarProductos().subscribe({
      next: datos => {
        this.productos = datos;
      },
      error: (e: HttpErrorResponse) => {
        this.error = mensajeDeError(e);
      },
    });
  }

  // Un producto vacío para el formulario. id 0 = todavía no existe.
  nuevo(): Producto {
    return {
      id: 0, nombre: '', descripcion: '', precio: 0, stock: 0, material: '', decoracion: '',
      tallas: 'S,M,L', bajoPedido: false, imagen: '', activo: true,
      categoria: { id: 0, nombre: '', descripcion: '' },
    };
  }

  // Productos que no son bajo pedido y les quedan 3 unidades o menos
  nombresPocoStock(): string {
    return this.pocoStock().map(p => p.nombre).join(', ');
  }

  pocoStock(): Producto[] {
    return this.productos.filter(p => !p.bajoPedido && p.stock <= 3);
  }

  // Pasa el producto al formulario. Se usa una copia ({ ...producto }) para que la tabla
  // no cambie mientras se escribe, sino solo cuando se guarde.
  editar(producto: Producto): void {
    this.producto = { ...producto };
    this.categoriaId = producto.categoria.id;
    this.error = '';
    window.scrollTo(0, 0);
  }

  // Deja el formulario como nuevo. resetForm(...) borra los mensajes de error y pone en cada
  // campo el valor del producto vacío (los nombres de los campos son los del atributo name).
  cancelar(formulario: NgForm): void {
    this.producto = this.nuevo();
    this.categoriaId = 0;
    formulario.resetForm({ ...this.producto, categoriaId: 0 });
  }

  guardar(formulario: NgForm): void {
    if (formulario.invalid || this.categoriaId === 0) {
      return;
    }
    this.producto.categoria = this.categorias.find(c => c.id === this.categoriaId)!;
    this.error = '';

    const peticion = this.producto.id === 0
      ? this.api.crearProducto(this.producto)
      : this.api.actualizarProducto(this.producto.id, this.producto);

    peticion.subscribe({
      next: guardado => {
        this.mensaje = this.traductor.instant('admin.guardado', { nombre: guardado.nombre });
        this.cancelar(formulario);
        this.cargar();
      },
      error: (e: HttpErrorResponse) => {
        this.error = mensajeDeError(e);
      },
    });
  }

  eliminar(producto: Producto): void {
    if (!confirm(this.traductor.instant('admin.confirmarEliminar', { nombre: producto.nombre }))) {
      return;
    }
    this.error = '';
    this.api.eliminarProducto(producto.id).subscribe({
      next: () => {
        this.mensaje = this.traductor.instant('admin.eliminado', { nombre: producto.nombre });
        this.cargar();
      },
      // 409 si ya se vendió: la API pide ocultarlo en vez de borrarlo
      error: (e: HttpErrorResponse) => {
        this.error = mensajeDeError(e);
        window.scrollTo(0, 0);
      },
    });
  }
}
