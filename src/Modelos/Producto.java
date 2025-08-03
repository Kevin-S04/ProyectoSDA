package Modelos;

/**
 * Clase de modelo para representar un producto completo de la base de datos.
 */
public class Producto {
    private int id;
    private String nombre;
    private String tipo;
    private String especie;
    private String descripcion;
    private double precioUnitario;
    private String presentacion;
    private int stock;

    public Producto(int id, String nombre, String tipo, String especie, String descripcion, double precioUnitario, String presentacion, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.especie = especie;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.presentacion = presentacion;
        this.stock = stock;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock =stock;
    }
}
