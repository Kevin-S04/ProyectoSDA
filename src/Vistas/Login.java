package Vistas;

import Controladores.LoginControlador;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Ventana de inicio de sesión de la aplicación.
 * Crea y muestra su propia interfaz de usuario (la Vista).
 */
public class Login extends JFrame {
    private final LoginControlador controlador;

    // Componentes de la interfaz
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private JButton loginButton;
    private JButton createAccountButton;
    private JLabel forgotPasswordLabel;

    /**
     * Constructor de la vista de Login. Inicializa los componentes de la interfaz
     * y asigna el controlador para manejar los eventos.
     */
    public Login() {
        this.controlador = new LoginControlador(this);

        // Configuración de la ventana
        setTitle("Inicio de Sesión");
        setSize(500, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Creamos y configuramos el layout principal
        setLayout(new BorderLayout(10, 10));

        // Panel para los campos de login
        JPanel loginPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20)); // Borde para espacio

        JLabel emailLabel = new JLabel("Correo Electrónico:");
        emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordField = new JPasswordField();

        loginPanel.add(emailLabel);
        loginPanel.add(emailField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);

        // Panel para los botones y labels
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        loginButton = new JButton("Iniciar Sesión");
        createAccountButton = new JButton("Crear Cuenta");
        forgotPasswordLabel = new JLabel("¿Olvidaste tu contraseña?");

        forgotPasswordLabel.setForeground(Color.BLUE);
        forgotPasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        actionsPanel.add(loginButton);
        actionsPanel.add(createAccountButton);
        actionsPanel.add(forgotPasswordLabel);

        // Label para mensajes
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Serif", Font.BOLD, 14));

        // Agregamos los paneles a la ventana
        add(loginPanel, BorderLayout.CENTER);
        add(actionsPanel, BorderLayout.SOUTH);
        add(messageLabel, BorderLayout.NORTH);

        // Asignamos los listeners a los componentes
        loginButton.addActionListener(e -> controlador.handleLogin());
        createAccountButton.addActionListener(e -> controlador.handleCreateAccount());
        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                controlador.handleChangePassword();
            }
        });
    }

    /**
     * Obtiene el texto del campo de correo electrónico.
     * @return El correo electrónico ingresado por el usuario.
     */
    public String getEmail() {
        return emailField.getText();
    }

    /**
     * Obtiene la contraseña del campo de contraseña.
     * @return La contraseña ingresada por el usuario en formato de array de caracteres.
     */
    public char[] getPassword() {
        return passwordField.getPassword();
    }

    /**
     * Muestra un cuadro de diálogo con un mensaje.
     * @param message El mensaje a mostrar.
     * @param title El título del cuadro de diálogo.
     * @param messageType El tipo de mensaje (JOptionPane.INFORMATION_MESSAGE, etc.).
     */
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    /**
     * Establece el texto en el label de mensajes de la interfaz.
     * @param message El mensaje a mostrar.
     */
    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    /**
     * Cierra la ventana actual.
     */
    public void close() {
        this.dispose();
    }
}