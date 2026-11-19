// Interfaces con la forma de los datos de la tienda.
// Son las mismas tablas de la base de datos (docs/base-de-datos.md) y los mismos JSON que
// devuelve la API de Spring Boot.

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
  imagen: string | null;
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

// Lo que responde la API al iniciar sesión o registrarse.
export interface RespuestaLogin {
  token: string;
  usuario: Usuario;
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
  nota: string | null;
  detalles: DetallePedido[];
}

// Lo que se le envía a la API para crear un pedido. No lleva precios ni total:
// los calcula el servidor con los precios de la base de datos.
export interface PedidoNuevo {
  direccion: string;
  telefono: string;
  nota: string;
  items: { productoId: number; talla: string; cantidad: number }[];
}

// Filas de los reportes
export interface VentaProducto {
  producto: string;
  unidades: number;
  totalVendido: number;
}

export interface PedidosPorEstado {
  estado: EstadoPedido;
  cantidad: number;
  total: number;
}
