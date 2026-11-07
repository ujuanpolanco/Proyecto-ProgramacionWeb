# KHAZA — Tienda web de sombreros artesanales

Proyecto final de Programación Web — Universidad Surcolombiana.
Autor: Juan Manuel Polanco Patiño.

KHAZA es un emprendimiento de sombreros artesanales, decorados y ropa que hoy vende solo por
WhatsApp. Esta aplicación le da una tienda en línea con catálogo, carrito, pedidos con nota de
personalización (confirmados por WhatsApp) y un panel de administración.

| Capa | Tecnología | Carpeta |
|---|---|---|
| Front-end | Angular, Bootstrap 5, jQuery | `khaza-frontend/` |
| Back-end | Java + Spring Boot (Fase 3) | `khaza-backend/` |
| Base de datos | MySQL (Fase 2) | `base-datos/` |

- El avance por fases está en [PLAN.md](PLAN.md).
- Lo que hay que saber para sustentar está en [GUIA_DEFENSA.md](GUIA_DEFENSA.md).
- El modelo de datos (MER y diccionario) está en [docs/base-de-datos.md](docs/base-de-datos.md).

## Cómo correr el front-end

Requisito: Node.js 22 o superior. Conviene abrir la carpeta `KHAZA` en VS Code: así quedan ocultas las
librerías descargadas (`node_modules`) y solo se ve nuestro código, que está en `khaza-frontend/src`.

```bash
cd khaza-frontend
npm install
npm start
```

Abrir <http://localhost:4200>.

Usuarios de prueba (Fase 1, simulados):

| Rol | Correo | Contraseña |
|---|---|---|
| Cliente | cliente@khaza.com | cliente123 |
| Administrador | admin@khaza.com | admin123 |
