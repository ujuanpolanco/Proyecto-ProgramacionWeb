import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../servicios/auth.service';
import { CarritoService } from '../servicios/carrito.service';

@Component({
  selector: 'app-registro',
  imports: [FormsModule, RouterLink],
  templateUrl: './registro.html',
})
export class Registro {

  nombre = '';
  email = '';
  telefono = '';
  password = '';
  confirmacion = '';
  aceptaDatos = false; // autorización de tratamiento de datos (Ley 1581 de 2012)
  error = '';

  constructor(private auth: AuthService, private carrito: CarritoService, private router: Router) {}

  registrar(formulario: NgForm): void {
    if (formulario.invalid || this.password !== this.confirmacion) {
      return;
    }
    this.auth.registrar(this.nombre, this.email, this.telefono, this.password).subscribe({
      // Si ya tenía productos en el carrito, lo llevamos a terminar la compra.
      next: () => this.router.navigate([this.carrito.items.length > 0 ? '/carrito' : '/']),
      error: (e: Error) => {
        this.error = e.message;
      },
    });
  }
}
