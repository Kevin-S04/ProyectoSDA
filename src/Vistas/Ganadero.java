package Vistas;

import Controladores.GanaderoControlador;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Ganadero extends JFrame {

    private final GanaderoControlador controlador;
    private final int idGanadero;

    // Pestaña Catálogo
    private JTable productosTable;
    private DefaultTableModel productosTableModel;
    private JButton agregarAlCarritoBtn;

    // Pestaña Carrito
    private JTable carritoTable;
    private DefaultTableModel carritoTableModel;
    private JButton eliminarDelCarritoBtn;
    private JButton realizarPedidoBtn;
    private JLabel totalCarritoLabel;

    // Pestaña Mis Pedidos
    private JTable pedidosTable;
    private DefaultTableModel pedidosTableModel;
    private JButton verDetallesPedidoBtn;

    private JButton salirBtn;

    public Ganadero(int idGanadero) {
        this.idGanadero = idGanadero;
        this.controlador = new GanaderoControlador(this, idGanadero);

        setTitle("Panel del Ganadero");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout());
        salirBtn = new JButton("Salir al Login");
        topPanel.add(salirBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- PESTAÑA CATÁLOGO DE PRODUCTOS ---
        JPanel catalogoPanel = new JPanel(new BorderLayout(10, 10));
        productosTableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Tipo", "Especie", "Descripción", "Precio", "Presentación", "Stock"}, 0);
        productosTable = new JTable(productosTableModel);
        catalogoPanel.add(new JScrollPane(productosTable), BorderLayout.CENTER);
        agregarAlCarritoBtn = new JButton("Agregar al Carrito");
        catalogoPanel.add(agregarAlCarritoBtn, BorderLayout.SOUTH);
        tabbedPane.addTab("Catálogo de Productos", catalogoPanel);

        // --- PESTAÑA CARRITO DE COMPRAS ---
        JPanel carritoPanel = new JPanel(new BorderLayout(10, 10));
        carritoTableModel = new DefaultTableModel(new Object[]{"ID Producto", "Nombre", "Cantidad", "Precio Unit.", "Subtotal"}, 0);
        carritoTable = new JTable(carritoTableModel);
        carritoPanel.add(new JScrollPane(carritoTable), BorderLayout.CENTER);

        JPanel carritoSurPanel = new JPanel(new BorderLayout());
        JPanel carritoBotonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        eliminarDelCarritoBtn = new JButton("Eliminar del Carrito");
        realizarPedidoBtn = new JButton("Realizar Pedido");
        carritoBotonesPanel.add(eliminarDelCarritoBtn);
        carritoBotonesPanel.add(realizarPedidoBtn);

        totalCarritoLabel = new JLabel("Total: $0.00", SwingConstants.CENTER);
        totalCarritoLabel.setFont(new Font("Arial", Font.BOLD, 16));

        carritoSurPanel.add(carritoBotonesPanel, BorderLayout.NORTH);
        carritoSurPanel.add(totalCarritoLabel, BorderLayout.SOUTH);
        carritoPanel.add(carritoSurPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Carrito de Compras", carritoPanel);

        // --- PESTAÑA MIS PEDIDOS ---
        JPanel misPedidosPanel = new JPanel(new BorderLayout(10, 10));
        pedidosTableModel = new DefaultTableModel(new Object[]{"ID Pedido", "Fecha", "Estado", "Total"}, 0);
        pedidosTable = new JTable(pedidosTableModel);
        misPedidosPanel.add(new JScrollPane(pedidosTable), BorderLayout.CENTER);
        verDetallesPedidoBtn = new JButton("Ver Detalles del Pedido");
        misPedidosPanel.add(verDetallesPedidoBtn, BorderLayout.SOUTH);
        tabbedPane.addTab("Mis Pedidos", misPedidosPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Listeners
        salirBtn.addActionListener(e -> controlador.cerrarSesion());
        agregarAlCarritoBtn.addActionListener(e -> controlador.agregarProductoAlCarrito());
        eliminarDelCarritoBtn.addActionListener(e -> controlador.eliminarProductoDelCarrito());
        realizarPedidoBtn.addActionListener(e -> controlador.realizarPedido());
        verDetallesPedidoBtn.addActionListener(e -> controlador.mostrarDetallesPedido());

        tabbedPane.addChangeListener(e -> {
            int index = ((JTabbedPane)e.getSource()).getSelectedIndex();
            String title = ((JTabbedPane)e.getSource()).getTitleAt(index);
            switch (title) {
                case "Catálogo de Productos":
                    controlador.cargarProductos();
                    break;
                case "Carrito de Compras":
                    controlador.actualizarVistaCarrito();
                    break;
                case "Mis Pedidos":
                    controlador.cargarPedidos();
                    break;
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent windowEvent) {
                controlador.cargarProductos();
            }
        });
    }

    // Getters para el controlador
    public int getSelectedProductId() {
        int selectedRow = productosTable.getSelectedRow();
        return selectedRow != -1 ? (int) productosTableModel.getValueAt(selectedRow, 0) : -1;
    }

    public int getSelectedCarritoProductId() {
        int selectedRow = carritoTable.getSelectedRow();
        return selectedRow != -1 ? (int) carritoTableModel.getValueAt(selectedRow, 0) : -1;
    }

    public int getSelectedPedidoId() {
        int selectedRow = pedidosTable.getSelectedRow();
        return selectedRow != -1 ? (int) pedidosTableModel.getValueAt(selectedRow, 0) : -1;
    }

    public DefaultTableModel getProductosTableModel() { return productosTableModel; }
    public DefaultTableModel getCarritoTableModel() { return carritoTableModel; }
    public DefaultTableModel getPedidosTableModel() { return pedidosTableModel; }
    public JLabel getTotalCarritoLabel() { return totalCarritoLabel; }

    // --- MÉTODOS GETTER AÑADIDOS ---
    public JTable getProductosTable() { return productosTable; }
    public JTable getCarritoTable() { return carritoTable; }
}
