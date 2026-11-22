import { AfterViewInit, ChangeDetectionStrategy, Component } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet, TitleStrategy } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import $ from 'jquery';

import { AuthService } from './servicios/auth.service';
import { CarritoService } from './servicios/carrito.service';

// Componente principal: tiene la barra de navegación y el pie de página que se ven en todas
// las páginas. En <router-outlet> se dibuja la página de la ruta actual.
@Component({
  selector: 'app-root',
  // Eager: Angular vuelve a revisar este componente cada vez que cambia algo (por ejemplo, cuando
  // llega la respuesta de la API). Desde Angular 22 el valor por defecto es OnPush.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, TranslatePipe],
  templateUrl: './app.html',
})
export class App implements AfterViewInit {

  constructor(
    public auth: AuthService,
    public carrito: CarritoService,
    public traductor: TranslateService,
    private router: Router,
    private titulos: TitleStrategy,
  ) {
    document.documentElement.lang = this.idioma();

    // En el celular, el menú ☰ se cierra solo cuando el usuario cambia de página
    this.router.events.subscribe(evento => {
      if (evento instanceof NavigationEnd) {
        $('#menu').removeClass('show');
        $('.navbar-toggler').addClass('collapsed').attr('aria-expanded', 'false');
      }
    });
  }

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

  idioma(): string {
    return this.traductor.getCurrentLang() ?? 'es';
  }

  // Cambia el idioma de toda la página y lo recuerda para la próxima visita.
  // También cambia <html lang="..."> para que los lectores de pantalla lean con la pronunciación correcta.
  cambiarIdioma(idioma: 'es' | 'en' | 'pt'): void {
    this.traductor.use(idioma).subscribe(() => {
      // el título de la pestaña también cambia de idioma
      this.titulos.updateTitle(this.router.routerState.snapshot);
    });
    localStorage.setItem('idioma', idioma);
    document.documentElement.lang = idioma;
  }

  // Accesibilidad: el enlace "Saltar al contenido" lleva el foco del teclado al contenido
  // principal sin tener que pasar por todo el menú.
  saltarAlContenido(evento: Event): void {
    evento.preventDefault();
    document.getElementById('contenido')?.focus();
  }

  cerrarSesion(): void {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
