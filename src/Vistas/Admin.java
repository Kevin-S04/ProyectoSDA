package Vistas;

import Controladores.AdminControlador;
import Servicios.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Vista del Administrador con un diseño visual pulido y completo.
 * <p>
 * Utiliza la paleta de diseño de {@link UIUtils} para una apariencia profesional y coherente.
 * Esta clase sirve como la plantilla de diseño definitiva para las demás vistas del sistema.
 */
public class Admin extends JFrame {

    private final AdminControlador controlador;

    // --- Componentes Estructurales ---
    private JMenuBar menuBar;
    private JTabbedPane tabbedPane;
    private JLabel statusBar;

    // --- Pestaña Usuarios ---
    private JTable usuariosTable;
    private DefaultTableModel usuariosTableModel;
    private JTextField buscarUsuariosField;
    private TableRowSorter<DefaultTableModel> sorterUsuarios;

    // --- Pestaña Productos ---
    private JTable productosTable;
    private DefaultTableModel productosTableModel;
    private JTextField buscarProductosField;
    private TableRowSorter<DefaultTableModel> sorterProductos;

    // --- Pestaña Pedidos ---
    private JTable pedidosTable;
    private DefaultTableModel pedidosTableModel;
    private JTextField buscarPedidosField;
    private TableRowSorter<DefaultTableModel> sorterPedidos;

    // --- Pestaña Historial de Ventas ---
    private JLabel totalVentasLabel;
    private JLabel pedidosCompletadosLabel;
    private JTable topProductosTable;
    private DefaultTableModel topProductosTableModel;
    private JTable ultimosPedidosTable;
    private DefaultTableModel ultimosPedidosTableModel;


    /**
     * Constructor que inicializa y ensambla la interfaz de administrador completa.
     */
    public Admin() {
        this.controlador = new AdminControlador(this);

        setTitle("Panel de Administración - ProyectoSDA");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        crearMenuBar();
        setJMenuBar(menuBar);

        // Crear las pestañas con sus respectivos paneles de contenido
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FUENTE_NORMAL);
        tabbedPane.addTab("Gestión de Usuarios", crearPanelUsuarios());
        tabbedPane.addTab("Gestión de Productos", crearPanelProductos());
        tabbedPane.addTab("Gestión de Pedidos", crearPanelPedidos());
        tabbedPane.addTab("Historial de Ventas", crearPanelHistorialVentas());

