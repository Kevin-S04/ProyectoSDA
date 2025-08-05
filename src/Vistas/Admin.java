package Vistas;

import Controladores.AdminControlador;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Vista para el rol de Administrador. Proporciona una interfaz con pestañas para
 * gestionar usuarios, productos, pedidos y ver un historial de ventas.
 */
public class Admin extends JFrame {

    private final AdminControlador controlador;

    private JTable usuariosTable;
    private DefaultTableModel usuariosTableModel;
    private JButton crearUsuarioBtn, editarUsuarioBtn, eliminarUsuarioBtn;
    private JTable productosTable;
    private DefaultTableModel productosTableModel;
    private JButton crearProductoBtn, editarProductoBtn, eliminarProductoBtn;
    private JTable pedidosTable;
    private DefaultTableModel pedidosTableModel;
    private JButton verDetallesPedidoBtn, actualizarEstadoPedidoBtn, asignarTransportistaBtn;

    // --- NUEVOS COMPONENTES PARA HISTORIAL DE VENTAS ---
    private JLabel totalVentasLabel;
    private JLabel pedidosCompletadosLabel;
    private JTable topProductosTable;
    private DefaultTableModel topProductosTableModel;
    private JTable ultimosPedidosTable;
    private DefaultTableModel ultimosPedidosTableModel;

    private JButton salirBtn;

