package Modelos;

/**
 * Representa un artículo dentro del carrito de compras.
 * Contiene información sobre el producto, la cantidad y el precio.
 */
public class CarritoItem {
    private int productoId;
    private String nombre;
    private int cantidad;
    private double precioUnitario;

    /**
     * Constructor para un artículo del carrito.
     * @param productoId El ID del producto.
     * @param nombre El nombre del producto.
     * @param cantidad La cantidad de este producto en el carrito.
     * @param precioUnitario El precio por unidad del producto.
     */
    public CarritoItem(int productoId, String nombre, int cantidad, double precioUnitario) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    /**
     * Obtiene el ID del producto.
     * @return El ID del producto.
     */
    public int getProductoId() {
        return productoId;
    }

    /**
     * Obtiene el nombre del producto.
     * @return El nombre del producto.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene la cantidad del producto.
     * @return La cantidad del producto.
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad del producto.
     * @param cantidad La nueva cantidad.
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene el precio unitario del producto.
     * @return El precio por unidad.
     */
    public double getPrecioUnitario() {
        return precioUnitario;
    }

    /**
     * Calcula y obtiene el subtotal para este artículo del carrito.
     * @return El subtotal (cantidad * precioUnitario).
     */
    public double getSubtotal() {
        return cantidad * precioUnitario;
    }
}