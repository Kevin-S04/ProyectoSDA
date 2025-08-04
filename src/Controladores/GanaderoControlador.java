package Controladores;

import Modelos.CarritoItem;
import Modelos.DetallePedidoProducto;
import Modelos.Pedido;
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
 * el carrito de compras, la realización de pedidos y la generación de facturas.
 */
public class GanaderoControlador {

    private final Ganadero vista;
    private final int idGanadero;
    private final ConexionBD conexion;
    private final List<CarritoItem> carrito;
    private final NumberFormat currencyFormat;

    /**
     * Constructor para GanaderoControlador.
     * @param vista La instancia de la vista Ganadero que este controlador maneja.
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
    /**
     * Carga los productos con stock disponible desde la base de datos y los muestra en la tabla.
     */
    public void cargarProductos() {
        DefaultTableModel model = vista.getProductosTableModel();
        model.setRowCount(0);
        String query = "SELECT id, nombre, tipo, especie, descripcion, precio_unitario, presentacion, stock FROM productos WHERE stock > 0";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("nombre"));
                row.add(rs.getString("tipo"));
                row.add(rs.getString("especie"));
                row.add(rs.getString("descripcion"));
                row.add(currencyFormat.format(rs.getDouble("precio_unitario")));
                row.add(rs.getString("presentacion"));
                row.add(rs.getInt("stock"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar productos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- MÉTODOS PARA CARRITO ---
    /**
     * Agrega un producto seleccionado del catálogo al carrito de compras.
     * Pide al usuario la cantidad deseada y valida contra el stock disponible.
     */
    public void agregarProductoAlCarrito() {
        int selectedRow = vista.getProductosTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un producto de la lista.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int productoId = (int) vista.getProductosTableModel().getValueAt(selectedRow, 0);
        String nombre = (String) vista.getProductosTableModel().getValueAt(selectedRow, 1);
        double precio;
        try {
            precio = currencyFormat.parse((String) vista.getProductosTableModel().getValueAt(selectedRow, 5)).doubleValue();
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(vista, "Error al leer el precio del producto.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int stockDisponible = (int) vista.getProductosTableModel().getValueAt(selectedRow, 7);
        String cantidadStr = JOptionPane.showInputDialog(vista, "Ingrese la cantidad:", "Agregar al Carrito", JOptionPane.PLAIN_MESSAGE);
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) return;
        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cantidad > stockDisponible) {
                JOptionPane.showMessageDialog(vista, "La cantidad solicitada excede el stock disponible (" + stockDisponible + ").", "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
                return;
            }
            for (CarritoItem item : carrito) {
                if (item.getProductoId() == productoId) {
                    if (item.getCantidad() + cantidad > stockDisponible) {
                        JOptionPane.showMessageDialog(vista, "No puedes agregar más de este producto, excede el stock.", "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    item.setCantidad(item.getCantidad() + cantidad);
                    actualizarVistaCarrito();
                    JOptionPane.showMessageDialog(vista, "Cantidad actualizada en el carrito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            carrito.add(new CarritoItem(productoId, nombre, cantidad, precio));
            actualizarVistaCarrito();
            JOptionPane.showMessageDialog(vista, "Producto agregado al carrito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "Por favor, ingrese un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina un producto seleccionado del carrito de compras.
     */
    public void eliminarProductoDelCarrito() {
        int selectedRow = vista.getCarritoTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un producto del carrito para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int productoId = (int) vista.getCarritoTableModel().getValueAt(selectedRow, 0);
        carrito.removeIf(item -> item.getProductoId() == productoId);
        actualizarVistaCarrito();
    }

    /**
     * Actualiza la tabla del carrito y el total a pagar en la vista.
     */
    public void actualizarVistaCarrito() {
        DefaultTableModel model = vista.getCarritoTableModel();
        model.setRowCount(0);
        double total = 0;
        for (CarritoItem item : carrito) {
            Vector<Object> row = new Vector<>();
            row.add(item.getProductoId());
            row.add(item.getNombre());
            row.add(item.getCantidad());
            row.add(currencyFormat.format(item.getPrecioUnitario()));
            row.add(currencyFormat.format(item.getSubtotal()));
            model.addRow(row);
            total += item.getSubtotal();
        }
        vista.getTotalCarritoLabel().setText("Total: " + currencyFormat.format(total));
    }

    // --- MÉTODOS PARA PEDIDOS ---
    /**
     * Realiza el pedido con los productos del carrito.
     * Inserta el pedido y sus detalles en la base de datos y actualiza el stock de productos.
     */
    public void realizarPedido() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El carrito está vacío.", "Advertencia", JOptionPane.WARNING_MESSAGE);
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
                        throw new SQLException("No se pudo obtener el ID del pedido.");
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

    /**
     * Carga el historial de pedidos del ganadero desde la base de datos.
     */
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
            JOptionPane.showMessageDialog(vista, "Error al cargar tus pedidos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // --- SECCIÓN DE DETALLES DEL PEDIDO Y FACTURA ---

    /**
     * Muestra los detalles de un pedido seleccionado, incluyendo la opción de generar una factura.
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

            // ... (Paneles de información y tabla de productos, sin cambios)
            JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
            infoPanel.setBorder(BorderFactory.createTitledBorder("Información General"));
            infoPanel.add(new JLabel("ID del Pedido:"));
            infoPanel.add(new JLabel(String.valueOf(pedido.getId())));
            infoPanel.add(new JLabel("Fecha:"));
            infoPanel.add(new JLabel(pedido.getFecha().toString()));
            infoPanel.add(new JLabel("Estado del Pedido:"));
            infoPanel.add(new JLabel(pedido.getEstado()));
            infoPanel.add(new JLabel("Total Pagado:"));
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

            JPanel envioPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            envioPanel.setBorder(BorderFactory.createTitledBorder("Información de Envío"));
            envioPanel.add(new JLabel("Transportista: " + (transportistaNombre != null ? transportistaNombre : "No asignado")));
            envioPanel.add(new JLabel("Estado de Envío: " + (estadoEnvio != null ? estadoEnvio : "N/A")));

            // --- PANEL DE BOTONES CON NUEVO BOTÓN DE FACTURA ---
            JButton cerrarBtn = new JButton("Cerrar");
            cerrarBtn.addActionListener(e -> detallesDialog.dispose());

            JButton facturaBtn = new JButton("Generar Factura (.txt)");
            facturaBtn.addActionListener(e -> generarFacturaTxt(pedido, productos, conn));

            JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            botonesPanel.add(facturaBtn);
            botonesPanel.add(cerrarBtn);

            JPanel centroPanel = new JPanel(new BorderLayout());
            centroPanel.add(productosScrollPane, BorderLayout.CENTER);
            centroPanel.add(envioPanel, BorderLayout.EAST);

            detallesDialog.add(infoPanel, BorderLayout.NORTH);
            detallesDialog.add(centroPanel, BorderLayout.CENTER);
            detallesDialog.add(botonesPanel, BorderLayout.SOUTH); // Panel con ambos botones

            detallesDialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al obtener los detalles: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Genera un archivo de texto (.txt) con la factura de un pedido.
     * @param pedido El objeto Pedido con la información general.
     * @param productos La lista de productos del pedido.
     * @param conn La conexión a la base de datos para obtener datos del cliente.
     */
    private void generarFacturaTxt(Pedido pedido, List<DetallePedidoProducto> productos, Connection conn) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Factura");
        fileChooser.setSelectedFile(new File("Factura-Pedido-" + pedido.getId() + ".txt"));

        int userSelection = fileChooser.showSaveDialog(vista);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {

                // Obtener datos del ganadero
                Map<String, String> datosGanadero = obtenerDatosGanadero(conn);

                // --- Construcción del contenido de la factura ---
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
                String fechaFormateada = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(pedido.getFecha());
                writer.write("Fecha de Emisión: " + fechaFormateada + "\n\n");

                // Encabezados de la tabla de productos
                writer.write(String.format("%-30s %10s %15s %15s\n", "Producto", "Cantidad", "P. Unitario", "Subtotal"));
                writer.write("------------------------------------------------------------------------\n");

                for (DetallePedidoProducto p : productos) {
                    writer.write(String.format("%-30.30s %10d %15s %15s\n",
                            p.getProductoNombre(),
                            p.getCantidad(),
                            currencyFormat.format(p.getPrecioUnitario()),
                            currencyFormat.format(p.getSubtotal())));
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
                e.printStackTrace();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(vista, "Error al obtener datos del cliente: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

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
     * Cierra la sesión actual y vuelve a la ventana de Login.
     */
    public void cerrarSesion() {
        vista.dispose();
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}