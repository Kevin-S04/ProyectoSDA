package Vistas;

import Controladores.GanaderoControlador;
import Modelos.Producto;
import Servicios.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import Vistas.ProductoListRenderer;

/**
 * Vista rediseñada para el rol de Ganadero.
 * <p>
 * Ofrece una experiencia de compra moderna con un catálogo de productos visual,
 * un carrito de compras interactivo y un historial de pedidos claro y funcional.
 * Utiliza la paleta de colores personalizada definida en {@link UIUtils}.
 */
public class Ganadero extends JFrame {

    private final GanaderoControlador controlador;
    private final int idGanadero;

    // --- Componentes Estructurales ---
    private JMenuBar menuBar;
    private JTabbedPane tabbedPane;
    private JLabel statusBar;

    // --- Pestaña Catálogo ---
    private JList<Producto> productoList;
    private DefaultListModel<Producto> productoListModel;
    private JSpinner cantidadSpinner;

    // --- Pestaña Carrito ---
    private JTable carritoTable;
    private DefaultTableModel carritoTableModel;
    private JLabel totalCarritoLabel;

    // --- Pestaña Mis Pedidos ---
    private JTable pedidosTable;
    private DefaultTableModel pedidosTableModel;

    /**
     * Constructor para la vista Ganadero.
     * @param idGanadero El ID del ganadero que ha iniciado sesión.
     */
    public Ganadero(int idGanadero) {
        this.idGanadero = idGanadero;
        this.controlador = new GanaderoControlador(this, idGanadero);

        setTitle("Panel del Ganadero - ProyectoSDA");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        crearMenuBar();
        setJMenuBar(menuBar);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FUENTE_NORMAL);
        tabbedPane.addTab("Catálogo de Productos", crearPanelCatalogo());
        tabbedPane.addTab("Carrito de Compras", crearPanelCarrito());
        tabbedPane.addTab("Mis Pedidos", crearPanelMisPedidos());

