# KHAZA — Tienda web de sombreros artesanales

Proyecto final de Programación Web — Universidad Surcolombiana, 2026-2.
Autor: Juan Manuel Polanco Patiño.

KHAZA es un emprendimiento de sombreros artesanales, decorados y ropa que hoy vende solo por
WhatsApp. Esta aplicación le da una tienda en línea: catálogo con búsqueda y filtros, carrito,
pedidos con nota de personalización confirmados por WhatsApp, historial de pedidos y un panel de
administración con inventario, gestión de pedidos y reporte de ventas en PDF.

| Capa | Tecnología | Carpeta |
|---|---|---|
| Front-end | Angular 22, Bootstrap 5, jQuery, ngx-translate (español / inglés) | `khaza-frontend/` |
| Back-end | Java 21 + Spring Boot 3.5, Spring Security, JWT, OAuth 2.0, JPA, JDBC, Swagger, OpenPDF | `khaza-backend/` |
| Base de datos | MySQL 8 o superior, con triggers | `base-datos/` |

- Modelo de datos (MER y diccionario): [docs/base-de-datos.md](docs/base-de-datos.md)
- Documento final: `docs/Documentacion_KHAZA.docx` (y su versión en PDF)

## 1. Requisitos

- MySQL 8 o superior y MySQL Workbench
- Java 21 o superior (no hace falta instalar Maven: el proyecto trae `mvnw`)
- Node.js 22 o superior

## 2. Crear la base de datos

1. Abrir MySQL Workbench y conectarse como `root`.
2. *File → Open SQL Script…* → `base-datos/khaza.sql` y ejecutarlo completo (⚡).
3. Para ver los triggers funcionando: abrir `base-datos/pruebas.sql` y ejecutarlo sentencia por
   sentencia (Ctrl + Enter). Para volver a los datos iniciales, ejecutar otra vez `khaza.sql`.

El script crea la base `khaza`, las 8 tablas, los triggers `trg_descontar_stock` y
`trg_auditar_precio`, los datos de ejemplo y el usuario `khaza_app` con el que se conecta el back-end.

## 3. Correr el back-end (https://localhost:8443)

```bash
cd khaza-backend
./mvnw spring-boot:run
```

- Documentación Swagger: <https://localhost:8443/swagger-ui.html> (botón **Authorize** para probar
  las rutas protegidas con el token que devuelve `/api/auth/login`).
- JavaDoc: `./mvnw javadoc:javadoc` y abrir `target/reports/apidocs/index.html`.
- El certificado HTTPS es autofirmado (`src/main/resources/khaza.p12`, creado con `keytool`): la
  primera vez el navegador muestra una advertencia y hay que darle "Continuar".

## 4. Correr el front-end (https://localhost:4200)

```bash
cd khaza-frontend
npm install
npm start
```

Abrir <https://localhost:4200> y aceptar la advertencia del certificado de desarrollo. El servidor de
Angular reenvía `/api` y el inicio de sesión con Google al back-end (`proxy.conf.json`), así que el
navegador solo habla con el puerto 4200.

Usuarios de prueba:

| Rol | Correo | Contraseña |
|---|---|---|
| Cliente | cliente@khaza.com | cliente123 |
| Administrador | admin@khaza.com | admin123 |

## 5. Inicio de sesión con Google (OAuth 2.0)

El código ya está listo; solo faltan las credenciales, que se crean con una cuenta de Google:

1. Entrar a <https://console.cloud.google.com>, crear un proyecto y configurar la pantalla de
   consentimiento OAuth (tipo "Externo", con tu correo como usuario de prueba).
2. *APIs y servicios → Credenciales → Crear credenciales → ID de cliente de OAuth* → tipo
   "Aplicación web".
3. En **URI de redireccionamiento autorizados** agregar: `https://localhost:4200/login/oauth2/code/google`
4. Copiar el ID y el secreto, y arrancar el back-end con ellos (PowerShell):

```powershell
$env:GOOGLE_CLIENT_ID = "el-id-que-da-google"
$env:GOOGLE_CLIENT_SECRET = "el-secreto-que-da-google"
./mvnw spring-boot:run
```

## 6. API REST

| Método | Ruta | Quién | Qué hace |
|---|---|---|---|
| POST | `/api/auth/registro` | Público | Crea una cuenta de cliente (contraseña con BCrypt) y devuelve el token |
| POST | `/api/auth/login` | Público | Inicia sesión y devuelve el token JWT |
| GET | `/api/auth/yo` | Con sesión | Datos del dueño del token |
| GET | `/api/productos`, `/api/productos/{id}` | Público | Catálogo |
| POST, PUT, DELETE | `/api/productos/...` | ADMIN | CRUD de productos |
| GET | `/api/categorias`, `/api/categorias/{id}` | Público | Categorías |
| POST, PUT, DELETE | `/api/categorias/...` | ADMIN | CRUD de categorías |
| POST | `/api/pedidos` | CLIENTE o ADMIN | Crea un pedido a nombre del dueño del token |
| GET | `/api/pedidos/mios` | CLIENTE o ADMIN | Historial del usuario |
| GET | `/api/pedidos` | ADMIN | Todos los pedidos |
| PUT | `/api/pedidos/{id}/estado?estado=ENVIADO` | ADMIN | Cambia el estado de un pedido |
| GET | `/api/reportes/ventas`, `/api/reportes/estados` | ADMIN | Reportes (consultas JDBC) |
| GET | `/api/reportes/ventas/pdf` | ADMIN | Reporte de ventas en PDF (OpenPDF) |

Sin token, las rutas protegidas responden **401**; con un rol que no corresponde, **403**.
