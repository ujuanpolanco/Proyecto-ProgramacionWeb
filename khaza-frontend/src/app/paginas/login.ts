import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

import { mensajeDeError } from '../servicios/api.service';
import { AuthService } from '../servicios/auth.service';

@Component({
  selector: 'app-login',
  // Eager: Angular vuelve a revisar este componente cada vez que cambia algo (por ejemplo, cuando
  // llega la respuesta de la API). Desde Angular 22 el valor por defecto es OnPush.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [FormsModule, RouterLink, TranslatePipe],
  templateUrl: './login.html',
})
export class Login {

  email = '';
  password = '';
  error = '';
  errorGoogle: string | null = null;

  constructor(public auth: AuthService, private router: Router, private route: ActivatedRoute) {
    // Si el back-end devolvió aquí después de un intento fallido con Google:
    // /login?error=google (falló), ?error=clave (el correo tiene contraseña) o ?error=desactivada
    this.errorGoogle = this.route.snapshot.queryParamMap.get('error');
  }

  ingresar(formulario: NgForm): void {
    if (formulario.invalid) {
      return;
    }
    this.error = '';
    this.auth.login(this.email, this.password).subscribe({
      next: respuesta => {
        // Si llegó aquí desde otra página (?volver=/carrito), regresa a esa página.
        const volver = this.route.snapshot.queryParamMap.get('volver');
        if (volver) {
          this.router.navigateByUrl(volver);
        } else {
          this.router.navigate([respuesta.usuario.rol === 'ADMIN' ? '/admin/pedidos' : '/']);
        }
      },
      error: (e: HttpErrorResponse) => {
        this.error = mensajeDeError(e);
      },
    });
  }

  // OAuth 2.0: el navegador se va a la página de Google (pasando por el back-end).
  entrarConGoogle(): void {
    window.location.href = this.auth.urlLoginGoogle();
  }
}
