package Controladores;

import Modelos.Usuario;
import Modelos.Producto;
import Modelos.Pedido;
import Modelos.DetallePedidoProducto;
import Servicios.ConexionBD;
import Vistas.Admin;
import Vistas.Login;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

/**
 * Controlador para la vista de administrador. Gestiona toda la lógica de negocio
 * para las operaciones de administración, interactuando con la base de datos y
 * actualizando la vista {@link Admin}.
 */
public class AdminControlador {
    private final Admin vista;
    private final ConexionBD conexion;
    private final NumberFormat currencyFormat;

    /**
     * Constructor para AdminControlador.
     * @param vista La instancia de la vista {@link Admin} que este controlador gestiona.
     */
    public AdminControlador(Admin vista) {
        this.vista = vista;
        this.conexion = new ConexionBD();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "EC"));
    }

    //---------------------------------------------------------
    // MÉTODOS PARA GESTIÓN DE USUARIOS
    //---------------------------------------------------------

    /**
     * Carga o recarga la lista de usuarios desde la base de datos y la muestra en la tabla correspondiente.
     */
    public void cargarUsuarios() {
        DefaultTableModel model = vista.getUsuariosTableModel();
        model.setRowCount(0);
        String query = "SELECT id, nombre, correo, rol, telefono, direccion FROM usuarios";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("nombre"));
                row.add(rs.getString("correo"));
                row.add(rs.getString("rol"));
                row.add(rs.getString("telefono"));
                row.add(rs.getString("direccion"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar los usuarios: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Muestra un diálogo para que el administrador pueda crear un nuevo usuario.
     */
    public void mostrarDialogoCrearUsuario() {
        JTextField nombreField = new JTextField();
        JTextField correoField = new JTextField();
        JPasswordField contrasenaField = new JPasswordField();
        JComboBox<String> rolComboBox = new JComboBox<>(new String[]{"Administrador", "Transportista", "Inventariado", "Ganadero"});
        JTextField telefonoField = new JTextField();
        JTextField direccionField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Correo:"));
        panel.add(correoField);
        panel.add(new JLabel("Contraseña:"));
        panel.add(contrasenaField);
        panel.add(new JLabel("Rol:"));
        panel.add(rolComboBox);
        panel.add(new JLabel("Teléfono:"));
        panel.add(telefonoField);
        panel.add(new JLabel("Dirección:"));
        panel.add(direccionField);

        int result = JOptionPane.showConfirmDialog(vista, panel, "Crear Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            crearUsuario(
                    nombreField.getText(),
                    correoField.getText(),
                    new String(contrasenaField.getPassword()),
                    (String) rolComboBox.getSelectedItem(),
                    telefonoField.getText(),
                    direccionField.getText()
            );
        }
    }

    /**
     * Procesa la creación de un nuevo usuario en la base de datos.
     * @param nombre El nombre del usuario.
     * @param correo El correo electrónico del usuario.
     * @param contrasena La contraseña del usuario.
     * @param rol El rol del usuario.
     * @param telefono El teléfono del usuario.
     * @param direccion La dirección del usuario.
     */
    public void crearUsuario(String nombre, String correo, String contrasena, String rol, String telefono, String direccion) {
        String query = "INSERT INTO usuarios (nombre, correo, contraseña, rol, telefono, direccion) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, correo);
            pstmt.setString(3, contrasena);
            pstmt.setString(4, rol);
            pstmt.setString(5, telefono);
            pstmt.setString(6, direccion);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(vista, "Usuario creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al crear el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Muestra un diálogo para editar los datos de un usuario existente.
     */
    public void mostrarDialogoEditarUsuario() {
        int userId = vista.getSelectedUserId();
        if (userId == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un usuario para editar.", "Ningún Usuario Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JTextField nombreField = new JTextField(rs.getString("nombre"));
                JTextField correoField = new JTextField(rs.getString("correo"));
                JPasswordField contrasenaField = new JPasswordField();
                JComboBox<String> rolComboBox = new JComboBox<>(new String[]{"Administrador", "Transportista", "Inventariado", "Ganadero"});
                rolComboBox.setSelectedItem(rs.getString("rol"));
                JTextField telefonoField = new JTextField(rs.getString("telefono"));
                JTextField direccionField = new JTextField(rs.getString("direccion"));

                JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
                panel.add(new JLabel("Nombre:"));
                panel.add(nombreField);
                panel.add(new JLabel("Correo:"));
                panel.add(correoField);
                panel.add(new JLabel("Nueva Contraseña (dejar en blanco para no cambiar):"));
                panel.add(contrasenaField);
                panel.add(new JLabel("Rol:"));
                panel.add(rolComboBox);
                panel.add(new JLabel("Teléfono:"));
                panel.add(telefonoField);
                panel.add(new JLabel("Dirección:"));
                panel.add(direccionField);

                int result = JOptionPane.showConfirmDialog(vista, panel, "Editar Usuario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    editarUsuario(userId, nombreField.getText(), correoField.getText(), new String(contrasenaField.getPassword()), (String) rolComboBox.getSelectedItem(), telefonoField.getText(), direccionField.getText());
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al obtener datos del usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Procesa la actualización de un usuario en la base de datos.
     * @param id El ID del usuario a editar.
     * @param nombre El nuevo nombre del usuario.
     * @param correo El nuevo correo electrónico del usuario.
     * @param contrasena La nueva contraseña (si se ingresó una).
     * @param rol El nuevo rol del usuario.
     * @param telefono El nuevo teléfono del usuario.
     * @param direccion La nueva dirección del usuario.
     */
    private void editarUsuario(int id, String nombre, String correo, String contrasena, String rol, String telefono, String direccion) {
        StringBuilder query = new StringBuilder("UPDATE usuarios SET nombre = ?, correo = ?, rol = ?, telefono = ?, direccion = ?");
        if (!contrasena.isEmpty()) {
            query.append(", contraseña = ?");
        }
        query.append(" WHERE id = ?");

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, correo);
            pstmt.setString(3, rol);
            pstmt.setString(4, telefono);
            pstmt.setString(5, direccion);
            if (!contrasena.isEmpty()) {
                pstmt.setString(6, contrasena);
                pstmt.setInt(7, id);
            } else {
                pstmt.setInt(6, id);
            }
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(vista, "Usuario actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al actualizar el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Elimina un usuario seleccionado de la base de datos, pidiendo confirmación previa.
     */
    public void eliminarUsuario() {
        int userId = vista.getSelectedUserId();
        if (userId == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un usuario para eliminar.", "Ningún Usuario Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista, "¿Estás seguro de que quieres eliminar este usuario?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM usuarios WHERE id = ?";
            try (Connection conn = conexion.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(vista, "Usuario eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarUsuarios();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(vista, "Error al eliminar el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    //---------------------------------------------------------
    // MÉTODOS PARA GESTIÓN DE PRODUCTOS
    //---------------------------------------------------------
    /**
     * Carga los productos desde la base de datos y los muestra en la tabla de productos.
     */
    public void cargarProductos() {
        DefaultTableModel model = vista.getProductosTableModel();
        model.setRowCount(0);
        String query = "SELECT id, nombre, tipo, especie, precio_unitario, stock FROM productos";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("nombre"));
                row.add(rs.getString("tipo"));
                row.add(rs.getString("especie"));
                row.add(rs.getDouble("precio_unitario"));
                row.add(rs.getInt("stock"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar los productos: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Muestra un diálogo para crear un nuevo producto.
     */
    public void mostrarDialogoCrearProducto() {
        JTextField nombreField = new JTextField();
        JTextField tipoField = new JTextField();
        JTextField especieField = new JTextField();
        JTextArea descripcionArea = new JTextArea(3, 20);
        JTextField precioField = new JTextField();
        JTextField presentacionField = new JTextField();
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Tipo:"));
        panel.add(tipoField);
        panel.add(new JLabel("Especie:"));
        panel.add(especieField);
        panel.add(new JLabel("Descripción:"));
        panel.add(new JScrollPane(descripcionArea));
        panel.add(new JLabel("Precio Unitario:"));
        panel.add(precioField);
        panel.add(new JLabel("Presentación:"));
        panel.add(presentacionField);
        panel.add(new JLabel("Stock:"));
        panel.add(stockSpinner);

        int result = JOptionPane.showConfirmDialog(vista, panel, "Crear Nuevo Producto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                crearProducto(
                        nombreField.getText(),
                        tipoField.getText(),
                        especieField.getText(),
                        descripcionArea.getText(),
                        Double.parseDouble(precioField.getText()),
                        presentacionField.getText(),
                        (Integer) stockSpinner.getValue()
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vista, "El precio y el stock deben ser números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Crea un nuevo producto en la base de datos.
     * @param nombre El nombre del producto.
     * @param tipo El tipo de producto.
     * @param especie La especie del producto.
     * @param descripcion La descripción del producto.
     * @param precioUnitario El precio unitario del producto.
     * @param presentacion La presentación del producto.
     * @param stock El stock del producto.
     */
    public void crearProducto(String nombre, String tipo, String especie, String descripcion, double precioUnitario, String presentacion, int stock) {
        String query = "INSERT INTO productos (nombre, tipo, especie, descripcion, precio_unitario, presentacion, stock) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, tipo);
            pstmt.setString(3, especie);
            pstmt.setString(4, descripcion);
            pstmt.setDouble(5, precioUnitario);
            pstmt.setString(6, presentacion);
            pstmt.setInt(7, stock);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(vista, "Producto creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al crear el producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Muestra un diálogo para editar un producto existente.
     */
    public void mostrarDialogoEditarProducto() {
        int productId = vista.getSelectedProductId();
        if (productId == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un producto para editar.", "Ningún Producto Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "SELECT * FROM productos WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JTextField nombreField = new JTextField(rs.getString("nombre"));
                JTextField tipoField = new JTextField(rs.getString("tipo"));
                JTextField especieField = new JTextField(rs.getString("especie"));
                JTextArea descripcionArea = new JTextArea(rs.getString("descripcion"), 3, 20);
                JTextField precioField = new JTextField(String.valueOf(rs.getDouble("precio_unitario")));
                JTextField presentacionField = new JTextField(rs.getString("presentacion"));
                JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(rs.getInt("stock"), 0, 9999, 1));

                JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
                panel.add(new JLabel("Nombre:"));
                panel.add(nombreField);
                panel.add(new JLabel("Tipo:"));
                panel.add(tipoField);
                panel.add(new JLabel("Especie:"));
                panel.add(especieField);
                panel.add(new JLabel("Descripción:"));
                panel.add(new JScrollPane(descripcionArea));
                panel.add(new JLabel("Precio Unitario:"));
                panel.add(precioField);
                panel.add(new JLabel("Presentación:"));
                panel.add(presentacionField);
                panel.add(new JLabel("Stock:"));
                panel.add(stockSpinner);

                int result = JOptionPane.showConfirmDialog(vista, panel, "Editar Producto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    try {
                        editarProducto(productId, nombreField.getText(), tipoField.getText(), especieField.getText(), descripcionArea.getText(), Double.parseDouble(precioField.getText()), presentacionField.getText(), (Integer) stockSpinner.getValue());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(vista, "El precio y el stock deben ser números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al obtener datos del producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Edita un producto existente en la base de datos.
     * @param id El ID del producto a editar.
     * @param nombre El nuevo nombre del producto.
     * @param tipo El nuevo tipo del producto.
     * @param especie La nueva especie del producto.
     * @param descripcion La nueva descripción del producto.
     * @param precioUnitario El nuevo precio unitario del producto.
     * @param presentacion La nueva presentación del producto.
     * @param stock El nuevo stock del producto.
     */
    private void editarProducto(int id, String nombre, String tipo, String especie, String descripcion, double precioUnitario, String presentacion, int stock) {
        String query = "UPDATE productos SET nombre = ?, tipo = ?, especie = ?, descripcion = ?, precio_unitario = ?, presentacion = ?, stock = ? WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, tipo);
            pstmt.setString(3, especie);
            pstmt.setString(4, descripcion);
            pstmt.setDouble(5, precioUnitario);
            pstmt.setString(6, presentacion);
            pstmt.setInt(7, stock);
            pstmt.setInt(8, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(vista, "Producto actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProductos();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al actualizar el producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Elimina un producto de la base de datos.
     */
    public void eliminarProducto() {
        int productId = vista.getSelectedProductId();
        if (productId == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un producto para eliminar.", "Ningún Producto Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista, "¿Estás seguro de que quieres eliminar este producto?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM productos WHERE id = ?";
            try (Connection conn = conexion.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, productId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(vista, "Producto eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarProductos();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(vista, "Error al eliminar el producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    //---------------------------------------------------------
    // MÉTODOS PARA GESTIÓN DE PEDIDOS
    //---------------------------------------------------------

    /**
     * Carga los pedidos desde la base de datos y los muestra en la tabla de pedidos.
     */
    public void cargarPedidos() {
        DefaultTableModel model = vista.getPedidosTableModel();
        model.setRowCount(0);

        String query = "SELECT p.id, p.fecha, u.nombre AS ganadero_nombre, p.estado, p.total " +
                "FROM pedidos p JOIN usuarios u ON p.id_usuario = u.id ORDER BY p.id DESC";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getTimestamp("fecha"));
                row.add(rs.getString("ganadero_nombre"));
                row.add(rs.getString("estado"));
                row.add(currencyFormat.format(rs.getDouble("total")));
                model.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar los pedidos: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Muestra los detalles de un pedido seleccionado.
     */
    public void mostrarDetallesPedido() {
        int pedidoId = vista.getSelectedPedidoId();
        if (pedidoId == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un pedido de la tabla para ver los detalles.", "Ningún Pedido Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = conexion.getConnection()) {
            Pedido pedido = obtenerPedidoPorId(conn, pedidoId);
            if (pedido == null) {
                JOptionPane.showMessageDialog(vista, "No se encontró el pedido con el ID: " + pedidoId, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            List<DetallePedidoProducto> productos = obtenerDetallesProductosPorPedido(conn, pedidoId);
            String transportistaNombre = obtenerTransportistaPorPedido(conn, pedidoId);
            String estadoEnvio = obtenerEstadoEnvioPorPedido(conn, pedidoId);

            JDialog detallesDialog = new JDialog(vista, "Detalles del Pedido #" + pedidoId, true);
            detallesDialog.setSize(700, 500);
            detallesDialog.setLocationRelativeTo(vista);
            detallesDialog.setLayout(new BorderLayout(10, 10));

            JPanel infoPanel = new JPanel(new GridLayout(5, 2, 5, 5));
            infoPanel.setBorder(BorderFactory.createTitledBorder("Información General"));
            infoPanel.add(new JLabel("ID del Pedido:"));
            infoPanel.add(new JLabel(String.valueOf(pedido.getId())));
            infoPanel.add(new JLabel("Fecha:"));
            infoPanel.add(new JLabel(pedido.getFecha().toString()));
            infoPanel.add(new JLabel("Ganadero:"));
            infoPanel.add(new JLabel(pedido.getGanaderoNombre()));
            infoPanel.add(new JLabel("Estado:"));
            infoPanel.add(new JLabel(pedido.getEstado()));
            infoPanel.add(new JLabel("Total:"));
            infoPanel.add(new JLabel(currencyFormat.format(pedido.getTotal())));

            String[] columnNames = {"Producto", "Cantidad", "Precio Unitario", "Subtotal"};
            DefaultTableModel productosTableModel = new DefaultTableModel(null, columnNames);
            for (DetallePedidoProducto p : productos) {
                productosTableModel.addRow(new Object[]{
                        p.getProductoNombre(),
                        p.getCantidad(),
                        currencyFormat.format(p.getPrecioUnitario()),
                        currencyFormat.format(p.getSubtotal())
                });
            }
            JTable productosTable = new JTable(productosTableModel);
            JScrollPane productosScrollPane = new JScrollPane(productosTable);
            productosScrollPane.setBorder(BorderFactory.createTitledBorder("Productos del Pedido"));

            JPanel envioPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            envioPanel.setBorder(BorderFactory.createTitledBorder("Información de Envío"));
            envioPanel.add(new JLabel("Transportista:"));
            envioPanel.add(new JLabel(transportistaNombre != null ? transportistaNombre : "No asignado"));
            envioPanel.add(new JLabel("Estado de Envío:"));
            envioPanel.add(new JLabel(estadoEnvio != null ? estadoEnvio : "N/A"));

            JButton cerrarBtn = new JButton("Cerrar");
            cerrarBtn.addActionListener(e -> detallesDialog.dispose());
            JPanel cerrarPanel = new JPanel();
            cerrarPanel.add(cerrarBtn);

            detallesDialog.add(infoPanel, BorderLayout.NORTH);
            detallesDialog.add(productosScrollPane, BorderLayout.CENTER);
            detallesDialog.add(envioPanel, BorderLayout.EAST);
            detallesDialog.add(cerrarPanel, BorderLayout.SOUTH);

            detallesDialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al obtener los detalles del pedido: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Obtiene un pedido por su ID.
     * @param conn La conexión a la base de datos.
     * @param pedidoId El ID del pedido.
     * @return El pedido encontrado, o null si no se encuentra.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    private Pedido obtenerPedidoPorId(Connection conn, int pedidoId) throws SQLException {
        String query = "SELECT p.id, p.fecha, p.estado, p.total, u.nombre AS ganadero_nombre " +
                "FROM pedidos p JOIN usuarios u ON p.id_usuario = u.id WHERE p.id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, pedidoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Pedido(rs.getInt("id"), rs.getTimestamp("fecha"), rs.getString("estado"), rs.getDouble("total"), rs.getString("ganadero_nombre"));
            }
        }
        return null;
    }

    /**
     * Obtiene los detalles de los productos de un pedido.
     * @param conn La conexión a la base de datos.
     * @param pedidoId El ID del pedido.
     * @return Una lista de los detalles de los productos.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    private List<DetallePedidoProducto> obtenerDetallesProductosPorPedido(Connection conn, int pedidoId) throws SQLException {
        List<DetallePedidoProducto> productos = new ArrayList<>();
        String query = "SELECT dp.cantidad, dp.precio_unitario, p.nombre " +
                "FROM detalle_pedido dp JOIN productos p ON dp.id_producto = p.id WHERE dp.id_pedido = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, pedidoId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int cantidad = rs.getInt("cantidad");
                double precioUnitario = rs.getDouble("precio_unitario");
                productos.add(new DetallePedidoProducto(
                        rs.getString("nombre"),
                        cantidad,
                        precioUnitario,
                        cantidad * precioUnitario
                ));
            }
        }
        return productos;
    }

    /**
     * Obtiene el transportista de un pedido.
     * @param conn La conexión a la base de datos.
     * @param pedidoId El ID del pedido.
     * @return El nombre del transportista, o null si no se encuentra.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    private String obtenerTransportistaPorPedido(Connection conn, int pedidoId) throws SQLException {
        String query = "SELECT u.nombre FROM envios e JOIN usuarios u ON e.id_transportista = u.id WHERE e.id_pedido = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, pedidoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
        }
        return null;
    }

    /**
     * Obtiene el estado del envío de un pedido.
     * @param conn La conexión a la base de datos.
     * @param pedidoId El ID del pedido.
     * @return El estado del envío, o null si no se encuentra.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    private String obtenerEstadoEnvioPorPedido(Connection conn, int pedidoId) throws SQLException {
        String query = "SELECT estado_envio FROM envios WHERE id_pedido = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, pedidoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("estado_envio");
            }
        }
        return null;
    }

    /**
     * Actualiza el estado de un pedido.
     */
    public void actualizarEstadoPedido() {
        int pedidoId = vista.getSelectedPedidoId();
        if (pedidoId == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un pedido de la tabla para actualizar su estado.", "Ningún Pedido Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] estados = {"Pendiente", "Procesado", "Enviado", "Entregado", "Cancelado"};
        String estadoActual = "";

        String queryEstado = "SELECT estado FROM pedidos WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmtEstado = conn.prepareStatement(queryEstado)) {

            pstmtEstado.setInt(1, pedidoId);
            ResultSet rs = pstmtEstado.executeQuery();
            if (rs.next()) {
                estadoActual = rs.getString("estado");
            } else {
                JOptionPane.showMessageDialog(vista, "Error al obtener el estado actual del pedido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error de base de datos al obtener el estado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        JComboBox<String> estadoComboBox = new JComboBox<>(estados);
        estadoComboBox.setSelectedItem(estadoActual);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Selecciona el nuevo estado:"));
        panel.add(estadoComboBox);

        int result = JOptionPane.showConfirmDialog(vista, panel, "Actualizar Estado del Pedido #" + pedidoId, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String nuevoEstado = (String) estadoComboBox.getSelectedItem();
            if (nuevoEstado.equals(estadoActual)) {
                JOptionPane.showMessageDialog(vista, "El estado no ha cambiado.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            actualizarEstadoEnBD(pedidoId, nuevoEstado);
        }
    }

    /**
     * Actualiza el estado de un pedido en la base de datos.
     * @param pedidoId El ID del pedido a actualizar.
     * @param nuevoEstado El nuevo estado del pedido.
     */
    public void actualizarEstadoEnBD(int pedidoId, String nuevoEstado) {
        String query = "UPDATE pedidos SET estado = ? WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, pedidoId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(vista, "Estado del pedido actualizado a '" + nuevoEstado + "'.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarPedidos();
            } else {
                JOptionPane.showMessageDialog(vista, "No se pudo actualizar el estado del pedido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error de base de datos al actualizar el estado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Asigna un transportista a un pedido.
     */
    public void asignarTransportista() {
        int pedidoId = vista.getSelectedPedidoId();
        if (pedidoId == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un pedido de la tabla para asignar un transportista.", "Ningún Pedido Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, Integer> transportistas = obtenerTransportistas();
        if (transportistas.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No hay transportistas registrados para asignar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<String> transportistasComboBox = new JComboBox<>(transportistas.keySet().toArray(new String[0]));

        JPanel panel = new JPanel();
        panel.add(new JLabel("Selecciona el transportista a asignar:"));
        panel.add(transportistasComboBox);

        int result = JOptionPane.showConfirmDialog(vista, panel, "Asignar Transportista al Pedido #" + pedidoId, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String transportistaSeleccionado = (String) transportistasComboBox.getSelectedItem();
            int transportistaId = transportistas.get(transportistaSeleccionado);

            asignarTransportistaYActualizarEstado(pedidoId, transportistaId);
        }
    }

    /**
     * Obtiene una lista de transportistas.
     * @return Un mapa de transportistas, con el nombre como clave y el ID como valor.
     */
    private Map<String, Integer> obtenerTransportistas() {
        Map<String, Integer> transportistas = new HashMap<>();
        String query = "SELECT id, nombre FROM usuarios WHERE rol = 'Transportista'";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                transportistas.put(rs.getString("nombre"), rs.getInt("id"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al obtener la lista de transportistas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return transportistas;
    }

    /**
     * Asigna un transportista a un pedido y actualiza el estado del pedido.
     * @param pedidoId El ID del pedido.
     * @param transportistaId El ID del transportista.
     */
    private void asignarTransportistaYActualizarEstado(int pedidoId, int transportistaId) {
        String insertQuery = "INSERT INTO envios (id_pedido, id_transportista, fecha_envio, estado_envio) VALUES (?, ?, ?, 'Preparando')";
        String updateQuery = "UPDATE pedidos SET estado = 'Enviado' WHERE id = ?";

        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false); // Iniciar transacción

            try (PreparedStatement pstmtInsert = conn.prepareStatement(insertQuery)) {
                pstmtInsert.setInt(1, pedidoId);
                pstmtInsert.setInt(2, transportistaId);
                pstmtInsert.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                pstmtInsert.executeUpdate();
            }

            try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateQuery)) {
                pstmtUpdate.setInt(1, pedidoId);
                pstmtUpdate.executeUpdate();
            }

            conn.commit(); // Confirmar transacción
            JOptionPane.showMessageDialog(vista, "Transportista asignado y estado del pedido actualizado a 'Enviado'.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarPedidos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al asignar el transportista: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    // --- SECCIÓN DE HISTORIAL DE VENTAS ---

    /**
     * Carga el historial de ventas.
     */
    public void cargarHistorialVentas() {
        try (Connection conn = conexion.getConnection()) {
            // Cargar Métricas Generales
            cargarMetricas(conn);
            // Cargar Top 5 Productos
            cargarTopProductos(conn);
            // Cargar Últimos 5 Pedidos
            cargarUltimosPedidos(conn);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar el historial de ventas: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Carga las métricas de ventas.
     * @param conn La conexión a la base de datos.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    private void cargarMetricas(Connection conn) throws SQLException {
        String query = "SELECT COUNT(id) as num_pedidos, SUM(total) as total_ventas FROM pedidos WHERE estado = 'Entregado'";
        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                int numPedidos = rs.getInt("num_pedidos");
                double totalVentas = rs.getDouble("total_ventas");
                vista.getPedidosCompletadosLabel().setText("Pedidos Completados: " + numPedidos);
                vista.getTotalVentasLabel().setText("Total de Ventas: " + currencyFormat.format(totalVentas));
            }
        }
    }

    /**
     * Carga los productos más vendidos.
     * @param conn La conexión a la base de datos.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    private void cargarTopProductos(Connection conn) throws SQLException {
        DefaultTableModel model = vista.getTopProductosTableModel();
        model.setRowCount(0);
        String query = "SELECT pr.nombre, SUM(dp.cantidad) as cantidad_total " +
                "FROM detalle_pedido dp " +
                "JOIN productos pr ON dp.id_producto = pr.id " +
                "JOIN pedidos p ON dp.id_pedido = p.id " +
                "WHERE p.estado = 'Entregado' " +
                "GROUP BY pr.nombre " +
                "ORDER BY cantidad_total DESC " +
                "LIMIT 5";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("nombre"));
                row.add(rs.getInt("cantidad_total"));
                model.addRow(row);
            }
        }
    }

    /**
     * Carga los últimos pedidos.
     * @param conn La conexión a la base de datos.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    private void cargarUltimosPedidos(Connection conn) throws SQLException {
        DefaultTableModel model = vista.getUltimosPedidosTableModel();
        model.setRowCount(0);
        String query = "SELECT p.id, p.fecha, u.nombre as cliente, p.total " +
                "FROM pedidos p " +
                "JOIN usuarios u ON p.id_usuario = u.id " +
                "WHERE p.estado = 'Entregado' " +
                "ORDER BY p.fecha DESC " +
                "LIMIT 5";

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getTimestamp("fecha"));
                row.add(rs.getString("cliente"));
                row.add(currencyFormat.format(rs.getDouble("total")));
                model.addRow(row);
            }
        }
    }

    // --- OTROS MÉTODOS ---

    /**
     * Cierra la sesión actual y abre la ventana de inicio de sesión.
     */
    public void cerrarSesion() {
        vista.dispose();
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}