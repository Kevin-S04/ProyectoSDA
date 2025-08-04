package Modelos;

import java.sql.Timestamp;

/**
 * Representa un pedido realizado por un usuario (Ganadero).
 * Contiene información sobre el pedido, su estado y el cliente.
 */
public class Pedido {
    private int id;
    private Timestamp fecha;
    private String estado;
    private double total;
    private String ganaderoNombre;
    private String transportistaNombre;
    private String estadoEnvio;

    /**
     * Constructor para la clase Pedido.
     * @param id El ID único del pedido.
     * @param fecha La fecha y hora en que se realizó el pedido.
     * @param estado El estado actual del pedido (ej. "Pendiente", "Enviado").
     * @param total El costo total del pedido.
     * @param ganaderoNombre El nombre del ganadero que realizó el pedido.
     */
    public Pedido(int id, Timestamp fecha, String estado, double total, String ganaderoNombre) {
        this.id = id;
        this.fecha = fecha;
        this.estado = estado;
        this.total = total;
        this.ganaderoNombre = ganaderoNombre;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getGanaderoNombre() { return ganaderoNombre; }
    public void setGanaderoNombre(String ganaderoNombre) { this.ganaderoNombre = ganaderoNombre; }
    public String getTransportistaNombre() { return transportistaNombre; }
    public void setTransportistaNombre(String transportistaNombre) { this.transportistaNombre = transportistaNombre; }
    public String getEstadoEnvio() { return estadoEnvio; }
    public void setEstadoEnvio(String estadoEnvio) { this.estadoEnvio = estadoEnvio; }
}