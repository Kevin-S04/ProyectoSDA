package Vistas;

import Controladores.InventariadoControlador;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Inventariado extends JFrame {

    private final InventariadoControlador controlador;

    // Componentes de la pestaña de Stock de Productos
    private JTable productosTable;
    private DefaultTableModel productosTableModel;
    private JButton actualizarStockBtn;

    // Componentes de la pestaña de Pedidos Pendientes
    private JTable pedidosTable;
    private DefaultTableModel pedidosTableModel;
    private JButton verDetallesPedidoBtn;

    private JButton salirBtn;

    public Inventariado() {
        this.controlador = new InventariadoControlador(this);

        // Configuración de la ventana principal
        setTitle("Panel de Inventariado");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout());
        salirBtn = new JButton("Salir al Login");
        topPanel.add(salirBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // ----------------------------------------
        // PESTAÑA DE STOCK DE PRODUCTOS
        // ----------------------------------------
        JPanel stockPanel = new JPanel(new BorderLayout(10, 10));

        productosTableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Tipo", "Especie", "Stock"}, 0);
        productosTable = new JTable(productosTableModel);
        JScrollPane productosScrollPane = new JScrollPane(productosTable);
        stockPanel.add(productosScrollPane, BorderLayout.CENTER);

        JPanel stockBotonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actualizarStockBtn = new JButton("Actualizar Stock");
        stockBotonesPanel.add(actualizarStockBtn);
        stockPanel.add(stockBotonesPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Stock de Productos", stockPanel);

        // ----------------------------------------
        // PESTAÑA DE PEDIDOS PENDIENTES
        // ----------------------------------------
        JPanel pedidosPanel = new JPanel(new BorderLayout(10, 10));

        pedidosTableModel = new DefaultTableModel(new Object[]{"ID Pedido", "Fecha", "Ganadero", "Estado"}, 0);
        pedidosTable = new JTable(pedidosTableModel);
        JScrollPane pedidosScrollPane = new JScrollPane(pedidosTable);
        pedidosPanel.add(pedidosScrollPane, BorderLayout.CENTER);

        JPanel pedidosBotonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        verDetallesPedidoBtn = new JButton("Ver Detalles del Pedido");
        pedidosBotonesPanel.add(verDetallesPedidoBtn);
        pedidosPanel.add(pedidosBotonesPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Pedidos Pendientes", pedidosPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Asignamos los listeners
        actualizarStockBtn.addActionListener(e -> controlador.mostrarDialogoActualizarStock());
        verDetallesPedidoBtn.addActionListener(e -> controlador.mostrarDetallesPedido());
        salirBtn.addActionListener(e -> controlador.cerrarSesion());

        // Listener para cargar datos cuando se cambie de pestaña
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                String title = sourceTabbedPane.getTitleAt(index);

                if (title.equals("Stock de Productos")) {
                    controlador.cargarProductos();
                } else if (title.equals("Pedidos Pendientes")) {
                    controlador.cargarPedidosPendientes();
                }
            }
        });

        // Al mostrar la ventana, cargamos los productos
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent windowEvent) {
                controlador.cargarProductos();
            }
        });
    }

    // Getters para que el controlador pueda acceder a los componentes
    public DefaultTableModel getProductosTableModel() {
        return productosTableModel;
    }

    public int getSelectedProductId() {
        int selectedRow = productosTable.getSelectedRow();
        if (selectedRow != -1) {
            return (int) productosTableModel.getValueAt(selectedRow, 0);
        }
        return -1;
    }

    public DefaultTableModel getPedidosTableModel() {
        return pedidosTableModel;
    }

    public int getSelectedPedidoId() {
        int selectedRow = pedidosTable.getSelectedRow();
        if (selectedRow != -1) {
            return (int) pedidosTableModel.getValueAt(selectedRow, 0);
        }
        return -1;
    }
}