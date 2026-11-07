import { AfterViewInit, Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import $ from 'jquery';

import { AuthService } from './servicios/auth.service';
import { CarritoService } from './servicios/carrito.service';

// Componente principal: tiene la barra de navegación y el pie de página que se ven en todas
// las páginas. En <router-outlet> se dibuja la página de la ruta actual.
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
})
export class App implements AfterViewInit {

  constructor(public auth: AuthService, public carrito: CarritoService, private router: Router) {}

  // jQuery: botón "volver arriba". Aparece con fadeIn cuando el usuario baja más de 400 px
  // y al hacer clic sube con una animación. Se hace en ngAfterViewInit porque ahí el botón
  // ya existe en la página.
  ngAfterViewInit(): void {
    const boton = $('#btn-arriba');
    $(window).on('scroll', () => {
      if (($(window).scrollTop() ?? 0) > 400) {
        boton.fadeIn();
      } else {
        boton.fadeOut();
      }
    });
    boton.on('click', () => {
      $('html, body').animate({ scrollTop: 0 }, 500);
    });
  }

  cerrarSesion(): void {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