        statusBar = new JLabel(" ¡Bienvenido!");
        statusBar.setFont(UIUtils.FUENTE_NORMAL);
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        // Usamos el color de fondo personalizado
        getContentPane().setBackground(UIUtils.COLOR_FONDO);
        add(tabbedPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                controlador.cargarProductos();
            }
        });

        tabbedPane.addChangeListener(e -> {
            switch(tabbedPane.getSelectedIndex()) {
                case 0: controlador.cargarProductos(); break;
                case 1: controlador.actualizarVistaCarrito(); break;
                case 2: controlador.cargarPedidos(); break;
            }
        });
    }

    private void crearMenuBar() {
        menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setFont(UIUtils.FUENTE_NORMAL);

        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar Sesión");
        itemCerrarSesion.setFont(UIUtils.FUENTE_NORMAL);
        itemCerrarSesion.addActionListener(e -> controlador.cerrarSesion());

        menuArchivo.add(itemCerrarSesion);
        menuBar.add(menuArchivo);
    }

    private JPanel crearPanelCatalogo() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(UIUtils.BORDE_PANELES);
        panel.setBackground(UIUtils.COLOR_FONDO);

        productoListModel = new DefaultListModel<>();
        productoList = new JList<>(productoListModel);
        productoList.setCellRenderer(new ProductoListRenderer());
        productoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);

        JLabel cantidadLabel = new JLabel("Cantidad:");
        cantidadLabel.setFont(UIUtils.FUENTE_ETIQUETA);

        // --- LÍNEA CORREGIDA ---
        cantidadSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        cantidadSpinner.setFont(UIUtils.FUENTE_NORMAL);

        JButton agregarBtn = new JButton("Agregar al Carrito");
        UIUtils.estilizarBoton(agregarBtn, UIUtils.COLOR_PRINCIPAL);
        agregarBtn.addActionListener(e -> {
            Producto selectedProduct = productoList.getSelectedValue();
            int cantidad = (int) cantidadSpinner.getValue();
            controlador.agregarProductoAlCarrito(selectedProduct, cantidad);
        });

        actionPanel.add(cantidadLabel);
        actionPanel.add(cantidadSpinner);
        actionPanel.add(agregarBtn);

        panel.add(new JScrollPane(productoList), BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(UIUtils.BORDE_PANELES);
        panel.setBackground(UIUtils.COLOR_FONDO);

        carritoTableModel = new DefaultTableModel(new String[]{"Producto", "Cantidad", "Precio Unit.", "Subtotal"}, 0){
            public boolean isCellEditable(int row, int column) { return false; }
        };
        carritoTable = new JTable(carritoTableModel);
        configurarTabla(carritoTable);

        JPanel southPanel = new JPanel(new BorderLayout(10,10));
        southPanel.setOpaque(false);

        totalCarritoLabel = new JLabel("Total: $0.00");
        totalCarritoLabel.setFont(UIUtils.FUENTE_ETIQUETA.deriveFont(20f));
        totalCarritoLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonsPanel.setOpaque(false);

        JButton eliminarBtn = new JButton("Eliminar Seleccionado");
        UIUtils.estilizarBoton(eliminarBtn, UIUtils.COLOR_ACCION_NEGATIVA);
        eliminarBtn.addActionListener(e -> controlador.eliminarProductoDelCarrito());

        JButton realizarPedidoBtn = new JButton("Realizar Pedido");
        UIUtils.estilizarBoton(realizarPedidoBtn, UIUtils.COLOR_ACCION_POSITIVA);
        realizarPedidoBtn.addActionListener(e -> controlador.realizarPedido());

        buttonsPanel.add(eliminarBtn);
        buttonsPanel.add(realizarPedidoBtn);

        southPanel.add(buttonsPanel, BorderLayout.WEST);
        southPanel.add(totalCarritoLabel, BorderLayout.EAST);

        panel.add(new JScrollPane(carritoTable), BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelMisPedidos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(UIUtils.BORDE_PANELES);
        panel.setBackground(UIUtils.COLOR_FONDO);

        pedidosTableModel = new DefaultTableModel(new String[]{"ID Pedido", "Fecha", "Estado", "Total"}, 0){
            public boolean isCellEditable(int row, int column) { return false; }
        };
        pedidosTable = new JTable(pedidosTableModel);
        configurarTabla(pedidosTable);

        // Panel de botones para las acciones de "Mis Pedidos"
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);

        JButton detallesBtn = new JButton("Ver Detalles del Pedido");
        UIUtils.estilizarBoton(detallesBtn, UIUtils.COLOR_SECUNDARIO);
        detallesBtn.addActionListener(e -> controlador.mostrarDetallesPedido());

        // Se restaura el botón de Generar Factura
        JButton facturaBtn = new JButton("Generar Factura (.txt)");
        UIUtils.estilizarBoton(facturaBtn, UIUtils.COLOR_PRINCIPAL);
        facturaBtn.addActionListener(e -> controlador.generarFacturaTxt());

        actionPanel.add(detallesBtn);
        actionPanel.add(facturaBtn);

        panel.add(new JScrollPane(pedidosTable), BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void configurarTabla(JTable table) {
        table.setFont(UIUtils.FUENTE_NORMAL);
        table.setRowHeight(30);
        table.getTableHeader().setFont(UIUtils.FUENTE_ETIQUETA);
        table.getTableHeader().setBackground(UIUtils.COLOR_CABECERA_TABLA);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void showStatus(String message, boolean isError) {
        statusBar.setText(" " + message);
        statusBar.setForeground(isError ? UIUtils.COLOR_ACCION_NEGATIVA : UIUtils.COLOR_TEXTO_NORMAL);
        Timer timer = new Timer(5000, e -> statusBar.setText(" ¡Bienvenido!"));
        timer.setRepeats(false);
        timer.start();
    }

    // --- Getters y Setters para el Controlador ---
    public DefaultListModel<Producto> getProductoListModel() { return productoListModel; }
    public DefaultTableModel getCarritoTableModel() { return carritoTableModel; }
    public JLabel getTotalCarritoLabel() { return totalCarritoLabel; }
    public int getSelectedCarritoRowIndex() { return carritoTable.getSelectedRow(); }
    public void setSelectedCarritoRowIndex(int index) {
        carritoTable.setRowSelectionInterval(index, index);  // Selecciona la fila `index` en la tabla
    }
    public DefaultTableModel getPedidosTableModel() { return pedidosTableModel; }
    public int getSelectedPedidoId() {
        int selectedRow = pedidosTable.getSelectedRow();
        return (selectedRow != -1) ? (int) pedidosTable.getValueAt(selectedRow, 0) : -1;
    }

    public void setCarritoTable(JTable carritoTable) {
        this.carritoTable = carritoTable;
    }

    public JTable getProductosTable() { return null; } // No se usa tabla en el nuevo diseño
    public JTable getCarritoTable() { return carritoTable; }
    // Se mantiene por si el controlador lo necesita
}