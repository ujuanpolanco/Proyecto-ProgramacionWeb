import { Injectable, Injector } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { RouterStateSnapshot, TitleStrategy } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

// Pone el título de la pestaña del navegador en el idioma elegido.
// En app.routes.ts cada ruta tiene como "title" una clave de traducción (por ejemplo
// 'titulos.carrito'); aquí se traduce y se le agrega " | KHAZA".
// Accesibilidad: el lector de pantalla anuncia este título cada vez que se cambia de página.
@Injectable({ providedIn: 'root' })
export class TituloTraducido extends TitleStrategy {

  constructor(private titulo: Title, private injector: Injector) {
    super();
  }

  override updateTitle(estado: RouterStateSnapshot): void {
    const clave = this.buildTitle(estado);
    if (clave) {
      // El traductor se pide aquí y no en el constructor para evitar un ciclo:
      // Router → TituloTraducido → TranslateService → HttpClient → interceptor → Router.
      const traductor = this.injector.get(TranslateService);
      // get() espera a que el archivo de traducciones haya cargado
      traductor.get(clave).subscribe(texto => this.titulo.setTitle(`${texto} | KHAZA`));
    }
  }
}