        // Barra de estado para notificaciones no intrusivas
        statusBar = new JLabel(" Listo");
        statusBar.setFont(UIUtils.FUENTE_NORMAL);
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        // Ensamblado final de la ventana
        getContentPane().setBackground(UIUtils.COLOR_FONDO);
        add(tabbedPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // Listener para cargar datos de la primera pestaña al abrir la ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                controlador.cargarUsuarios();
            }
        });

        // Listener para recargar los datos correspondientes al cambiar de pestaña
        tabbedPane.addChangeListener(e -> {
            switch(tabbedPane.getSelectedIndex()){
                case 0: controlador.cargarUsuarios(); break;
                case 1: controlador.cargarProductos(); break;
                case 2: controlador.cargarPedidos(); break;
                case 3: controlador.cargarHistorialVentas(); break;
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

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setFont(UIUtils.FUENTE_NORMAL);
        itemSalir.addActionListener(e -> System.exit(0));

        menuArchivo.add(itemCerrarSesion);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        menuBar.add(menuArchivo);
    }

    /**
     * Crea el panel completo para la pestaña de "Gestión de Usuarios".
     * @return Un JPanel configurado con toolbar, campo de búsqueda y tabla.
     */
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(UIUtils.BORDE_PANELES);
        panel.setBackground(UIUtils.COLOR_FONDO);

        // Barra de Herramientas Superior
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);

        JButton crearBtn = new JButton("Crear Usuario");
        // AHORA LLAMAMOS AL MÉTODO DE UIUTILS
        UIUtils.estilizarBoton(crearBtn, UIUtils.COLOR_ACCION_POSITIVA);
        crearBtn.addActionListener(e -> controlador.mostrarDialogoCrearUsuario());

        JButton editarBtn = new JButton("Editar Seleccionado");
        UIUtils.estilizarBoton(editarBtn, UIUtils.COLOR_SECUNDARIO);
        editarBtn.addActionListener(e -> controlador.mostrarDialogoEditarUsuario());

        JButton eliminarBtn = new JButton("Eliminar Seleccionado");
        UIUtils.estilizarBoton(eliminarBtn, UIUtils.COLOR_ACCION_NEGATIVA);
        eliminarBtn.addActionListener(e -> controlador.eliminarUsuario());

        toolBar.add(crearBtn);
        toolBar.add(new JToolBar.Separator());
        toolBar.add(editarBtn);
        toolBar.add(eliminarBtn);

        // Campo de Búsqueda
        buscarUsuariosField = new JTextField(20);
        JPanel searchPanel = crearPanelDeBusqueda(buscarUsuariosField);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(toolBar, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        // Tabla de Usuarios
        usuariosTableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Correo", "Rol", "Teléfono", "Dirección"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        usuariosTable = new JTable(usuariosTableModel);
        configurarTabla(usuariosTable);

        sorterUsuarios = new TableRowSorter<>(usuariosTableModel);
        usuariosTable.setRowSorter(sorterUsuarios);
        buscarUsuariosField.getDocument().addDocumentListener(createSearchListener(sorterUsuarios, buscarUsuariosField));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(usuariosTable), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea el panel completo para la pestaña de "Gestión de Productos".
     * @return Un JPanel configurado.
     */
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(UIUtils.BORDE_PANELES);
        panel.setBackground(UIUtils.COLOR_FONDO);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);

        JButton crearBtn = new JButton("Crear Producto");
        UIUtils.estilizarBoton(crearBtn, UIUtils.COLOR_ACCION_POSITIVA);
        crearBtn.addActionListener(e -> controlador.mostrarDialogoCrearProducto());

        JButton editarBtn = new JButton("Editar Producto");
        UIUtils.estilizarBoton(editarBtn, UIUtils.COLOR_SECUNDARIO);
        editarBtn.addActionListener(e -> controlador.mostrarDialogoEditarProducto());

        JButton eliminarBtn = new JButton("Eliminar Producto");
        UIUtils.estilizarBoton(eliminarBtn, UIUtils.COLOR_ACCION_NEGATIVA);
        eliminarBtn.addActionListener(e -> controlador.eliminarProducto());

        toolBar.add(crearBtn);
        toolBar.add(new JToolBar.Separator());
        toolBar.add(editarBtn);
        toolBar.add(eliminarBtn);

        buscarProductosField = new JTextField(20);
        JPanel searchPanel = crearPanelDeBusqueda(buscarProductosField);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(toolBar, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        productosTableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Tipo", "Especie", "Precio", "Stock"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        productosTable = new JTable(productosTableModel);
        configurarTabla(productosTable);

        sorterProductos = new TableRowSorter<>(productosTableModel);
        productosTable.setRowSorter(sorterProductos);
        buscarProductosField.getDocument().addDocumentListener(createSearchListener(sorterProductos, buscarProductosField));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(productosTable), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea el panel completo para la pestaña de "Gestión de Pedidos".
     * @return Un JPanel configurado.
     */
    private JPanel crearPanelPedidos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(UIUtils.BORDE_PANELES);
        panel.setBackground(UIUtils.COLOR_FONDO);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);

        JButton detallesBtn = new JButton("Ver Detalles");
        UIUtils.estilizarBoton(detallesBtn, UIUtils.COLOR_SECUNDARIO);
        detallesBtn.addActionListener(e -> controlador.mostrarDetallesPedido());

        JButton estadoBtn = new JButton("Actualizar Estado");
        UIUtils.estilizarBoton(estadoBtn, UIUtils.COLOR_SECUNDARIO);
        estadoBtn.addActionListener(e -> controlador.actualizarEstadoPedido());

        JButton transportistaBtn = new JButton("Asignar Transportista");
        UIUtils.estilizarBoton(transportistaBtn, UIUtils.COLOR_PRINCIPAL);
        transportistaBtn.addActionListener(e -> controlador.asignarTransportista());

        toolBar.add(detallesBtn);
        toolBar.add(new JToolBar.Separator());
        toolBar.add(estadoBtn);
        toolBar.add(transportistaBtn);

        buscarPedidosField = new JTextField(20);
        JPanel searchPanel = crearPanelDeBusqueda(buscarPedidosField);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(toolBar, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        pedidosTableModel = new DefaultTableModel(new String[]{"ID Pedido", "Fecha", "Ganadero", "Estado", "Total"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        pedidosTable = new JTable(pedidosTableModel);
        configurarTabla(pedidosTable);

        sorterPedidos = new TableRowSorter<>(pedidosTableModel);
        pedidosTable.setRowSorter(sorterPedidos);
        buscarPedidosField.getDocument().addDocumentListener(createSearchListener(sorterPedidos, buscarPedidosField));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(pedidosTable), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Crea el panel de dashboard para el "Historial de Ventas".
     * @return Un JPanel configurado.
     */
    private JPanel crearPanelHistorialVentas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(UIUtils.BORDE_PANELES);
        panel.setBackground(UIUtils.COLOR_FONDO);

        // Panel de Métricas Superiores
        JPanel metricasPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        metricasPanel.setOpaque(false);
        metricasPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        totalVentasLabel = new JLabel("Total de Ventas: $0.00", SwingConstants.CENTER);
        totalVentasLabel.setFont(UIUtils.FUENTE_TITULO_LOGIN.deriveFont(22f));
        totalVentasLabel.setForeground(UIUtils.COLOR_ACCION_POSITIVA);

        pedidosCompletadosLabel = new JLabel("Pedidos Completados: 0", SwingConstants.CENTER);
        pedidosCompletadosLabel.setFont(UIUtils.FUENTE_TITULO_LOGIN.deriveFont(22f));
        pedidosCompletadosLabel.setForeground(UIUtils.COLOR_PRINCIPAL);

        metricasPanel.add(totalVentasLabel);
        metricasPanel.add(pedidosCompletadosLabel);

        // Panel Central con las tablas
        JPanel tablasPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        tablasPanel.setOpaque(false);

        topProductosTableModel = new DefaultTableModel(new String[]{"Producto", "Cantidad Vendida"}, 0);
        topProductosTable = new JTable(topProductosTableModel);
        configurarTabla(topProductosTable);
        JScrollPane topProductosScroll = new JScrollPane(topProductosTable);
        topProductosScroll.setBorder(UIUtils.BORDE_TITULADO("Top 5 Productos Más Vendidos"));

        ultimosPedidosTableModel = new DefaultTableModel(new String[]{"ID Pedido", "Fecha", "Cliente", "Total"}, 0);
        ultimosPedidosTable = new JTable(ultimosPedidosTableModel);
        configurarTabla(ultimosPedidosTable);
        JScrollPane ultimosPedidosScroll = new JScrollPane(ultimosPedidosTable);
        ultimosPedidosScroll.setBorder(UIUtils.BORDE_TITULADO("Últimos 5 Pedidos Entregados"));

        tablasPanel.add(topProductosScroll);
        tablasPanel.add(ultimosPedidosScroll);

        panel.add(metricasPanel, BorderLayout.NORTH);
        panel.add(tablasPanel, BorderLayout.CENTER);
        return panel;
    }

    // --- MÉTODOS DE UTILIDAD PRIVADOS ---

    /**
     * Aplica una configuración visual estándar y profesional a una JTable.
     * @param table La tabla a configurar.
     */
    private void configurarTabla(JTable table) {
        table.setFont(UIUtils.FUENTE_NORMAL);
        table.setRowHeight(30);
        table.getTableHeader().setFont(UIUtils.FUENTE_ETIQUETA);
        table.getTableHeader().setBackground(UIUtils.COLOR_CABECERA_TABLA);
        table.getTableHeader().setForeground(UIUtils.COLOR_TEXTO_NORMAL);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(210, 210, 210));
    }

    /**
     * Crea un panel de búsqueda estandarizado.
     * @param searchField El campo de texto que se usará para la búsqueda.
     * @return Un JPanel configurado.
     */
    private JPanel crearPanelDeBusqueda(JTextField searchField) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Buscar:");
        searchLabel.setFont(UIUtils.FUENTE_ETIQUETA);
        searchField.setFont(UIUtils.FUENTE_NORMAL);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        return searchPanel;
    }

    /**
     * Crea un DocumentListener para el filtrado de tablas.
     * @param sorter El TableRowSorter asociado a la tabla.
     * @param searchField El JTextField que dispara el filtro.
     * @return Un DocumentListener configurado.
     */
    private javax.swing.event.DocumentListener createSearchListener(TableRowSorter<DefaultTableModel> sorter, JTextField searchField) {
        return new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            void filtrar() {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchField.getText()));
            }
        };
    }

    // --- GETTERS PARA EL CONTROLADOR ---
    public DefaultTableModel getUsuariosTableModel() { return usuariosTableModel; }
    public int getSelectedUserId() { int row = usuariosTable.getSelectedRow(); return row != -1 ? (int) usuariosTable.getValueAt(row, 0) : -1; }

    public DefaultTableModel getProductosTableModel() { return productosTableModel; }
    public int getSelectedProductId() { int row = productosTable.getSelectedRow(); return row != -1 ? (int) productosTable.getValueAt(row, 0) : -1; }

    public DefaultTableModel getPedidosTableModel() { return pedidosTableModel; }
    public int getSelectedPedidoId() { int row = pedidosTable.getSelectedRow(); return row != -1 ? (int) pedidosTable.getValueAt(row, 0) : -1; }

    public JLabel getTotalVentasLabel() { return totalVentasLabel; }
    public JLabel getPedidosCompletadosLabel() { return pedidosCompletadosLabel; }
    public DefaultTableModel getTopProductosTableModel() { return topProductosTableModel; }
    public DefaultTableModel getUltimosPedidosTableModel() { return ultimosPedidosTableModel; }
}