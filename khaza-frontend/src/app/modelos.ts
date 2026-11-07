// Interfaces con la forma de los datos de la tienda.
// Son las mismas tablas de la base de datos (docs/base-de-datos.md), escritas en TypeScript.

export interface Categoria {
  id: number;
  nombre: string;
  descripcion: string;
}

export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  stock: number;
  material: string;
  decoracion: string;
  tallas: string; // separadas por coma, por ejemplo "S,M,L"
  bajoPedido: boolean;
  imagen: string;
  activo: boolean;
  categoria: Categoria;
}

export interface Usuario {
  id: number;
  nombre: string;
  email: string;
  telefono: string;
  rol: 'ADMIN' | 'CLIENTE';
}

// Un producto dentro del carrito, con la talla y la cantidad que eligió el cliente.
export interface ItemCarrito {
  producto: Producto;
  talla: string;
  cantidad: number;
}

export type EstadoPedido = 'PENDIENTE' | 'ENVIADO' | 'ENTREGADO';

export interface DetallePedido {
  producto: Producto;
  talla: string;
  cantidad: number;
  precioUnitario: number;
}

export interface Pedido {
  id: number;
  usuario: Usuario;
  fecha: string;
  estado: EstadoPedido;
  total: number;
  direccion: string;
  telefono: string;
  nota: string;
  detalles: DetallePedido[];
}
