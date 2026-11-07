import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../servicios/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
})
export class Login {

  email = '';
  password = '';
  error = '';

  constructor(private auth: AuthService, private router: Router, private route: ActivatedRoute) {}

  ingresar(formulario: NgForm): void {
    if (formulario.invalid) {
      return;
    }
    this.auth.login(this.email, this.password).subscribe({
      next: usuario => {
        // Si llegó aquí desde otra página (?volver=/carrito), regresa a esa página.
        const volver = this.route.snapshot.queryParamMap.get('volver');
        if (volver) {
          this.router.navigateByUrl(volver);
        } else {
          this.router.navigate([usuario.rol === 'ADMIN' ? '/admin/pedidos' : '/']);
        }
      },
      error: (e: Error) => {
        this.error = e.message;
      },
    });
  }
}
