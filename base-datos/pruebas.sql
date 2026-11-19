-- =====================================================================
--  KHAZA - Pruebas de los triggers y consultas de ejemplo
-- ---------------------------------------------------------------------
--  Ejecutar DESPUÉS de khaza.sql. Sirve para mostrarle al profesor que
--  los triggers funcionan.
--
--  En Workbench conviene ir sentencia por sentencia: poner el cursor en
--  una línea y presionar Ctrl + Enter.
--
--  Estas pruebas cambian los datos. Para dejar todo como al principio,
--  basta con volver a ejecutar khaza.sql.
-- =====================================================================
SET NAMES utf8mb4;
USE khaza;


-- ---------------------------------------------------------------------
-- PRUEBA 1: trg_descontar_stock
-- El Sombrero Rosa (id 2) empieza con stock 5.
-- ---------------------------------------------------------------------
SELECT id, nombre, stock FROM productos WHERE id = 2;           -- stock = 5

INSERT INTO pedidos (usuario_id, total, direccion, telefono, nota)
VALUES (2, 400000, 'Carrera 5 # 10-30, Neiva', '3104445566', 'Prueba del trigger');

-- LAST_INSERT_ID() es el id del pedido que se acabó de crear
INSERT INTO detalle_pedido (pedido_id, producto_id, talla, cantidad, precio_unitario)
VALUES (LAST_INSERT_ID(), 2, 'S', 2, 200000);

SELECT id, nombre, stock FROM productos WHERE id = 2;           -- stock = 3: lo restó el trigger


-- ---------------------------------------------------------------------
-- PRUEBA 2: los productos bajo pedido no se descuentan
-- El Sombrero Vino (id 4) es bajo pedido y tiene stock 0.
-- ---------------------------------------------------------------------
INSERT INTO pedidos (usuario_id, total, direccion, telefono)
VALUES (2, 190000, 'Carrera 5 # 10-30, Neiva', '3104445566');

INSERT INTO detalle_pedido (pedido_id, producto_id, talla, cantidad, precio_unitario)
VALUES (LAST_INSERT_ID(), 4, 'M', 1, 190000);

SELECT id, nombre, stock, bajo_pedido FROM productos WHERE id = 4;   -- sigue en 0, sin error


-- ---------------------------------------------------------------------
-- PRUEBA 3: trg_auditar_precio
-- ---------------------------------------------------------------------
UPDATE productos SET precio = 230000 WHERE id = 1;     -- cambia el precio del Multicolor
UPDATE productos SET stock = 20 WHERE id = 6;          -- cambia solo el stock de la Camiseta

-- Solo aparece el cambio del Multicolor (200000 → 230000).
-- El cambio de stock no se registra porque el precio no cambió.
SELECT a.id, p.nombre, a.precio_anterior, a.precio_nuevo, a.fecha
FROM auditoria_precios a
INNER JOIN productos p ON p.id = a.producto_id;


-- ---------------------------------------------------------------------
-- CONSULTAS DE EJEMPLO (las mismas que usa el reporte de ventas, ReporteDao.java)
-- ---------------------------------------------------------------------

-- Unidades vendidas y dinero recibido por cada producto
SELECT p.nombre,
       SUM(d.cantidad)                     AS unidades,
       SUM(d.cantidad * d.precio_unitario) AS total_vendido
FROM detalle_pedido d
INNER JOIN productos p ON p.id = d.producto_id
GROUP BY p.nombre
ORDER BY total_vendido DESC;

-- Cuántos pedidos hay en cada estado
SELECT estado, COUNT(*) AS cantidad
FROM pedidos
GROUP BY estado;

-- Pedidos con el nombre del cliente (JOIN entre pedidos y usuarios)
SELECT pe.id, u.nombre AS cliente, pe.fecha, pe.estado, pe.total
FROM pedidos pe
INNER JOIN usuarios u ON u.id = pe.usuario_id
ORDER BY pe.fecha DESC;


-- ---------------------------------------------------------------------
-- PRUEBA 4: no se puede vender más de lo que hay
-- Va de última porque DEBE dar error. El Sombrero Turquesa (id 3) tiene
-- stock 2 y se intenta vender 10. El trigger intenta dejar el stock en -8,
-- el CHECK (stock >= 0) lo impide y MySQL rechaza la línea del pedido:
--   Error 3819: Check constraint 'productos_chk_2' is violated.
-- ---------------------------------------------------------------------
INSERT INTO detalle_pedido (pedido_id, producto_id, talla, cantidad, precio_unitario)
VALUES (1, 3, 'M', 10, 210000);

SELECT id, nombre, stock FROM productos WHERE id = 3;           -- sigue en 2
