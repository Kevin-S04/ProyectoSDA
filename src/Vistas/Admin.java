package Vistas;

import Controladores.AdminControlador;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Ventana principal para el rol de Administrador.
 * Contiene un JTabbedPane para gestionar usuarios, productos, pedidos y revisar el historial de ventas.
 */
public class Admin extends JFrame {

    private final AdminControlador controlador;
    private JTable usuariosTable;
    private DefaultTableModel usuariosTableModel;
    private JButton crearUsuarioBtn;
    private JButton editarUsuarioBtn;
    private JButton eliminarUsuarioBtn;
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

        // Tabla para mostrar los usuarios
        usuariosTableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Correo", "Rol", "Teléfono", "Dirección"}, 0);
        usuariosTable = new JTable(usuariosTableModel);
        JScrollPane scrollPane = new JScrollPane(usuariosTable);
        usuariosPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel para los botones
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        crearUsuarioBtn = new JButton("Crear Usuario");
        editarUsuarioBtn = new JButton("Editar Usuario");
        eliminarUsuarioBtn = new JButton("Eliminar Usuario");
        botonesPanel.add(crearUsuarioBtn);
        botonesPanel.add(editarUsuarioBtn);
        botonesPanel.add(eliminarUsuarioBtn);
        usuariosPanel.add(botonesPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Usuarios", usuariosPanel);

        // ----------------------------------------
        // OTRAS PESTAÑAS (se mantendrán simples por ahora)
        // ----------------------------------------
        JPanel productosPanel = new JPanel();
        productosPanel.add(new JLabel("Gestión de Productos - Contenido por venir"));

        JPanel pedidosPanel = new JPanel();
        pedidosPanel.add(new JLabel("Gestión de Pedidos - Contenido por venir"));

        JPanel historialVentasPanel = new JPanel();
        historialVentasPanel.add(new JLabel("Historial de Ventas - Contenido por venir"));

        tabbedPane.addTab("Productos", productosPanel);
        tabbedPane.addTab("Pedidos", pedidosPanel);
        tabbedPane.addTab("Historial de Ventas", historialVentasPanel);

        // Agregamos el JTabbedPane al centro de la ventana principal
        add(tabbedPane, BorderLayout.CENTER);

        // Asignamos los listeners a los componentes
        crearUsuarioBtn.addActionListener(e -> controlador.mostrarDialogoCrearUsuario());
        editarUsuarioBtn.addActionListener(e -> controlador.mostrarDialogoEditarUsuario());
        eliminarUsuarioBtn.addActionListener(e -> controlador.eliminarUsuario());
        salirBtn.addActionListener(e -> controlador.cerrarSesion());

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

    /**
     * Obtiene el ID del usuario seleccionado en la tabla.
     * @return El ID del usuario o -1 si no hay ninguna fila seleccionada.
     */
    public int getSelectedUserId() {
        int selectedRow = usuariosTable.getSelectedRow();
        if (selectedRow != -1) {
            // El ID está en la primera columna (índice 0)
            return (int) usuariosTableModel.getValueAt(selectedRow, 0);
        }
        return -1;
    }
}