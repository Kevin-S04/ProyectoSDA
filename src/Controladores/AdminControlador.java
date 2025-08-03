package Controladores;

import Modelos.Usuario;
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
import java.util.Vector;

/**
 * Controlador principal para la ventana de Administración.
 * Maneja la lógica de negocio y las interacciones con la base de datos
 * para la gestión de usuarios, productos, pedidos, etc.
 */
public class AdminControlador {
    private final Admin vista;
    private final ConexionBD conexion;

    public AdminControlador(Admin vista) {
        this.vista = vista;
        this.conexion = new ConexionBD();
    }

    /**
     * Carga todos los usuarios de la base de datos y los muestra en la tabla de la vista.
     */
    public void cargarUsuarios() {
        DefaultTableModel model = vista.getUsuariosTableModel();
        model.setRowCount(0); // Limpia la tabla antes de cargar nuevos datos

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
     * Muestra un cuadro de diálogo para crear un nuevo usuario.
     */
    public void mostrarDialogoCrearUsuario() {
        JTextField nombreField = new JTextField();
        JTextField correoField = new JTextField();
        JPasswordField contrasenaField = new JPasswordField();
        JTextField telefonoField = new JTextField();
        JTextField direccionField = new JTextField();
        JComboBox<String> rolComboBox = new JComboBox<>(new String[]{"Administrador", "Transportista", "Inventariado", "Ganadero"});

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Correo Electrónico:"));
        panel.add(correoField);
        panel.add(new JLabel("Contraseña:"));
        panel.add(contrasenaField);
        panel.add(new JLabel("Teléfono:"));
        panel.add(telefonoField);
        panel.add(new JLabel("Dirección:"));
        panel.add(direccionField);
        panel.add(new JLabel("Rol:"));
        panel.add(rolComboBox);

        int result = JOptionPane.showConfirmDialog(vista, panel, "Crear Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String nombre = nombreField.getText();
            String correo = correoField.getText();
            String contrasena = new String(contrasenaField.getPassword());
            String telefono = telefonoField.getText();
            String direccion = direccionField.getText();
            String rol = (String) rolComboBox.getSelectedItem();

            if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || telefono.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Por favor, completa todos los campos obligatorios.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            } else {
                crearUsuario(nombre, correo, contrasena, rol, telefono, direccion);
            }
        }
    }

    /**
     * Inserta un nuevo usuario en la base de datos.
     */
    private void crearUsuario(String nombre, String correo, String contrasena, String rol, String telefono, String direccion) {
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
                cargarUsuarios(); // Recargamos la tabla para mostrar el nuevo usuario
            } else {
                JOptionPane.showMessageDialog(vista, "Error al crear el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error de conexión o el correo ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Muestra un cuadro de diálogo para editar el usuario seleccionado.
     */
    public void mostrarDialogoEditarUsuario() {
        int userId = vista.getSelectedUserId();
        if (userId == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un usuario de la tabla para editar.", "Ningún Usuario Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "SELECT nombre, correo, contraseña, rol, telefono, direccion FROM usuarios WHERE id = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                JTextField nombreField = new JTextField(rs.getString("nombre"));
                JTextField correoField = new JTextField(rs.getString("correo"));
                JPasswordField contrasenaField = new JPasswordField(rs.getString("contraseña"));
                JTextField telefonoField = new JTextField(rs.getString("telefono"));
                JTextField direccionField = new JTextField(rs.getString("direccion"));
                JComboBox<String> rolComboBox = new JComboBox<>(new String[]{"Administrador", "Transportista", "Inventariado", "Ganadero"});
                rolComboBox.setSelectedItem(rs.getString("rol"));

                JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
                panel.add(new JLabel("Nombre:"));
                panel.add(nombreField);
                panel.add(new JLabel("Correo Electrónico:"));
                panel.add(correoField);
                panel.add(new JLabel("Contraseña:"));
                panel.add(contrasenaField);
                panel.add(new JLabel("Teléfono:"));
                panel.add(telefonoField);
                panel.add(new JLabel("Dirección:"));
                panel.add(direccionField);
                panel.add(new JLabel("Rol:"));
                panel.add(rolComboBox);

                int result = JOptionPane.showConfirmDialog(vista, panel, "Editar Usuario ID: " + userId, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    String nombre = nombreField.getText();
                    String correo = correoField.getText();
                    String contrasena = new String(contrasenaField.getPassword());
                    String telefono = telefonoField.getText();
                    String direccion = direccionField.getText();
                    String rol = (String) rolComboBox.getSelectedItem();

                    editarUsuario(userId, nombre, correo, contrasena, rol, telefono, direccion);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al obtener los datos del usuario: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Actualiza un usuario en la base de datos.
     */
    private void editarUsuario(int id, String nombre, String correo, String contrasena, String rol, String telefono, String direccion) {
        String query = "UPDATE usuarios SET nombre=?, correo=?, contraseña=?, rol=?, telefono=?, direccion=? WHERE id=?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, correo);
            pstmt.setString(3, contrasena);
            pstmt.setString(4, rol);
            pstmt.setString(5, telefono);
            pstmt.setString(6, direccion);
            pstmt.setInt(7, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(vista, "Usuario editado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios(); // Recargamos la tabla
            } else {
                JOptionPane.showMessageDialog(vista, "Error al editar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error de conexión o el correo ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Elimina el usuario seleccionado de la base de datos, previa confirmación.
     */
    public void eliminarUsuario() {
        int userId = vista.getSelectedUserId();
        if (userId == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, selecciona un usuario de la tabla para eliminar.", "Ningún Usuario Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmResult = JOptionPane.showConfirmDialog(vista, "¿Estás seguro de que deseas eliminar este usuario?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmResult == JOptionPane.YES_OPTION) {
            String query = "DELETE FROM usuarios WHERE id = ?";
            try (Connection conn = conexion.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, userId);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(vista, "Usuario eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarUsuarios(); // Recargamos la tabla
                } else {
                    JOptionPane.showMessageDialog(vista, "Error al eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vista, "Error de conexión al eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Cierra la ventana actual de Administrador y abre la ventana de Login.
     */
    public void cerrarSesion() {
        vista.dispose();
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}