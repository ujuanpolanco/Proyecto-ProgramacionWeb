// Punto de entrada: arranca la aplicación Angular con el componente principal (App).

import { registerLocaleData } from '@angular/common';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import localeEsCo from '@angular/common/locales/es-CO';
import localePt from '@angular/common/locales/pt';
import { LOCALE_ID, provideZoneChangeDetection } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { TitleStrategy, provideRouter, withInMemoryScrolling } from '@angular/router';
import { provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';

import { App } from './app/app';
import { routes } from './app/app.routes';
import { tokenInterceptor } from './app/servicios/token.interceptor';
import { TituloTraducido } from './app/titulo-traducido';

// Formato colombiano para precios y fechas: "$ 200.000" y "3 nov 2026".
registerLocaleData(localeEsCo);
// Portugués, para las fechas cuando la tienda está en ese idioma: "3 de nov. de 2026".
registerLocaleData(localePt, 'pt-BR');

bootstrapApplication(App, {
  providers: [
    // zone.js: Angular vuelve a dibujar la página después de cada evento o respuesta de la API.
    provideZoneChangeDetection(),
    // Las rutas de app.routes.ts. scrollPositionRestoration: al cambiar de página, sube al inicio.
    provideRouter(routes, withInMemoryScrolling({ scrollPositionRestoration: 'top' })),
    // HttpClient para llamar a la API (AJAX). El interceptor agrega el token JWT a cada petición.
    provideHttpClient(withInterceptors([tokenInterceptor])),
    // Internacionalización (i18n) con ngx-translate: los textos están en public/i18n/es.json y en.json
    provideTranslateService({
      loader: provideTranslateHttpLoader({ prefix: '/i18n/', suffix: '.json' }),
      fallbackLang: 'es',
      lang: localStorage.getItem('idioma') ?? 'es',
    }),
    // Títulos de pestaña traducidos
    { provide: TitleStrategy, useClass: TituloTraducido },
    { provide: LOCALE_ID, useValue: 'es-CO' },
  ],
}).catch(err => console.error(err));
