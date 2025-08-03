package Vistas;

import Controladores.AdminControlador;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Ventana principal para el rol de Administrador.
 * Contiene un JTabbedPane para gestionar usuarios, productos, pedidos y revisar el historial de ventas.
 */
public class Admin extends JFrame {

    private final AdminControlador controlador;

    // Componentes de la pestaña de Usuarios
    private JTable usuariosTable;
    private DefaultTableModel usuariosTableModel;
    private JButton crearUsuarioBtn;
    private JButton editarUsuarioBtn;
    private JButton eliminarUsuarioBtn;

    // Componentes de la pestaña de Productos
    private JTable productosTable;
    private DefaultTableModel productosTableModel;
    private JButton crearProductoBtn;
    private JButton editarProductoBtn;
    private JButton eliminarProductoBtn;

    // Componentes de la pestaña de Pedidos
    private JTable pedidosTable;
    private DefaultTableModel pedidosTableModel;
    private JButton verDetallesPedidoBtn;
    private JButton actualizarEstadoPedidoBtn;
    private JButton asignarTransportistaBtn;

    private JButton salirBtn;

    /**
     * Constructor de la ventana de administrador.
     * Inicializa la interfaz de usuario con sus componentes y asigna el controlador.
     */
    public Admin() {
        this.controlador = new AdminControlador(this);

        // Configuración de la ventana principal
        setTitle("Panel de Administración");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar la ventana en la pantalla

        // Panel superior para el botón de salir
        JPanel topPanel = new JPanel(new BorderLayout());
        salirBtn = new JButton("Salir al Login");
        topPanel.add(salirBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Creamos el panel de pestañas
        JTabbedPane tabbedPane = new JTabbedPane();

        // ----------------------------------------
        // PESTAÑA DE USUARIOS
        // ----------------------------------------
        JPanel usuariosPanel = new JPanel(new BorderLayout(10, 10));

        usuariosTableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Correo", "Rol", "Teléfono", "Dirección"}, 0);
        usuariosTable = new JTable(usuariosTableModel);
        JScrollPane usuariosScrollPane = new JScrollPane(usuariosTable);
        usuariosPanel.add(usuariosScrollPane, BorderLayout.CENTER);

        JPanel usuariosBotonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        crearUsuarioBtn = new JButton("Crear Usuario");
        editarUsuarioBtn = new JButton("Editar Usuario");
        eliminarUsuarioBtn = new JButton("Eliminar Usuario");
        usuariosBotonesPanel.add(crearUsuarioBtn);
        usuariosBotonesPanel.add(editarUsuarioBtn);
        usuariosBotonesPanel.add(eliminarUsuarioBtn);
        usuariosPanel.add(usuariosBotonesPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Usuarios", usuariosPanel);

        // ----------------------------------------
        // PESTAÑA DE PRODUCTOS
        // ----------------------------------------
        JPanel productosPanel = new JPanel(new BorderLayout(10, 10));

        productosTableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Tipo", "Especie", "Precio", "Stock"}, 0);
        productosTable = new JTable(productosTableModel);
        JScrollPane productosScrollPane = new JScrollPane(productosTable);
        productosPanel.add(productosScrollPane, BorderLayout.CENTER);

        JPanel productosBotonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        crearProductoBtn = new JButton("Crear Producto");
        editarProductoBtn = new JButton("Editar Producto");
        eliminarProductoBtn = new JButton("Eliminar Producto");
        productosBotonesPanel.add(crearProductoBtn);
        productosBotonesPanel.add(editarProductoBtn);
        productosBotonesPanel.add(eliminarProductoBtn);
        productosPanel.add(productosBotonesPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Productos", productosPanel);

        // ----------------------------------------
        // PESTAÑA DE PEDIDOS
        // ----------------------------------------
        JPanel pedidosPanel = new JPanel(new BorderLayout(10, 10));

        pedidosTableModel = new DefaultTableModel(new Object[]{"ID Pedido", "Fecha", "Ganadero", "Estado", "Total"}, 0);
        pedidosTable = new JTable(pedidosTableModel);
        JScrollPane pedidosScrollPane = new JScrollPane(pedidosTable);
        pedidosPanel.add(pedidosScrollPane, BorderLayout.CENTER);

        JPanel pedidosBotonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        verDetallesPedidoBtn = new JButton("Ver Detalles");
        actualizarEstadoPedidoBtn = new JButton("Actualizar Estado");
        asignarTransportistaBtn = new JButton("Asignar Transportista");
        pedidosBotonesPanel.add(verDetallesPedidoBtn);
        pedidosBotonesPanel.add(actualizarEstadoPedidoBtn);
        pedidosBotonesPanel.add(asignarTransportistaBtn);
        pedidosPanel.add(pedidosBotonesPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Pedidos", pedidosPanel);

        // ----------------------------------------
        // OTRAS PESTAÑAS (se mantendrán simples por ahora)
        // ----------------------------------------
        JPanel historialVentasPanel = new JPanel();
        historialVentasPanel.add(new JLabel("Historial de Ventas - Contenido por venir"));

        tabbedPane.addTab("Historial de Ventas", historialVentasPanel);

        // Agregamos el JTabbedPane al centro de la ventana principal
        add(tabbedPane, BorderLayout.CENTER);

        // Asignamos los listeners a los componentes
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

        // Listener para cargar datos cuando se cambie de pestaña
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                String title = sourceTabbedPane.getTitleAt(index);

                if (title.equals("Usuarios")) {
                    controlador.cargarUsuarios();
                } else if (title.equals("Productos")) {
                    controlador.cargarProductos();
                } else if (title.equals("Pedidos")) {
                    controlador.cargarPedidos();
                }
            }
        });

        // Al mostrar la ventana, cargamos los usuarios
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent windowEvent) {
                controlador.cargarUsuarios();
            }
        });
    }

    // Getters para que el controlador pueda acceder a los componentes
    public DefaultTableModel getUsuariosTableModel() {
        return usuariosTableModel;
    }

    public int getSelectedUserId() {
        int selectedRow = usuariosTable.getSelectedRow();
        if (selectedRow != -1) {
            return (int) usuariosTableModel.getValueAt(selectedRow, 0);
        }
        return -1;
    }

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