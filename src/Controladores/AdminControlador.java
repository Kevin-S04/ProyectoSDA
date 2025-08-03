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
 * Controlador principal para la ventana de Administración.
 * Maneja la lógica de negocio y las interacciones con la base de datos
 * para la gestión de usuarios, productos, pedidos, etc.
 */
public class AdminControlador {
    private final Admin vista;
    private final ConexionBD conexion;
    private final NumberFormat currencyFormat;

    public AdminControlador(Admin vista) {
        this.vista = vista;
        this.conexion = new ConexionBD();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "EC"));
    }

    //---------------------------------------------------------
    // MÉTODOS PARA GESTIÓN DE USUARIOS (El código anterior sigue siendo válido)
    //---------------------------------------------------------
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
    public void mostrarDialogoCrearUsuario() { /* ... */ }
    private void crearUsuario(String nombre, String correo, String contrasena, String rol, String telefono, String direccion) { /* ... */ }
    public void mostrarDialogoEditarUsuario() { /* ... */ }
    private void editarUsuario(int id, String nombre, String correo, String contrasena, String rol, String telefono, String direccion) { /* ... */ }
    public void eliminarUsuario() { /* ... */ }

    //---------------------------------------------------------
    // MÉTODOS PARA GESTIÓN DE PRODUCTOS (El código anterior sigue siendo válido)
    //---------------------------------------------------------
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
    public void mostrarDialogoCrearProducto() { /* ... */ }
    private void crearProducto(String nombre, String tipo, String especie, String descripcion, double precioUnitario, String presentacion, int stock) { /* ... */ }
    public void mostrarDialogoEditarProducto() { /* ... */ }
    private void editarProducto(int id, String nombre, String tipo, String especie, String descripcion, double precioUnitario, String presentacion, int stock) { /* ... */ }
    public void eliminarProducto() { /* ... */ }

    //---------------------------------------------------------
    // MÉTODOS PARA GESTIÓN DE PEDIDOS
    //---------------------------------------------------------

    /**
     * Carga todos los pedidos de la base de datos y los muestra en la tabla de la vista.
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
     * Muestra un cuadro de diálogo con los detalles completos del pedido seleccionado.
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

    private String obtenerEstadoEnvioPorPedido(Connection conn, int pedidoId) throws SQLException {
        String query = "SELECT estado FROM envios WHERE id_pedido = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, pedidoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("estado");
            }
        }
        return null;
    }

    /**
     * Muestra un diálogo para que el administrador actualice el estado de un pedido.
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
     * Actualiza el estado del pedido en la base de datos.
     */
    private void actualizarEstadoEnBD(int pedidoId, String nuevoEstado) {
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
     * Muestra un diálogo para que el administrador asigne un transportista a un pedido.
     */
    public void asignarTransportista() {
        int pedidoId = vista.getSelectedPedidoId();
        if (pedidoId == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un pedido de la tabla para asignar un transportista.", "Ningún Pedido Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener la lista de transportistas
        Map<String, Integer> transportistas = obtenerTransportistas();
        if (transportistas.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No hay transportistas registrados para asignar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Crear el JComboBox con los nombres de los transportistas
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
     * Obtiene una lista de usuarios con el rol 'Transportista'.
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
     */
    private void asignarTransportistaYActualizarEstado(int pedidoId, int transportistaId) {
        String insertQuery = "INSERT INTO envios (id_pedido, id_transportista, fecha_asignacion, estado) VALUES (?, ?, ?, 'Pendiente')";
        String updateQuery = "UPDATE pedidos SET estado = 'Enviado' WHERE id = ?";

        try (Connection conn = conexion.getConnection()) {
            // Inserción en la tabla de envíos
            try (PreparedStatement pstmtInsert = conn.prepareStatement(insertQuery)) {
                pstmtInsert.setInt(1, pedidoId);
                pstmtInsert.setInt(2, transportistaId);
                pstmtInsert.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                pstmtInsert.executeUpdate();
            }

            // Actualización del estado del pedido
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateQuery)) {
                pstmtUpdate.setInt(1, pedidoId);
                pstmtUpdate.executeUpdate();
            }

            JOptionPane.showMessageDialog(vista, "Transportista asignado y estado del pedido actualizado a 'Enviado'.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarPedidos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al asignar el transportista: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    //---------------------------------------------------------
    // OTROS MÉTODOS
    //---------------------------------------------------------

    /**
     * Cierra la ventana actual de Administrador y abre la ventana de Login.
     */
    public void cerrarSesion() {
        vista.dispose();
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}