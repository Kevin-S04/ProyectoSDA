package Modelos;

import java.sql.Timestamp;

public class Pedido {
    private int id;
    private Timestamp fecha;
    private String estado;
    private double total;
    private String ganaderoNombre;
    private String transportistaNombre;
    private String estadoEnvio;

    // Constructor con todos los campos
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