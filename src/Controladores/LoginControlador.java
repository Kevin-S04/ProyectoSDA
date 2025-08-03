package Controladores;

import Vistas.Login;
import Servicios.ConexionBD;
import Vistas.Admin;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controlador para la ventana de Login.
 * Contiene la lógica de negocio, maneja los eventos de la vista y se comunica con el servicio de base de datos.
 */
public class LoginControlador {
    private final Login vista;
    private final ConexionBD conexion;

    /**
     * Constructor del controlador.
     * @param vista La instancia de la ventana de Login que este controlador gestiona.
     */
    public LoginControlador(Login vista) {
        this.vista = vista;
        this.conexion = new ConexionBD();
    }

    /**
     * Maneja el evento de inicio de sesión.
     * Valida las credenciales del usuario con la base de datos y abre la ventana correspondiente si el login es exitoso.
     */
    public void handleLogin() {
        String correo = vista.getEmail();
        String contrasena = new String(vista.getPassword());

        try (Connection conn = conexion.getConnection()) {
            String query = "SELECT rol FROM usuarios WHERE correo = ? AND contraseña = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, correo);
            pstmt.setString(2, contrasena);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String userRole = rs.getString("rol");
                vista.setMessage("¡Bienvenido! Rol: " + userRole);
                vista.showMessage("Acceso concedido para el rol: " + userRole, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                openMainWindow(userRole);
            } else {
                vista.setMessage("Correo o contraseña incorrectos.");
                vista.showMessage("Credenciales incorrectas. Intenta de nuevo.", "Error de Login", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            vista.showMessage("Error de conexión a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Maneja el evento de crear una nueva cuenta.
     * Muestra un diálogo para que el usuario ingrese los datos y los guarda en la base de datos.
     */
    public void handleCreateAccount() {
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

        int result = JOptionPane.showConfirmDialog(null, panel, "Crear Nueva Cuenta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String nombre = nombreField.getText();
            String correo = correoField.getText();
            String contrasena = new String(contrasenaField.getPassword());
            String telefono = telefonoField.getText();
            String direccion = direccionField.getText();
            String rol = (String) rolComboBox.getSelectedItem();

            try (Connection conn = conexion.getConnection()) {
                String query = "INSERT INTO usuarios (nombre, correo, contraseña, rol, telefono, direccion) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, nombre);
                pstmt.setString(2, correo);
                pstmt.setString(3, contrasena);
                pstmt.setString(4, rol);
                pstmt.setString(5, telefono);
                pstmt.setString(6, direccion);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    vista.showMessage("Cuenta creada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    vista.showMessage("Error al crear la cuenta.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                vista.showMessage("Error de conexión o el correo ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Maneja el evento de cambio de contraseña.
     * Muestra un diálogo para que el usuario ingrese su correo y una nueva contraseña, y la actualiza en la base de datos.
     */
    public void handleChangePassword() {
        JTextField correoField = new JTextField();
        JPasswordField nuevaContrasenaField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Ingresa tu correo electrónico:"));
        panel.add(correoField);
        panel.add(new JLabel("Ingresa tu nueva contraseña:"));
        panel.add(nuevaContrasenaField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Cambiar Contraseña", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String correo = correoField.getText();
            String nuevaContrasena = new String(nuevaContrasenaField.getPassword());

            try (Connection conn = conexion.getConnection()) {
                String query = "UPDATE usuarios SET contraseña = ? WHERE correo = ?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, nuevaContrasena);
                pstmt.setString(2, correo);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    vista.showMessage("Contraseña cambiada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    vista.showMessage("Error al cambiar la contraseña. El correo no existe.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                vista.showMessage("Error de conexión a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Abre la ventana principal correspondiente al rol del usuario.
     * Cierra la ventana de login actual y muestra la nueva ventana.
     * @param role El rol del usuario que ha iniciado sesión.
     */
    private void openMainWindow(String role) {
        vista.close();
        switch (role) {
            case "Administrador":
                new Admin().setVisible(true); // Llamada a la nueva clase Admin
                break;
            case "Transportista":
                // new TransportistaWindow().setVisible(true);
                JOptionPane.showMessageDialog(null, "Abriendo ventana de Transportista", "Ventana de Rol", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "Inventariado":
                // new InventariadoWindow().setVisible(true);
                JOptionPane.showMessageDialog(null, "Abriendo ventana de Inventariado", "Ventana de Rol", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "Ganadero":
                // new GanaderoWindow().setVisible(true);
                JOptionPane.showMessageDialog(null, "Abriendo ventana de Ganadero", "Ventana de Rol", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Rol no reconocido", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }
}