import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';

import { Categoria } from '../modelos';
import { ApiService, mensajeDeError } from '../servicios/api.service';

// CRUD de categorías en una sola página: el formulario de arriba sirve para crear y para editar.
// Funciona igual que la página de productos.
@Component({
  selector: 'app-admin-categorias',
  // Eager: Angular vuelve a revisar este componente cada vez que cambia algo (por ejemplo, cuando
  // llega la respuesta de la API). Desde Angular 22 el valor por defecto es OnPush.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [FormsModule, TranslatePipe],
  templateUrl: './admin-categorias.html',
})
export class AdminCategorias implements OnInit {

  categorias: Categoria[] = [];
  categoria: Categoria = this.nueva(); // la que está en el formulario
  error = '';
  mensaje = '';

  constructor(private api: ApiService, private traductor: TranslateService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.api.listarCategorias().subscribe({
      next: datos => {
        this.categorias = datos;
      },
      error: (e: HttpErrorResponse) => {
        this.error = mensajeDeError(e);
      },
    });
  }

  // Una categoría vacía para el formulario. id 0 = todavía no existe.
  nueva(): Categoria {
    return { id: 0, nombre: '', descripcion: '' };
  }

  editar(categoria: Categoria): void {
    this.categoria = { ...categoria };
    this.error = '';
  }

  // Deja el formulario como nuevo. resetForm(...) borra los mensajes de error y pone en cada
  // campo el valor de la categoría vacía.
  cancelar(formulario: NgForm): void {
    this.categoria = this.nueva();
    formulario.resetForm(this.categoria);
  }

  guardar(formulario: NgForm): void {
    if (formulario.invalid) {
      return;
    }
    this.error = '';
    // id 0 = todavía no existe, entonces se crea; si no, se actualiza.
    const peticion = this.categoria.id === 0
      ? this.api.crearCategoria(this.categoria)
      : this.api.actualizarCategoria(this.categoria.id, this.categoria);

    peticion.subscribe({
      next: guardada => {
        this.mensaje = this.traductor.instant('admin.guardado', { nombre: guardada.nombre });
        this.cancelar(formulario);
        this.cargar();
      },
      // 409 si ya existe una categoría con ese nombre
      error: (e: HttpErrorResponse) => {
        this.error = mensajeDeError(e);
      },
    });
  }

  eliminar(categoria: Categoria): void {
    if (!confirm(this.traductor.instant('admin.confirmarEliminar', { nombre: categoria.nombre }))) {
      return;
    }
    this.error = '';
    this.api.eliminarCategoria(categoria.id).subscribe({
      next: () => {
        this.mensaje = this.traductor.instant('admin.eliminado', { nombre: categoria.nombre });
        this.cargar();
      },
      // 409 si la categoría tiene productos
      error: (e: HttpErrorResponse) => {
        this.error = mensajeDeError(e);
      },
    });
  }
}
