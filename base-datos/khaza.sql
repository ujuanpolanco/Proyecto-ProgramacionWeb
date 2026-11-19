-- =====================================================================
--  KHAZA - Base de datos de la tienda (MySQL 8 o superior)
-- ---------------------------------------------------------------------
--  Cómo usarlo: abrir este archivo en MySQL Workbench y ejecutarlo
--  completo con el rayo (⚡). Se puede ejecutar las veces que se quiera:
--  borra la base de datos y la vuelve a crear desde cero.
--
--  Partes del script:
--    1. Base de datos
--    2. Tablas (DDL)
--    3. Triggers
--    4. Datos de ejemplo (DML)
--    5. Usuario de MySQL para la aplicación (DCL)
--
--  El modelo (MER y diccionario de datos) está en docs/base-de-datos.md
-- =====================================================================


-- =====================================================================
-- 1. BASE DE DATOS
-- =====================================================================

-- Le dice a MySQL que este archivo viene en UTF-8 (para las tildes y la ñ)
SET NAMES utf8mb4;

DROP DATABASE IF EXISTS khaza;

-- utf8mb4 para que se guarden bien las tildes y la ñ
CREATE DATABASE khaza CHARACTER SET utf8mb4;

USE khaza;


-- =====================================================================
-- 2. TABLAS
-- Se crean en orden: primero las que no dependen de nadie y después las
-- que tienen llaves foráneas hacia ellas.
-- =====================================================================

CREATE TABLE roles (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE          -- 'ADMIN' o 'CLIENTE'
);

CREATE TABLE usuarios (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    email          VARCHAR(100) NOT NULL UNIQUE,
    password       VARCHAR(100) NULL,           -- cifrada con BCrypt; NULL si entra con Google
    telefono       VARCHAR(20)  NULL,
    acepta_datos   BOOLEAN      NOT NULL DEFAULT FALSE,   -- autorización Ley 1581 de 2012
    activo         BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_registro DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Relación muchos a muchos entre usuarios y roles (como en el ejercicio de clase)
CREATE TABLE usuarios_roles (
    usuario_id INT NOT NULL,
    rol_id     INT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id)     REFERENCES roles(id)
);

CREATE TABLE categorias (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL UNIQUE,
    descripcion VARCHAR(255) NULL
);

CREATE TABLE productos (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nombre       VARCHAR(100)  NOT NULL,
    descripcion  TEXT          NULL,
    precio       DECIMAL(10,2) NOT NULL,
    stock        INT           NOT NULL DEFAULT 0,
    material     VARCHAR(50)   NULL,
    decoracion   VARCHAR(100)  NULL,
    tallas       VARCHAR(50)   NOT NULL,       -- separadas por coma: 'S,M,L'
    bajo_pedido  BOOLEAN       NOT NULL DEFAULT FALSE,
    imagen       VARCHAR(255)  NULL,
    activo       BOOLEAN       NOT NULL DEFAULT TRUE,
    categoria_id INT           NOT NULL,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    -- Reglas que la base de datos no deja romper:
    CHECK (precio > 0),
    CHECK (stock >= 0)       -- no se puede vender más de lo que hay
);

CREATE TABLE pedidos (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT           NOT NULL,
    fecha      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado     VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    total      DECIMAL(10,2) NOT NULL,
    direccion  VARCHAR(200)  NOT NULL,
    telefono   VARCHAR(20)   NOT NULL,
    nota       VARCHAR(500)  NULL,            -- nota de personalización
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CHECK (estado IN ('PENDIENTE', 'ENVIADO', 'ENTREGADO'))
);

CREATE TABLE detalle_pedido (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id       INT           NOT NULL,
    producto_id     INT           NOT NULL,
    talla           VARCHAR(10)   NOT NULL,
    cantidad        INT           NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,   -- el precio que pagó, aunque después cambie
    FOREIGN KEY (pedido_id)   REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id),
    CHECK (cantidad > 0)
);