    /**
     * Constructor para la vista Admin. Inicializa la interfaz de usuario
     * y el controlador asociado.
     */
    public Admin() {
        this.controlador = new AdminControlador(this);

        setTitle("Panel de Administración");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout());
        salirBtn = new JButton("Salir al Login");
        topPanel.add(salirBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- PESTAÑAS DE USUARIOS, PRODUCTOS, PEDIDOS (SIN CAMBIOS) ---
        // PESTAÑA DE USUARIOS
        JPanel usuariosPanel = new JPanel(new BorderLayout(10, 10));
        usuariosTableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Correo", "Rol", "Teléfono", "Dirección"}, 0);
        usuariosTable = new JTable(usuariosTableModel);
        usuariosPanel.add(new JScrollPane(usuariosTable), BorderLayout.CENTER);
        JPanel usuariosBotonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        crearUsuarioBtn = new JButton("Crear Usuario");
        editarUsuarioBtn = new JButton("Editar Usuario");
        eliminarUsuarioBtn = new JButton("Eliminar Usuario");
        usuariosBotonesPanel.add(crearUsuarioBtn);
        usuariosBotonesPanel.add(editarUsuarioBtn);
        usuariosBotonesPanel.add(eliminarUsuarioBtn);
        usuariosPanel.add(usuariosBotonesPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Usuarios", usuariosPanel);

        // PESTAÑA DE PRODUCTOS
        JPanel productosPanel = new JPanel(new BorderLayout(10, 10));
        productosTableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Tipo", "Especie", "Precio", "Stock"}, 0);
        productosTable = new JTable(productosTableModel);
        productosPanel.add(new JScrollPane(productosTable), BorderLayout.CENTER);
        JPanel productosBotonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        crearProductoBtn = new JButton("Crear Producto");
        editarProductoBtn = new JButton("Editar Producto");
        eliminarProductoBtn = new JButton("Eliminar Producto");
        productosBotonesPanel.add(crearProductoBtn);
        productosBotonesPanel.add(editarProductoBtn);
        productosBotonesPanel.add(eliminarProductoBtn);
        productosPanel.add(productosBotonesPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Productos", productosPanel);

        // PESTAÑA DE PEDIDOS
        JPanel pedidosPanel = new JPanel(new BorderLayout(10, 10));
        pedidosTableModel = new DefaultTableModel(new Object[]{"ID Pedido", "Fecha", "Ganadero", "Estado", "Total"}, 0);
        pedidosTable = new JTable(pedidosTableModel);
        pedidosPanel.add(new JScrollPane(pedidosTable), BorderLayout.CENTER);
        JPanel pedidosBotonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        verDetallesPedidoBtn = new JButton("Ver Detalles");
        actualizarEstadoPedidoBtn = new JButton("Actualizar Estado");
        asignarTransportistaBtn = new JButton("Asignar Transportista");
        pedidosBotonesPanel.add(verDetallesPedidoBtn);
        pedidosBotonesPanel.add(actualizarEstadoPedidoBtn);
        pedidosBotonesPanel.add(asignarTransportistaBtn);
        pedidosPanel.add(pedidosBotonesPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Pedidos", pedidosPanel);

        // --- PESTAÑA DE HISTORIAL DE VENTAS (NUEVA IMPLEMENTACIÓN) ---
        JPanel historialVentasPanel = new JPanel(new BorderLayout(10, 10));

        // Panel de Métricas Resumen
        JPanel metricasPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        metricasPanel.setBorder(BorderFactory.createTitledBorder("Métricas Generales"));
        totalVentasLabel = new JLabel("Total de Ventas: $0.00", SwingConstants.CENTER);
        totalVentasLabel.setFont(new Font("Arial", Font.BOLD, 16));
        pedidosCompletadosLabel = new JLabel("Pedidos Completados: 0", SwingConstants.CENTER);
        pedidosCompletadosLabel.setFont(new Font("Arial", Font.BOLD, 16));
        metricasPanel.add(totalVentasLabel);
        metricasPanel.add(pedidosCompletadosLabel);
        historialVentasPanel.add(metricasPanel, BorderLayout.NORTH);

        // Panel Central con dos tablas
        JPanel centroPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Tabla de Top Productos
        JPanel topProductosPanel = new JPanel(new BorderLayout());
        topProductosPanel.setBorder(BorderFactory.createTitledBorder("Top 5 Productos Más Vendidos"));
        topProductosTableModel = new DefaultTableModel(new Object[]{"Producto", "Cantidad Vendida"}, 0);
        topProductosTable = new JTable(topProductosTableModel);
        topProductosPanel.add(new JScrollPane(topProductosTable), BorderLayout.CENTER);

        // Tabla de Últimos Pedidos
        JPanel ultimosPedidosPanel = new JPanel(new BorderLayout());
        ultimosPedidosPanel.setBorder(BorderFactory.createTitledBorder("Últimos 5 Pedidos Entregados"));
        ultimosPedidosTableModel = new DefaultTableModel(new Object[]{"ID Pedido", "Fecha", "Cliente", "Total"}, 0);
        ultimosPedidosTable = new JTable(ultimosPedidosTableModel);
        ultimosPedidosPanel.add(new JScrollPane(ultimosPedidosTable), BorderLayout.CENTER);

        centroPanel.add(topProductosPanel);
        centroPanel.add(ultimosPedidosPanel);
        historialVentasPanel.add(centroPanel, BorderLayout.CENTER);

        tabbedPane.addTab("Historial de Ventas", historialVentasPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // --- ASIGNACIÓN DE LISTENERS (SIN CAMBIOS PARA OTROS BOTONES) ---
        crearUsuarioBtn.addActionListener(e -> controlador.mostrarDialogoCrearUsuario());
        editarUsuarioBtn.addActionListener(e -> controlador.mostrarDialogoEditarUsuario());
        eliminarUsuarioBtn.addActionListener(e -> controlador.eliminarUsuario());
        crearProductoBtn.addActionListener(e -> controlador.mostrarDialogoCrearProducto());
        editarProductoBtn.addActionListener(e -> controlador.mostrarDialogoEditarProducto());
        eliminarProductoBtn.addActionListener(e -> controlador.eliminarProducto());
        verDetallesPedidoBtn.addActionListener(e -> controlador.mostrarDetallesPedido());
        actualizarEstadoPedidoBtn.addActionListener(e -> controlador.actualizarEstadoPedido());
        asignarTransportistaBtn.addActionListener(e -> controlador.asignarTransportista());
        salirBtn.addActionListener(e -> controlador.cerrarSesion());

        // --- LISTENER DE PESTAÑAS ACTUALIZADO ---
        tabbedPane.addChangeListener(e -> {
            JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
            int index = sourceTabbedPane.getSelectedIndex();
            String title = sourceTabbedPane.getTitleAt(index);

            switch (title) {
                case "Usuarios":
                    controlador.cargarUsuarios();
                    break;
                case "Productos":
                    controlador.cargarProductos();
                    break;
                case "Pedidos":
                    controlador.cargarPedidos();
                    break;
                case "Historial de Ventas":
                    controlador.cargarHistorialVentas(); // Nueva llamada
                    break;
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent windowEvent) {
                controlador.cargarUsuarios();
            }
        });
    }

    // --- GETTERS (CON NUEVOS GETTERS AL FINAL) ---
    public DefaultTableModel getUsuariosTableModel() { return usuariosTableModel; }
    public int getSelectedUserId() {
        int row = usuariosTable.getSelectedRow();
        return row != -1 ? (int) usuariosTableModel.getValueAt(row, 0) : -1;
    }
    public DefaultTableModel getProductosTableModel() { return productosTableModel; }
    public int getSelectedProductId() {
        int row = productosTable.getSelectedRow();
        return row != -1 ? (int) productosTableModel.getValueAt(row, 0) : -1;
    }
    public DefaultTableModel getPedidosTableModel() { return pedidosTableModel; }
    public int getSelectedPedidoId() {
        int row = pedidosTable.getSelectedRow();
        return row != -1 ? (int) pedidosTableModel.getValueAt(row, 0) : -1;
    }

    // --- Nuevos Getters para Historial de Ventas ---
    public JLabel getTotalVentasLabel() { return totalVentasLabel; }
    public JLabel getPedidosCompletadosLabel() { return pedidosCompletadosLabel; }
    public DefaultTableModel getTopProductosTableModel() { return topProductosTableModel; }
    public DefaultTableModel getUltimosPedidosTableModel() { return ultimosPedidosTableModel; }
}