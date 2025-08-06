package Controladores;

import Modelos.Pedido;
import Modelos.DetallePedidoProducto;
import Servicios.ConexionBD;
import Vistas.Inventariado;
import Vistas.Login;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

/**
 * Controlador para la vista de Inventariado. Gestiona la lógica de negocio
 * para la actualización de stock de productos y la consulta de pedidos pendientes.
 */
public class InventariadoControlador {

    private final Inventariado vista;
    private final ConexionBD conexion;
    private final NumberFormat currencyFormat;

    /**
     * Constructor para InventariadoControlador.
     * @param vista La instancia de la vista Inventariado que este controlador maneja.
     */
    public InventariadoControlador(Inventariado vista) {
        this.vista = vista;
        this.conexion = new ConexionBD();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "EC"));
    }

    /**
     * Carga todos los productos de la base de datos y los muestra en la tabla de stock.
     */
    public void cargarProductos() {
        DefaultTableModel model = vista.getProductosTableModel();
        model.setRowCount(0);
        String query = "SELECT id, nombre, tipo, especie, stock FROM productos ORDER BY nombre ASC";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("nombre"));
                row.add(rs.getString("tipo"));
                row.add(rs.getString("especie"));
                row.add(rs.getInt("stock"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            vista.showStatus("Error al cargar los productos: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    /**
     * Guarda todos los cambios de stock realizados en la tabla editable.
     * <p>
     * Este método itera sobre las filas de la tabla, detecta los nuevos valores de stock
     * y los actualiza en la base de datos utilizando una transacción (batch update) para
     * mayor eficiencia.
     */
    public void guardarCambiosDeStock() {
        DefaultTableModel model = vista.getProductosTableModel();
        int rowCount = model.getRowCount();
        Map<Integer, Integer> cambios = new HashMap<>();

        for (int i = 0; i < rowCount; i++) {
            int id = (Integer) model.getValueAt(i, 0);
            int nuevoStock = (Integer) model.getValueAt(i, 4);
            // Por simplicidad, se asume que cualquier fila podría haber sido editada.
            // Para una mayor optimización, se podría comparar con un modelo de datos original.
            cambios.put(id, nuevoStock);
        }

        if (cambios.isEmpty()) {
            vista.showStatus("No hay datos para guardar.", false);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Estás seguro de que quieres guardar " + cambios.size() + " cambios en el stock?",
                "Confirmar Guardado",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String query = "UPDATE productos SET stock = ? WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            conn.setAutoCommit(false); // Iniciar transacción para el batch update

            for (Map.Entry<Integer, Integer> entry : cambios.entrySet()) {
                pstmt.setInt(1, entry.getValue()); // nuevoStock
                pstmt.setInt(2, entry.getKey());   // id
                pstmt.addBatch();
            }

            int[] updateCounts = pstmt.executeBatch();
            conn.commit(); // Confirmar la transacción

            vista.showStatus(updateCounts.length + " registros de stock actualizados exitosamente.", false);
            cargarProductos(); // Recargar para confirmar visualmente los cambios

        } catch (SQLException e) {
            vista.showStatus("Error de base de datos al actualizar stock: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    /**
     * Carga los pedidos con estado 'Procesado' de la base de datos. Estos son los pedidos
     * que el personal de inventario necesita preparar para el envío.
     */
    public void cargarPedidosPendientes() {
        DefaultTableModel model = vista.getPedidosTableModel();
        model.setRowCount(0);
        String query = "SELECT p.id, p.fecha, u.nombre AS ganadero_nombre, p.estado " +
                "FROM pedidos p JOIN usuarios u ON p.id_usuario = u.id WHERE p.estado = 'Procesado'";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getTimestamp("fecha"));
                row.add(rs.getString("ganadero_nombre"));
                row.add(rs.getString("estado"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            vista.showStatus("Error al cargar pedidos pendientes: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    /**
     * Muestra un cuadro de diálogo con los detalles completos del pedido seleccionado,
     * incluyendo los productos y cantidades a preparar.
     */
    public void mostrarDetallesPedido() {
        int pedidoId = vista.getSelectedPedidoId();
        if (pedidoId == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un pedido para ver sus detalles.", "Ningún Pedido Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = conexion.getConnection()) {
            Pedido pedido = obtenerPedidoPorId(conn, pedidoId);
            if (pedido == null) {
                JOptionPane.showMessageDialog(vista, "No se encontró el pedido con el ID: " + pedidoId, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            List<DetallePedidoProducto> productos = obtenerDetallesProductosPorPedido(conn, pedidoId);

            JDialog detallesDialog = new JDialog(vista, "Detalles del Pedido #" + pedidoId, true);
            detallesDialog.setSize(600, 400);
            detallesDialog.setLocationRelativeTo(vista);
            detallesDialog.setLayout(new BorderLayout(10, 10));

            JPanel infoPanel = new JPanel(new GridLayout(4, 2, 5, 5));
            infoPanel.setBorder(BorderFactory.createTitledBorder("Información General del Pedido"));
            infoPanel.add(new JLabel("ID del Pedido:"));
            infoPanel.add(new JLabel(String.valueOf(pedido.getId())));
            infoPanel.add(new JLabel("Fecha:"));
            infoPanel.add(new JLabel(pedido.getFecha().toString()));
            infoPanel.add(new JLabel("Ganadero:"));
            infoPanel.add(new JLabel(pedido.getGanaderoNombre()));
            infoPanel.add(new JLabel("Estado:"));
            infoPanel.add(new JLabel(pedido.getEstado()));

            String[] columnNames = {"Producto", "Cantidad", "Precio Unitario"};
            DefaultTableModel productosTableModel = new DefaultTableModel(null, columnNames);
            for (DetallePedidoProducto p : productos) {
                productosTableModel.addRow(new Object[]{
                        p.getProductoNombre(),
                        p.getCantidad(),
                        currencyFormat.format(p.getPrecioUnitario())
                });
            }
            JTable productosTable = new JTable(productosTableModel);
            JScrollPane productosScrollPane = new JScrollPane(productosTable);
            productosScrollPane.setBorder(BorderFactory.createTitledBorder("Productos del Pedido"));

            JButton cerrarBtn = new JButton("Cerrar");
            cerrarBtn.addActionListener(e -> detallesDialog.dispose());
            JPanel cerrarPanel = new JPanel();
            cerrarPanel.add(cerrarBtn);

            detallesDialog.add(infoPanel, BorderLayout.NORTH);
            detallesDialog.add(productosScrollPane, BorderLayout.CENTER);
            detallesDialog.add(cerrarPanel, BorderLayout.SOUTH);

            detallesDialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al obtener los detalles del pedido: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // --- Métodos Auxiliares ---

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

    /**
     * Cierra la ventana actual de inventariado y abre la ventana de Login.
     */
    public void cerrarSesion() {
        vista.dispose();
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}