-- Esta tabla no la llena la aplicación: la llena el trigger trg_auditar_precio
CREATE TABLE auditoria_precios (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    producto_id     INT           NOT NULL,
    precio_anterior DECIMAL(10,2) NOT NULL,
    precio_nuevo    DECIMAL(10,2) NOT NULL,
    fecha           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);


-- =====================================================================
-- 3. TRIGGERS
-- Un trigger es código SQL que MySQL ejecuta solo cuando pasa algo en
-- una tabla. NEW es la fila nueva y OLD la fila como estaba antes.
--
-- DELIMITER cambia el ";" por "//" mientras se escribe el trigger, para
-- que MySQL no crea que el trigger termina en el primer ";" de adentro.
-- =====================================================================
DELIMITER //

-- Cada vez que se agrega un producto a un pedido, se resta del stock.
-- Los productos bajo pedido no se descuentan porque se fabrican después.
-- Si no alcanza el stock, el CHECK (stock >= 0) de la tabla productos
-- rechaza el cambio y el pedido no se guarda.
CREATE TRIGGER trg_descontar_stock
AFTER INSERT ON detalle_pedido
FOR EACH ROW
BEGIN
    UPDATE productos
    SET stock = stock - NEW.cantidad
    WHERE id = NEW.producto_id
      AND bajo_pedido = FALSE;
END //

-- Cada vez que se modifica un producto, si el precio cambió se guarda
-- el precio anterior y el nuevo en auditoria_precios.
CREATE TRIGGER trg_auditar_precio
AFTER UPDATE ON productos
FOR EACH ROW
BEGIN
    IF OLD.precio <> NEW.precio THEN
        INSERT INTO auditoria_precios (producto_id, precio_anterior, precio_nuevo)
        VALUES (NEW.id, OLD.precio, NEW.precio);
    END IF;
END //

DELIMITER ;


-- =====================================================================
-- 4. DATOS DE EJEMPLO
-- Productos reales de KHAZA (las fotos están en khaza-frontend/public/img/productos).
-- =====================================================================

INSERT INTO roles (id, nombre) VALUES
    (1, 'ADMIN'),
    (2, 'CLIENTE');

-- Las contraseñas están cifradas con BCrypt (nunca en texto plano):
--   admin@khaza.com   → admin123
--   cliente@khaza.com → cliente123
INSERT INTO usuarios (id, nombre, email, password, telefono, acepta_datos) VALUES
    (1, 'Administrador KHAZA', 'admin@khaza.com',
        '$2a$10$zXqPCIz4UYMZT7TTFrlfPe52XjTftDSQ8Cwk3.4JMnRKrOfO4jCrW', '3001112233', TRUE),
    (2, 'Laura Gómez', 'cliente@khaza.com',
        '$2a$10$MQbCSSe40rZ0dppsiTSf8O7fzNn6qwJO.cM9wu6VE7nTudGYjM76a', '3104445566', TRUE);

INSERT INTO usuarios_roles (usuario_id, rol_id) VALUES
    (1, 1),   -- admin   → ADMIN
    (2, 2);   -- Laura   → CLIENTE

INSERT INTO categorias (id, nombre, descripcion) VALUES
    (1, 'Sombreros', 'Sombreros artesanales decorados a mano'),
    (2, 'Ropa', 'Prendas con la marca KHAZA');

