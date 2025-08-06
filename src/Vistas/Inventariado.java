package Vistas;

import Controladores.InventariadoControlador;
import Servicios.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Vista rediseñada para el rol de Inventariado, enfocada en la eficiencia.
 * <p>
 * Permite la edición directa del stock en la tabla y resalta visualmente los productos
 * con bajo inventario para una gestión rápida y clara.
 */
public class Inventariado extends JFrame {

    private final InventariadoControlador controlador;

    // --- Componentes Estructurales ---
    private JMenuBar menuBar;
    private JTabbedPane tabbedPane;
    private JLabel statusBar;

    // --- Pestaña Stock de Productos ---
    private JTable productosTable;
    private DefaultTableModel productosTableModel;
    private JTextField buscarProductosField;
    private TableRowSorter<DefaultTableModel> sorterProductos;
    private JButton guardarCambiosBtn;

    // --- Pestaña Pedidos Pendientes ---
    private JTable pedidosTable;
    private DefaultTableModel pedidosTableModel;

    public Inventariado() {
        this.controlador = new InventariadoControlador(this);

        setTitle("Panel de Inventariado - ProyectoSDA");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        crearMenuBar();
        setJMenuBar(menuBar);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FUENTE_NORMAL);
        tabbedPane.addTab("Gestión de Stock", crearPanelStock());
        tabbedPane.addTab("Pedidos Pendientes", crearPanelPedidos());

        statusBar = new JLabel(" Listo");
        statusBar.setFont(UIUtils.FUENTE_NORMAL);
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        add(tabbedPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                controlador.cargarProductos();
            }
        });

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                controlador.cargarProductos();
            } else {
                controlador.cargarPedidosPendientes();
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

    private JPanel crearPanelStock() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(UIUtils.BORDE_PANELES);
        panel.setBackground(UIUtils.COLOR_FONDO);

        // --- Barra de Herramientas ---
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);

        guardarCambiosBtn = new JButton("Guardar Cambios de Stock");
        UIUtils.estilizarBoton(guardarCambiosBtn, UIUtils.COLOR_ACCION_POSITIVA);
        guardarCambiosBtn.setEnabled(false); // Deshabilitado hasta que haya cambios
        guardarCambiosBtn.addActionListener(e -> {
            controlador.guardarCambiosDeStock();
            guardarCambiosBtn.setEnabled(false);
        });

        JButton refrescarBtn = new JButton("Refrescar Tabla");
        UIUtils.estilizarBoton(refrescarBtn, UIUtils.COLOR_SECUNDARIO);
        refrescarBtn.addActionListener(e -> controlador.cargarProductos());

        toolBar.add(guardarCambiosBtn);
        toolBar.add(refrescarBtn);

        // --- Panel de Búsqueda ---
        buscarProductosField = new JTextField(20);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(buscarProductosField);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(toolBar, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        // --- Tabla de Productos (con columna de Stock editable) ---
        productosTableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Tipo", "Especie", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo la columna "Stock" (índice 4) es editable
                return column == 4;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Integer.class; // Asegura que el editor sea numérico
                return String.class;
            }
        };

        productosTable = new JTable(productosTableModel);
        configurarTabla(productosTable);

        // Aplicar el renderer especial a la columna de Stock
        TableColumn stockColumn = productosTable.getColumnModel().getColumn(4);
        stockColumn.setCellRenderer(new StockTableCellRenderer());

        // Habilitar el botón de guardar cuando se edite una celda
        productosTableModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                guardarCambiosBtn.setEnabled(true);
            }
        });

        sorterProductos = new TableRowSorter<>(productosTableModel);
        productosTable.setRowSorter(sorterProductos);
        buscarProductosField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            void filtrar() {
                sorterProductos.setRowFilter(RowFilter.regexFilter("(?i)" + buscarProductosField.getText()));
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(productosTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelPedidos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(UIUtils.BORDE_PANELES);
        panel.setBackground(UIUtils.COLOR_FONDO);

        pedidosTableModel = new DefaultTableModel(new String[]{"ID Pedido", "Fecha", "Ganadero", "Estado"}, 0);
        pedidosTable = new JTable(pedidosTableModel);
        configurarTabla(pedidosTable);

        JButton detallesBtn = new JButton("Ver Detalles del Pedido");
        UIUtils.estilizarBoton(detallesBtn, UIUtils.COLOR_SECUNDARIO);
        detallesBtn.addActionListener(e -> controlador.mostrarDetallesPedido());

        panel.add(new JScrollPane(pedidosTable), BorderLayout.CENTER);
        panel.add(detallesBtn, BorderLayout.SOUTH);

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
        statusBar.setForeground(isError ? UIUtils.COLOR_ACCION_NEGATIVA : Color.BLACK);
        Timer timer = new Timer(5000, e -> statusBar.setText(" Listo"));
        timer.setRepeats(false);
        timer.start();
    }

    // --- Getters para el Controlador ---
    public DefaultTableModel getProductosTableModel() { return productosTableModel; }
    public DefaultTableModel getPedidosTableModel() { return pedidosTableModel; }
    public int getSelectedPedidoId() {
        int selectedRow = pedidosTable.getSelectedRow();
        return (selectedRow != -1) ? (int) pedidosTable.getValueAt(selectedRow, 0) : -1;
    }
    public JTable getProductosTable() { return productosTable; }
}