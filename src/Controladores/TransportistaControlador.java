package Controladores;

import Servicios.ConexionBD;
import Vistas.Login;
import Vistas.Transportista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;

/**
 * Controlador para la vista de Transportista. Gestiona la lógica de negocio
 * para la visualización y actualización de los envíos asignados.
 */
public class TransportistaControlador {

    private final Transportista vista;
    private final int idTransportista;
    private final ConexionBD conexion;

    /**
     * Constructor para TransportistaControlador.
     * @param vista La instancia de la vista Transportista que este controlador maneja.
     * @param idTransportista El ID del transportista que ha iniciado sesión.
     */
    public TransportistaControlador(Transportista vista, int idTransportista) {
        this.vista = vista;
        this.idTransportista = idTransportista;
        this.conexion = new ConexionBD();
    }

    /**
     * Carga los envíos asignados al transportista desde la base de datos y los
     * muestra en la tabla principal.
     */
    public void cargarEnviosAsignados() {
        DefaultTableModel model = vista.getEnviosTableModel();
        model.setRowCount(0);
        vista.getDetallesEnvioArea().setText("Selecciona un envío de la tabla para ver sus detalles aquí.");

        String query = "SELECT e.id, e.id_pedido, p.fecha AS fecha_pedido, e.estado_envio, u.direccion " +
                "FROM envios e JOIN pedidos p ON e.id_pedido = p.id " +
                "JOIN usuarios u ON p.id_usuario = u.id " +
                "WHERE e.id_transportista = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idTransportista);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getInt("id_pedido"));
                row.add(rs.getTimestamp("fecha_pedido"));
                row.add(rs.getString("estado_envio"));
                row.add(rs.getString("direccion"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar los envíos: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * NUEVO: Muestra la información detallada del envío seleccionado en el panel de texto.
     */
    public void mostrarDetallesEnvioSeleccionado() {
        int envioId = vista.getSelectedEnvioId();
        if (envioId == -1) {
            return; // No hacer nada si no hay selección
        }

        String query = "SELECT u.nombre AS cliente_nombre, u.telefono AS cliente_telefono, u.direccion, p.total, p.fecha " +
                "FROM envios e " +
                "JOIN pedidos p ON e.id_pedido = p.id " +
                "JOIN usuarios u ON p.id_usuario = u.id " +
                "WHERE e.id = ?";

        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, envioId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String nombreCliente = rs.getString("cliente_nombre");
                String telefonoCliente = rs.getString("cliente_telefono");
                String direccion = rs.getString("direccion");
                double total = rs.getDouble("total");
                Timestamp fecha = rs.getTimestamp("fecha");

                String detalles = "===== DETALLES DE ENTREGA =====\n\n" +
                        "Cliente:\t" + nombreCliente + "\n" +
                        "Teléfono:\t" + telefonoCliente + "\n\n" +
                        "Dirección de Entrega:\n" + direccion + "\n\n" +
                        "--------------------------------------------------\n" +
                        "Total del Pedido:\t$ " + String.format("%.2f", total) + "\n" +
                        "Fecha del Pedido:\t" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha);

                vista.getDetallesEnvioArea().setText(detalles);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al obtener detalles del envío: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Muestra un diálogo para que el transportista actualice el estado de un envío seleccionado.
     */
    public void actualizarEstadoEnvio() {
        int envioId = vista.getSelectedEnvioId();
        if (envioId == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un envío de la tabla para actualizar.", "Ningún Envío Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] estados = {"En ruta", "Entregado"};
        String nuevoEstado = (String) JOptionPane.showInputDialog(vista,
                "Selecciona el nuevo estado para el envío #" + envioId,
                "Actualizar Estado de Envío",
                JOptionPane.QUESTION_MESSAGE,
                null,
                estados,
                estados[0]);

        if (nuevoEstado != null) {
            actualizarEstadoEnBD(envioId, nuevoEstado);
        }
    }

    /**
     * Actualiza el estado de un envío en la base de datos y, si corresponde, el del pedido.
     * @param envioId El ID del envío a actualizar.
     * @param nuevoEstado El nuevo estado para el envío.
     */
    private void actualizarEstadoEnBD(int envioId, String nuevoEstado) {
        String queryEnvio = "UPDATE envios SET estado_envio = ?, fecha_entrega_real = ? WHERE id = ?";
        String queryPedido = "UPDATE pedidos SET estado = ? WHERE id = (SELECT id_pedido FROM envios WHERE id = ?)";

        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false); // Iniciar transacción

            // Actualizar tabla 'envios'
            try (PreparedStatement pstmtEnvio = conn.prepareStatement(queryEnvio)) {
                pstmtEnvio.setString(1, nuevoEstado);
                if ("Entregado".equals(nuevoEstado)) {
                    pstmtEnvio.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                } else {
                    pstmtEnvio.setNull(2, java.sql.Types.TIMESTAMP);
                }
                pstmtEnvio.setInt(3, envioId);
                pstmtEnvio.executeUpdate();
            }

            // Si el envío se marca como "Entregado", también se actualiza la tabla 'pedidos'
            if ("Entregado".equals(nuevoEstado)) {
                try (PreparedStatement pstmtPedido = conn.prepareStatement(queryPedido)) {
                    pstmtPedido.setString(1, "Entregado");
                    pstmtPedido.setInt(2, envioId);
                    pstmtPedido.executeUpdate();
                }
            }

            conn.commit(); // Confirmar transacción
            JOptionPane.showMessageDialog(vista, "Estado del envío actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarEnviosAsignados();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al actualizar el estado: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Cierra la sesión actual y vuelve a la ventana de Login.
     */
    public void cerrarSesion() {
        vista.dispose();
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}