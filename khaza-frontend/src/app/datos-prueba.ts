// DATOS DE PRUEBA (solo para la Fase 1).
// Mientras no existe el back-end, los servicios leen y modifican estas listas en memoria.
// En la Fase 4 los servicios pasan a pedir los datos a la API y este archivo se borra.

import { Categoria, Pedido, Producto, Usuario } from './modelos';

export const CATEGORIAS: Categoria[] = [
  { id: 1, nombre: 'Sombreros', descripcion: 'Sombreros artesanales decorados a mano' },
  { id: 2, nombre: 'Ropa', descripcion: 'Prendas con la marca KHAZA' },
];

export const PRODUCTOS: Producto[] = [
  {
    id: 1,
    nombre: 'Sombrero Multicolor',
    descripcion: 'Tejido en palma de iraca y decorado a mano con hilos en rojo, fucsia, naranja y turquesa, mostacilla y dijes en cobre.',
    precio: 200000,
    stock: 8,
    material: 'Palma de iraca',
    decoracion: 'Hilos, mostacilla y dijes de cobre',
    tallas: 'S,M,L',
    bajoPedido: false,
    imagen: 'img/productos/sombrero-multicolor.jpg',
    activo: true,
    categoria: CATEGORIAS[0],
  },
  {
    id: 2,
    nombre: 'Sombrero Rosa',
    descripcion: 'Paja natural con cintas en rosa palo y verde menta, cinta geométrica tejida y perlas cosidas a mano.',
    precio: 200000,
    stock: 5,
    material: 'Paja natural',
    decoracion: 'Cintas y perlas',
    tallas: 'S,M,L',
    bajoPedido: false,
    imagen: 'img/productos/sombrero-rosa.jpg',
    activo: true,
    categoria: CATEGORIAS[0],
  },
  {
    id: 3,
    nombre: 'Sombrero Turquesa',
    descripcion: 'Decorado con mostacilla en turquesa y azul profundo, cinta de motivos étnicos y caracoles naturales.',
    precio: 210000,
    stock: 2,
    material: 'Palma de iraca',
    decoracion: 'Mostacilla y caracoles',
    tallas: 'M,L',
    bajoPedido: false,
    imagen: 'img/productos/sombrero-turquesa.jpg',
    activo: true,
    categoria: CATEGORIAS[0],
  },
  {
    id: 4,
    nombre: 'Sombrero Vino',
    descripcion: 'Palma teñida en marrón chocolate con hilos en vino tinto y cuentas de cobre. El más sobrio de la colección.',
    precio: 190000,
    stock: 0,
    material: 'Palma de iraca',
    decoracion: 'Hilos y cuentas de cobre',
    tallas: 'S,M,L',
    bajoPedido: true,
    imagen: 'img/productos/sombrero-vino.jpg',
    activo: true,
    categoria: CATEGORIAS[0],
  },
  {
    id: 5,
    nombre: 'Sombrero Blanco y Negro',
    descripcion: 'Base en marrón oscuro con cintas en blanco, negro y rojo, flecos plateados y dijes en dos metales.',
    precio: 220000,
    stock: 4,
    material: 'Palma de iraca',
    decoracion: 'Cintas y flecos plateados',
    tallas: 'M,L',
    bajoPedido: false,
    imagen: 'img/productos/sombrero-blanco-y-negro.jpg',
    activo: true,
    categoria: CATEGORIAS[0],
  },
  {
    id: 6,
    nombre: 'Camiseta KHAZA',
    descripcion: 'Camiseta de algodón con el logo de KHAZA estampado al frente.',
    precio: 60000,
    stock: 15,
    material: 'Algodón',
    decoracion: 'Estampado',
    tallas: 'S,M,L,XL',
    bajoPedido: false,
    imagen: '',
    activo: true,
    categoria: CATEGORIAS[1],
  },
  {
    id: 7,
    nombre: 'Chaqueta bordada',
    descripcion: 'Chaqueta de jean con bordado artesanal en la espalda. Se fabrica cuando se pide.',
    precio: 180000,
    stock: 0,
    material: 'Jean',
    decoracion: 'Bordado a mano',
    tallas: 'S,M,L',
    bajoPedido: true,
    imagen: '',
    activo: true,
    categoria: CATEGORIAS[1],
  },
];

// Usuarios de prueba. La contraseña está en texto plano SOLO porque esto es una simulación;
// en la base de datos real se guarda cifrada con BCrypt (Fase 5).
export const USUARIOS: (Usuario & { password: string })[] = [
  { id: 1, nombre: 'Administrador KHAZA', email: 'admin@khaza.com', password: 'admin123', telefono: '3001112233', rol: 'ADMIN' },
  { id: 2, nombre: 'Laura Gómez', email: 'cliente@khaza.com', password: 'cliente123', telefono: '3104445566', rol: 'CLIENTE' },
];

export const PEDIDOS: Pedido[] = [
  {
    id: 1,
    usuario: USUARIOS[1],
    fecha: '2026-10-28T10:15:00',
    estado: 'ENTREGADO',
    total: 400000,
    direccion: 'Calle 8 # 5-20, Neiva',
    telefono: '3104445566',
    nota: 'Uno de los dos con las iniciales L.G. en la cinta',
    detalles: [{ producto: PRODUCTOS[0], talla: 'M', cantidad: 2, precioUnitario: 200000 }],
  },
  {
    id: 2,
    usuario: USUARIOS[1],
    fecha: '2026-11-01T16:40:00',
    estado: 'PENDIENTE',
    total: 270000,
    direccion: 'Calle 8 # 5-20, Neiva',
    telefono: '3104445566',
    nota: '',
    detalles: [
      { producto: PRODUCTOS[2], talla: 'L', cantidad: 1, precioUnitario: 210000 },
      { producto: PRODUCTOS[5], talla: 'M', cantidad: 1, precioUnitario: 60000 },
    ],
  },
];
