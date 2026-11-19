import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

import { mensajeDeError } from '../servicios/api.service';
import { AuthService } from '../servicios/auth.service';
import { CarritoService } from '../servicios/carrito.service';

@Component({
  selector: 'app-registro',
  // Eager: Angular vuelve a revisar este componente cada vez que cambia algo (por ejemplo, cuando
  // llega la respuesta de la API). Desde Angular 22 el valor por defecto es OnPush.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [FormsModule, RouterLink, TranslatePipe],
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
    this.error = '';
    this.auth.registrar(this.nombre, this.email, this.telefono, this.password, this.aceptaDatos).subscribe({
      // Si ya tenía productos en el carrito, lo llevamos a terminar la compra.
      next: () => this.router.navigate([this.carrito.items.length > 0 ? '/carrito' : '/']),
      // Por ejemplo 409 "Ya existe una cuenta con ese correo"
      error: (e: HttpErrorResponse) => {
        this.error = mensajeDeError(e);
      },
    });
  }
}
