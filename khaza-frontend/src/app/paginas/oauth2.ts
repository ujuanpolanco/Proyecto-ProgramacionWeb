import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

import { AuthService } from '../servicios/auth.service';

// Página a la que vuelve el back-end después de iniciar sesión con Google.
// La URL trae el token JWT: /oauth2?token=eyJhbGci...
// Se guarda el token, se piden los datos del usuario y se va al catálogo.
@Component({
  selector: 'app-oauth2',
  // Eager: Angular vuelve a revisar este componente cada vez que cambia algo (por ejemplo, cuando
  // llega la respuesta de la API). Desde Angular 22 el valor por defecto es OnPush.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [RouterLink, TranslatePipe],
  templateUrl: './oauth2.html',
})
export class Oauth2 implements OnInit {

  error = false;

  constructor(private route: ActivatedRoute, private router: Router, private auth: AuthService) {}

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (!token) {
      this.error = true;
      return;
    }
    this.auth.completarLoginGoogle(token).subscribe({
      // replaceUrl: reemplaza /oauth2?token=... en el historial, así el token no queda en "Atrás"
      next: () => this.router.navigate(['/'], { replaceUrl: true }),
      error: () => {
        this.auth.logout();
        this.error = true;
      },
    });
  }
}
