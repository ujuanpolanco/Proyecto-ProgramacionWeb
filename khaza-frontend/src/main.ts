// Punto de entrada: arranca la aplicación Angular con el componente principal (App).

import { registerLocaleData } from '@angular/common';
import localeEsCo from '@angular/common/locales/es-CO';
import { LOCALE_ID, provideZoneChangeDetection } from '@angular/core';
import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter, withInMemoryScrolling } from '@angular/router';

import { App } from './app/app';
import { routes } from './app/app.routes';

// Formato colombiano para precios y fechas: "$ 200.000" y "3 nov 2026".
registerLocaleData(localeEsCo);

bootstrapApplication(App, {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    // Las rutas de app.routes.ts. scrollPositionRestoration: al cambiar de página, sube al inicio.
    provideRouter(routes, withInMemoryScrolling({ scrollPositionRestoration: 'top' })),
    { provide: LOCALE_ID, useValue: 'es-CO' },
  ],
}).catch(err => console.error(err));
