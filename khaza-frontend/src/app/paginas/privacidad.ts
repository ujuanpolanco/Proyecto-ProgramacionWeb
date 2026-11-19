import { ChangeDetectionStrategy, Component } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

// Aviso de privacidad (Ley 1581 de 2012). Es solo texto; el registro enlaza a esta página.
@Component({
  selector: 'app-privacidad',
  // Eager: Angular vuelve a revisar este componente cada vez que cambia algo (por ejemplo, cuando
  // llega la respuesta de la API). Desde Angular 22 el valor por defecto es OnPush.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [TranslatePipe],
  templateUrl: './privacidad.html',
})
export class Privacidad {}
