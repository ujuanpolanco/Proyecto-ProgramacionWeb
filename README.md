# KHAZA — Tienda web de sombreros artesanales

Proyecto final de Programación Web — Universidad Surcolombiana.
Autor: Juan Manuel Polanco Patiño.

KHAZA es un emprendimiento de sombreros artesanales, decorados y ropa que hoy vende solo por
WhatsApp. Esta aplicación le da una tienda en línea con catálogo, carrito, pedidos con nota de
personalización (confirmados por WhatsApp) y un panel de administración.

| Capa | Tecnología | Carpeta |
|---|---|---|
| Front-end | Angular, Bootstrap 5, jQuery | `khaza-frontend/` |
| Back-end | Java + Spring Boot, JPA, JDBC, Swagger | `khaza-backend/` |
| Base de datos | MySQL | `base-datos/` |

- El modelo de datos (MER y diccionario) está en [docs/base-de-datos.md](docs/base-de-datos.md).

## Cómo crear la base de datos

Requisito: MySQL 8 o superior y MySQL Workbench.

1. Abrir MySQL Workbench y conectarse como `root`.
2. *File → Open SQL Script…* y elegir `base-datos/khaza.sql`.
3. Ejecutar todo con el rayo (⚡). Al final aparece la lista de productos.

Para ver los triggers funcionando, abrir `base-datos/pruebas.sql` y ejecutarlo sentencia por
sentencia (Ctrl + Enter). Para volver a los datos iniciales, basta con ejecutar otra vez `khaza.sql`.

## Cómo correr el back-end

Requisito: Java 21 o superior y la base de datos ya creada (paso anterior). No hace falta instalar
Maven: el proyecto trae Maven Wrapper (`mvnw`).

```bash
cd khaza-backend
./mvnw spring-boot:run
```

- API: <http://localhost:8080/api/productos>
- Documentación Swagger: <http://localhost:8080/swagger-ui.html>
- JavaDoc: `./mvnw javadoc:javadoc` y abrir `target/reports/apidocs/index.html`

| Método | Ruta | Qué hace |
|---|---|---|
| GET / POST | `/api/categorias` | Listar / crear categorías |
| GET / PUT / DELETE | `/api/categorias/{id}` | Buscar / editar / eliminar una categoría |
| GET / POST | `/api/productos` | Listar / crear productos |
| GET / PUT / DELETE | `/api/productos/{id}` | Buscar / editar / eliminar un producto |
| GET | `/api/pedidos?usuarioId=2` | Todos los pedidos, o solo los de un cliente |
| POST | `/api/pedidos` | Crear un pedido |
| PUT | `/api/pedidos/{id}/estado?estado=ENVIADO` | Cambiar el estado de un pedido |
| GET | `/api/reportes/ventas` | Ventas por producto (consulta con JDBC) |

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
