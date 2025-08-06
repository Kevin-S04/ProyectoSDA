package Controladores;

import Modelos.CarritoItem;
import Modelos.DetallePedidoProducto;
import Modelos.Pedido;
import Modelos.Producto;
import Servicios.ConexionBD;
import Vistas.Ganadero;
import Vistas.Login;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

/**
 * Controlador para la vista del Ganadero. Gestiona la lógica de negocio para el catálogo de productos,
 * el carrito de compras, la realización de pedidos y la generación de facturas, interactuando
 * con la moderna interfaz de {@link Ganadero}.
 */
public class GanaderoControlador {

    private final Ganadero vista;
    private final int idGanadero;
    private final ConexionBD conexion;
    private final List<CarritoItem> carrito;
    private final NumberFormat currencyFormat;

    /**
     * Constructor para GanaderoControlador.
     * @param vista La instancia de la vista {@link Ganadero} que este controlador maneja.
     * @param idGanadero El ID del ganadero que ha iniciado sesión.
     */
    public GanaderoControlador(Ganadero vista, int idGanadero) {
        this.vista = vista;
        this.idGanadero = idGanadero;
        this.conexion = new ConexionBD();
        this.carrito = new ArrayList<>();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "EC"));
    }

    // --- MÉTODOS PARA CATÁLOGO ---
    public void cargarProductos() {
        DefaultListModel<Producto> model = vista.getProductoListModel();
        model.clear();
        String query = "SELECT * FROM productos WHERE stock > 0 ORDER BY nombre ASC";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Producto producto = new Producto(
                        rs.getInt("id"), rs.getString("nombre"), rs.getString("tipo"),
                        rs.getString("especie"), rs.getString("descripcion"), rs.getDouble("precio_unitario"),
                        rs.getString("presentacion"), rs.getInt("stock")
                );
                model.addElement(producto);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar productos: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- MÉTODOS PARA CARRITO ---
    public void agregarProductoAlCarrito(Producto producto, int cantidad) {
        if (producto == null) {
            vista.showStatus("Por favor, selecciona un producto del catálogo.", true);
            return;
        }
        if (cantidad <= 0) {
            vista.showStatus("La cantidad debe ser un número positivo.", true);
            return;
        }
        if (cantidad > producto.getStock()) {
            vista.showStatus("La cantidad solicitada (" + cantidad + ") excede el stock disponible (" + producto.getStock() + ").", true);
            return;
        }

        for (CarritoItem item : carrito) {
            if (item.getProductoId() == producto.getId()) {
                if (item.getCantidad() + cantidad > producto.getStock()) {
                    vista.showStatus("No puedes agregar más de este producto, excede el stock total.", true);
                    return;
                }
                item.setCantidad(item.getCantidad() + cantidad);
                actualizarVistaCarrito();
                vista.showStatus("Cantidad de '" + producto.getNombre() + "' actualizada en el carrito.", false);
                return;
            }
        }

        carrito.add(new CarritoItem(producto.getId(), producto.getNombre(), cantidad, producto.getPrecioUnitario()));
        actualizarVistaCarrito();
        vista.showStatus("'" + producto.getNombre() + "' agregado al carrito.", false);
    }

    public void eliminarProductoDelCarrito() {
        int selectedRow = vista.getSelectedCarritoRowIndex();
        if (selectedRow == -1) {
            vista.showStatus("Selecciona un producto del carrito para eliminar.", true);
            return;
        }
        carrito.remove(selectedRow);
        actualizarVistaCarrito();
        vista.showStatus("Producto eliminado del carrito.", false);
    }

    public void actualizarVistaCarrito() {
        DefaultTableModel model = vista.getCarritoTableModel();
        model.setRowCount(0);
        double total = 0;
        for (CarritoItem item : carrito) {
            Vector<Object> row = new Vector<>();
            row.add(item.getNombre());
            row.add(item.getCantidad());
            row.add(currencyFormat.format(item.getPrecioUnitario()));
            row.add(currencyFormat.format(item.getSubtotal()));
            model.addRow(row);
            total += item.getSubtotal();
        }
        vista.getTotalCarritoLabel().setText("Total: " + currencyFormat.format(total));
    }

    // --- MÉTODOS PARA PEDIDOS Y FACTURACIÓN ---
    public void realizarPedido() {
        if (carrito.isEmpty()) {
            vista.showStatus("El carrito de compras está vacío.", true);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(vista, "¿Confirmas que deseas realizar este pedido?", "Confirmar Pedido", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        try {
            conn = conexion.getConnection();
            conn.setAutoCommit(false);
            double totalPedido = carrito.stream().mapToDouble(CarritoItem::getSubtotal).sum();
            String sqlPedido = "INSERT INTO pedidos (id_usuario, estado, total) VALUES (?, 'Pendiente', ?)";
            int pedidoId;
            try (PreparedStatement pstmtPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPedido.setInt(1, idGanadero);
                pstmtPedido.setDouble(2, totalPedido);
                pstmtPedido.executeUpdate();
                try (ResultSet generatedKeys = pstmtPedido.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pedidoId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID del pedido generado.");
                    }
                }
            }
            String sqlDetalle = "INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
            String sqlUpdateStock = "UPDATE productos SET stock = stock - ? WHERE id = ?";
            try (PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle);
                 PreparedStatement pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock)) {
                for (CarritoItem item : carrito) {
                    pstmtDetalle.setInt(1, pedidoId);
                    pstmtDetalle.setInt(2, item.getProductoId());
                    pstmtDetalle.setInt(3, item.getCantidad());
                    pstmtDetalle.setDouble(4, item.getPrecioUnitario());
                    pstmtDetalle.addBatch();

                    pstmtUpdateStock.setInt(1, item.getCantidad());
                    pstmtUpdateStock.setInt(2, item.getProductoId());
                    pstmtUpdateStock.addBatch();
                }
                pstmtDetalle.executeBatch();
                pstmtUpdateStock.executeBatch();
            }
            conn.commit();
            JOptionPane.showMessageDialog(vista, "¡Pedido realizado con éxito! ID del Pedido: " + pedidoId, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            carrito.clear();
            actualizarVistaCarrito();
            cargarProductos();
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(vista, "Error al realizar el pedido: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void cargarPedidos() {
        DefaultTableModel model = vista.getPedidosTableModel();
        model.setRowCount(0);
        String query = "SELECT id, fecha, estado, total FROM pedidos WHERE id_usuario = ? ORDER BY fecha DESC";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idGanadero);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getTimestamp("fecha"));
                row.add(rs.getString("estado"));
                row.add(currencyFormat.format(rs.getDouble("total")));
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar tus pedidos: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Muestra un diálogo detallado del pedido seleccionado.
     */
    public void mostrarDetallesPedido() {
        int pedidoId = vista.getSelectedPedidoId();
        if (pedidoId == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un pedido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = conexion.getConnection()) {
            Pedido pedido = obtenerPedidoPorId(conn, pedidoId);
            if (pedido == null) {
                JOptionPane.showMessageDialog(vista, "No se encontró el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<DetallePedidoProducto> productos = obtenerDetallesProductosPorPedido(conn, pedidoId);
            String transportistaNombre = obtenerTransportistaPorPedido(conn, pedidoId);
            String estadoEnvio = obtenerEstadoEnvioPorPedido(conn, pedidoId);

            JDialog detallesDialog = new JDialog(vista, "Detalles del Pedido #" + pedidoId, true);
            detallesDialog.setSize(700, 500);
            detallesDialog.setLocationRelativeTo(vista);
            detallesDialog.setLayout(new BorderLayout(10, 10));

            JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
            infoPanel.setBorder(BorderFactory.createTitledBorder("Información General"));
            infoPanel.add(new JLabel("ID del Pedido:")); infoPanel.add(new JLabel(String.valueOf(pedido.getId())));
            infoPanel.add(new JLabel("Fecha:")); infoPanel.add(new JLabel(pedido.getFecha().toString()));
            infoPanel.add(new JLabel("Estado del Pedido:")); infoPanel.add(new JLabel(pedido.getEstado()));
            infoPanel.add(new JLabel("Total Pagado:")); infoPanel.add(new JLabel(currencyFormat.format(pedido.getTotal())));

            DefaultTableModel productosTableModel = new DefaultTableModel(new String[]{"Producto", "Cantidad", "P. Unitario", "Subtotal"}, 0);
            for (DetallePedidoProducto p : productos) {
                productosTableModel.addRow(new Object[]{
                        p.getProductoNombre(), p.getCantidad(),
                        currencyFormat.format(p.getPrecioUnitario()), currencyFormat.format(p.getSubtotal())
                });
            }
            JTable productosTable = new JTable(productosTableModel);
            JScrollPane productosScrollPane = new JScrollPane(productosTable);
            productosScrollPane.setBorder(BorderFactory.createTitledBorder("Productos del Pedido"));

            JPanel envioPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            envioPanel.setBorder(BorderFactory.createTitledBorder("Información de Envío"));
            envioPanel.add(new JLabel("Transportista: " + (transportistaNombre != null ? transportistaNombre : "No asignado")));
            envioPanel.add(new JLabel("Estado de Envío: " + (estadoEnvio != null ? estadoEnvio : "N/A")));

            JButton cerrarBtn = new JButton("Cerrar");
            cerrarBtn.addActionListener(e -> detallesDialog.dispose());

            JPanel centroPanel = new JPanel(new BorderLayout(10, 0));
            centroPanel.add(productosScrollPane, BorderLayout.CENTER);
            centroPanel.add(envioPanel, BorderLayout.EAST);

            detallesDialog.add(infoPanel, BorderLayout.NORTH);
            detallesDialog.add(centroPanel, BorderLayout.CENTER);
            detallesDialog.add(cerrarBtn, BorderLayout.SOUTH);

            detallesDialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al obtener los detalles: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Genera un archivo de texto (.txt) con la factura de un pedido.
     */
    public void generarFacturaTxt() {
        int pedidoId = vista.getSelectedPedidoId();
        if (pedidoId == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un pedido para generar su factura.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = conexion.getConnection()) {
            Pedido pedido = obtenerPedidoPorId(conn, pedidoId);
            List<DetallePedidoProducto> productos = obtenerDetallesProductosPorPedido(conn, pedidoId);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Factura");
            fileChooser.setSelectedFile(new File("Factura-Pedido-" + pedido.getId() + ".txt"));

            if (fileChooser.showSaveDialog(vista) == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                    Map<String, String> datosGanadero = obtenerDatosGanadero(conn);

                    writer.write("=========================================================\n");
                    writer.write("                     FACTURA DE VENTA\n");
                    writer.write("=========================================================\n\n");
                    writer.write("AGRO-NEGOCIO \"EL CAMPESINO\"\n");
                    writer.write("Dirección: Av. de los Shyris y Naciones Unidas, Quito\n");
                    writer.write("---------------------------------------------------------\n");
                    writer.write("Datos del Cliente:\n");
                    writer.write("Nombre:    " + datosGanadero.getOrDefault("nombre", "N/A") + "\n");
                    writer.write("Dirección: " + datosGanadero.getOrDefault("direccion", "N/A") + "\n");
                    writer.write("Teléfono:  " + datosGanadero.getOrDefault("telefono", "N/A") + "\n");
                    writer.write("Correo:    " + datosGanadero.getOrDefault("correo", "N/A") + "\n");
                    writer.write("---------------------------------------------------------\n\n");
                    writer.write("Factura Nro: " + String.format("%09d", pedido.getId()) + "\n");
                    writer.write("Fecha de Emisión: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(pedido.getFecha()) + "\n\n");
                    writer.write(String.format("%-30s %10s %15s %15s\n", "Producto", "Cantidad", "P. Unitario", "Subtotal"));
                    writer.write("------------------------------------------------------------------------\n");

                    for (DetallePedidoProducto p : productos) {
                        writer.write(String.format("%-30.30s %10d %15s %15s\n",
                                p.getProductoNombre(), p.getCantidad(),
                                currencyFormat.format(p.getPrecioUnitario()), currencyFormat.format(p.getSubtotal())));
                    }

                    writer.write("------------------------------------------------------------------------\n");
                    writer.write(String.format("%57s %15s\n", "SUBTOTAL:", currencyFormat.format(pedido.getTotal())));
                    writer.write(String.format("%57s %15s\n", "IVA (0%):", currencyFormat.format(0.00)));
                    writer.write(String.format("%57s %15s\n", "TOTAL A PAGAR:", currencyFormat.format(pedido.getTotal())));
                    writer.write("\n=========================================================\n");
                    writer.write("         Gracias por su compra. ¡Vuelva pronto!\n");
                    writer.write("=========================================================\n");

                    JOptionPane.showMessageDialog(vista, "Factura guardada exitosamente en:\n" + fileToSave.getAbsolutePath(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(vista, "Error al guardar el archivo: " + e.getMessage(), "Error de Archivo", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al generar la factura: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Métodos Auxiliares ---
    private Map<String, String> obtenerDatosGanadero(Connection conn) throws SQLException {
        Map<String, String> datos = new HashMap<>();
        String query = "SELECT nombre, correo, telefono, direccion FROM usuarios WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, this.idGanadero);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                datos.put("nombre", rs.getString("nombre"));
                datos.put("correo", rs.getString("correo"));
                datos.put("telefono", rs.getString("telefono"));
                datos.put("direccion", rs.getString("direccion"));
            }
        }
        return datos;
    }

    private Pedido obtenerPedidoPorId(Connection conn, int pedidoId) throws SQLException {
        String query = "SELECT id, fecha, estado, total FROM pedidos WHERE id = ? AND id_usuario = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, pedidoId);
            pstmt.setInt(2, idGanadero);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Pedido(rs.getInt("id"), rs.getTimestamp("fecha"), rs.getString("estado"), rs.getDouble("total"), "");
            }
        }
        return null;
    }

    private List<DetallePedidoProducto> obtenerDetallesProductosPorPedido(Connection conn, int pedidoId) throws SQLException {
        List<DetallePedidoProducto> productos = new ArrayList<>();
        String query = "SELECT dp.cantidad, dp.precio_unitario, p.nombre FROM detalle_pedido dp JOIN productos p ON dp.id_producto = p.id WHERE dp.id_pedido = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, pedidoId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int cantidad = rs.getInt("cantidad");
                double precioUnitario = rs.getDouble("precio_unitario");
                productos.add(new DetallePedidoProducto(rs.getString("nombre"), cantidad, precioUnitario, cantidad * precioUnitario));
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

    public void cerrarSesion() {
        vista.dispose();
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}