-- Ojo con el stock: el Sombrero Multicolor entra con 10, el Turquesa con 3 y
-- la Camiseta con 16. Como los pedidos de ejemplo de más abajo compran 2, 1
-- y 1, el trigger los deja en 8, 2 y 15 (lo mismo que muestra el front-end).
INSERT INTO productos (id, nombre, descripcion, precio, stock, material, decoracion, tallas, bajo_pedido, imagen, categoria_id) VALUES
    (1, 'Sombrero Multicolor',
        'Tejido en palma de iraca y decorado a mano con hilos en rojo, fucsia, naranja y turquesa, mostacilla y dijes en cobre.',
        200000, 10, 'Palma de iraca', 'Hilos, mostacilla y dijes de cobre', 'S,M,L', FALSE,
        'img/productos/sombrero-multicolor.jpg', 1),
    (2, 'Sombrero Rosa',
        'Paja natural con cintas en rosa palo y verde menta, cinta geométrica tejida y perlas cosidas a mano.',
        200000, 5, 'Paja natural', 'Cintas y perlas', 'S,M,L', FALSE,
        'img/productos/sombrero-rosa.jpg', 1),
    (3, 'Sombrero Turquesa',
        'Decorado con mostacilla en turquesa y azul profundo, cinta de motivos étnicos y caracoles naturales.',
        210000, 3, 'Palma de iraca', 'Mostacilla y caracoles', 'M,L', FALSE,
        'img/productos/sombrero-turquesa.jpg', 1),
    (4, 'Sombrero Vino',
        'Palma teñida en marrón chocolate con hilos en vino tinto y cuentas de cobre. El más sobrio de la colección.',
        190000, 0, 'Palma de iraca', 'Hilos y cuentas de cobre', 'S,M,L', TRUE,
        'img/productos/sombrero-vino.jpg', 1),
    (5, 'Sombrero Blanco y Negro',
        'Base en marrón oscuro con cintas en blanco, negro y rojo, flecos plateados y dijes en dos metales.',
        220000, 4, 'Palma de iraca', 'Cintas y flecos plateados', 'M,L', FALSE,
        'img/productos/sombrero-blanco-y-negro.jpg', 1),
    (6, 'Camiseta KHAZA',
        'Camiseta de algodón con el logo de KHAZA estampado al frente.',
        60000, 16, 'Algodón', 'Estampado', 'S,M,L,XL', FALSE,
        NULL, 2),
    (7, 'Chaqueta bordada',
        'Chaqueta de jean con bordado artesanal en la espalda. Se fabrica cuando se pide.',
        180000, 0, 'Jean', 'Bordado a mano', 'S,M,L', TRUE,
        NULL, 2);

INSERT INTO pedidos (id, usuario_id, fecha, estado, total, direccion, telefono, nota) VALUES
    (1, 2, '2026-10-28 10:15:00', 'ENTREGADO', 400000, 'Calle 8 # 5-20, Neiva', '3104445566',
        'Uno de los dos con las iniciales L.G. en la cinta'),
    (2, 2, '2026-11-01 16:40:00', 'PENDIENTE', 270000, 'Calle 8 # 5-20, Neiva', '3104445566',
        NULL);

-- Al insertar cada línea se ejecuta trg_descontar_stock
INSERT INTO detalle_pedido (pedido_id, producto_id, talla, cantidad, precio_unitario) VALUES
    (1, 1, 'M', 2, 200000),   -- pedido 1: 2 × Sombrero Multicolor
    (2, 3, 'L', 1, 210000),   -- pedido 2: 1 × Sombrero Turquesa
    (2, 6, 'M', 1,  60000);   -- pedido 2: 1 × Camiseta KHAZA


-- =====================================================================
-- 5. USUARIO DE MYSQL PARA LA APLICACIÓN
-- Spring Boot no se conecta como root, sino con un usuario que solo puede
-- leer y modificar datos de la base khaza. No puede borrar tablas ni tocar
-- otras bases de datos (principio de mínimo privilegio).
-- =====================================================================
DROP USER IF EXISTS 'khaza_app'@'localhost';
CREATE USER 'khaza_app'@'localhost' IDENTIFIED BY 'Khaza2026*';
GRANT SELECT, INSERT, UPDATE, DELETE ON khaza.* TO 'khaza_app'@'localhost';


-- =====================================================================
-- Revisión rápida: debería mostrar 8 / 2 / 15 en el stock
-- =====================================================================
SELECT id, nombre, stock, bajo_pedido FROM productos;
