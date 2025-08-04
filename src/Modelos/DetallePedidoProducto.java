package Modelos;

/**
 * Representa el detalle de un producto dentro de un pedido.
 * Almacena información sobre el producto, la cantidad y el precio en el momento de la compra.
 */
public class DetallePedidoProducto {
    private String productoNombre;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    /**
     * Constructor para DetallePedidoProducto.
     * @param productoNombre El nombre del producto.
     * @param cantidad La cantidad comprada del producto.
     * @param precioUnitario El precio unitario del producto en el momento de la compra.
     * @param subtotal El subtotal para este item (cantidad * precioUnitario).
     */
    public DetallePedidoProducto(String productoNombre, int cantidad, double precioUnitario, double subtotal) {
        this.productoNombre = productoNombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    // Getters y Setters
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}