package Vistas;

import Controladores.LoginControlador;
import Servicios.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Ventana de inicio de sesión rediseñada con una estética moderna.
 * <p>
 * Combina un panel de bienvenida con un formulario de inicio de sesión limpio,
 * siguiendo las directrices de diseño de {@link UIUtils}.
 */
public class Login extends JFrame {
    private final LoginControlador controlador;

    // --- Componentes de la Interfaz ---
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton createAccountButton;
    private JLabel forgotPasswordLabel;
    private JLabel messageLabel; // Para mostrar errores de forma sutil

    /**
     * Constructor que inicializa y ensambla la nueva interfaz de login.
     */
    public Login() {
        this.controlador = new LoginControlador(this);

        setTitle("Inicio de Sesión - ProyectoSDA");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2)); // Layout principal de dos columnas

        // --- 1. Panel Izquierdo de Bienvenida (Branding) ---
        add(createWelcomePanel());

        // --- 2. Panel Derecho del Formulario de Login ---
        add(createLoginPanel());
    }

    /**
     * Crea el panel izquierdo con un fondo gradiente y el título del proyecto.
     * @return Un {@link JPanel} de bienvenida.
     */
    private JPanel createWelcomePanel() {
        // Usamos una clase anónima para sobrescribir paintComponent y dibujar un gradiente
        JPanel welcomePanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, UIUtils.COLOR_PRINCIPAL, 0, getHeight(), UIUtils.COLOR_SECUNDARIO);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Contenedor para el texto, para centrarlo fácilmente
        JPanel textContainer = new JPanel();
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));
        textContainer.setOpaque(false); // Hacerlo transparente para ver el gradiente

        JLabel titleLabel = new JLabel("Bienvenido!");
        titleLabel.setFont(UIUtils.FUENTE_TITULO_LOGIN);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Gestión para Agro-Negocio 'El Campesino'");
        subtitleLabel.setFont(UIUtils.FUENTE_SUBTITULO_LOGIN);
        subtitleLabel.setForeground(new Color(230, 230, 250)); // Un blanco más suave
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        textContainer.add(titleLabel);
        textContainer.add(Box.createRigidArea(new Dimension(0, 10))); // Espaciador
        textContainer.add(subtitleLabel);

        welcomePanel.add(textContainer);
        return welcomePanel;
    }

    /**
     * Crea el panel derecho con el formulario de inicio de sesión.
     * @return Un {@link JPanel} con los campos de login.
     */
    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(UIUtils.BORDE_PANELES);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        gbc.gridx = 0;

        // Título del formulario
        JLabel loginTitle = new JLabel("Iniciar Sesión");
        loginTitle.setFont(UIUtils.FUENTE_TITULO_LOGIN.deriveFont(Font.BOLD, 24f));
        loginTitle.setForeground(UIUtils.COLOR_TEXTO_NORMAL);
        loginTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        loginPanel.add(loginTitle, gbc);

        // Mensaje de error
        messageLabel = new JLabel(" ");
        messageLabel.setFont(UIUtils.FUENTE_NORMAL);
        messageLabel.setForeground(UIUtils.COLOR_ACCION_NEGATIVA);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        loginPanel.add(messageLabel, gbc);

        // Campo de Email
        JLabel emailLabel = new JLabel("Correo Electrónico:");
        emailLabel.setFont(UIUtils.FUENTE_ETIQUETA);
        gbc.gridy = 2;
        loginPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(UIUtils.FUENTE_NORMAL);
        emailField.setBorder(UIUtils.BORDE_CAMPOS_LOGIN);
        gbc.gridy = 3;
        loginPanel.add(emailField, gbc);

        // Campo de Contraseña
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(UIUtils.FUENTE_ETIQUETA);
        gbc.gridy = 4;
        loginPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(UIUtils.FUENTE_NORMAL);
        passwordField.setBorder(UIUtils.BORDE_CAMPOS_LOGIN);
        gbc.gridy = 5;
        loginPanel.add(passwordField, gbc);

        // Botón de Login
        loginButton = new JButton("Ingresar");
        loginButton.setFont(UIUtils.FUENTE_ETIQUETA);
        loginButton.setBackground(UIUtils.COLOR_PRINCIPAL);
        loginButton.setForeground(Color.WHITE);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginPanel.add(loginButton, gbc);

        // Botones secundarios
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 10, 10);

        createAccountButton = new JButton("Crear Cuenta");
        createAccountButton.setFont(UIUtils.FUENTE_NORMAL);
        gbc.gridy = 7;
        gbc.gridx = 0;
        loginPanel.add(createAccountButton, gbc);

        forgotPasswordLabel = new JLabel("<html><u>¿Olvidaste tu contraseña?</u></html>");
        forgotPasswordLabel.setFont(UIUtils.FUENTE_NORMAL);
        forgotPasswordLabel.setForeground(UIUtils.COLOR_SECUNDARIO);
        forgotPasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridx = 1;
        loginPanel.add(forgotPasswordLabel, gbc);

        // --- Asignación de Listeners ---
        loginButton.addActionListener(e -> controlador.handleLogin());
        createAccountButton.addActionListener(e -> controlador.handleCreateAccount());
        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controlador.handleChangePassword();
            }
        });

        return loginPanel;
    }

    // --- Getters y Setters para el Controlador ---
    public String getEmail() { return emailField.getText(); }
    public char[] getPassword() { return passwordField.getPassword(); }

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public void close() { this.dispose(); }
}