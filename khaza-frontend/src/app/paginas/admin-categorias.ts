import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

import { Categoria } from '../modelos';
import { ApiService } from '../servicios/api.service';

// CRUD de categorías en una sola página: el formulario de arriba sirve para crear y para editar.
// Funciona igual que la página de productos.
@Component({
  selector: 'app-admin-categorias',
  imports: [FormsModule],
  templateUrl: './admin-categorias.html',
})
export class AdminCategorias implements OnInit {

  categorias: Categoria[] = [];
  categoria: Categoria = this.nueva(); // la que está en el formulario

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.api.listarCategorias().subscribe(datos => {
      this.categorias = datos;
    });
  }

  // Una categoría vacía para el formulario. id 0 = todavía no existe.
  nueva(): Categoria {
    return { id: 0, nombre: '', descripcion: '' };
  }

  editar(categoria: Categoria): void {
    this.categoria = { ...categoria };
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
    const peticion = this.categoria.id === 0
      ? this.api.crearCategoria(this.categoria)
      : this.api.actualizarCategoria(this.categoria.id, this.categoria);

    peticion.subscribe(() => {
      this.cancelar(formulario);
      this.cargar();
    });
  }

  eliminar(categoria: Categoria): void {
    if (confirm(`¿Eliminar la categoría "${categoria.nombre}"?`)) {
      this.api.eliminarCategoria(categoria.id).subscribe(() => this.cargar());
    }
  }
}
