package Controladores;

import Controladores.LoginControlador;
import Vistas.Login;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase {@link LoginControlador}.
 *
 * <p>Esta clase de pruebas verifica el comportamiento del controlador de inicio de sesión
 * utilizando una implementación stub de la vista {@link Login} para simular interacciones
 * sin necesidad de una interfaz gráfica real.</p>
 */
public class LoginControladorTest {

    private LoginStub vista;
    private LoginControlador controlador;

    /**
     * Implementación stub de {@link Login} para pruebas.
     *
     * <p>Esta clase permite simular el comportamiento de la vista de login sin interactuar
     * con la interfaz gráfica real, almacenando los datos de entrada y capturando los mensajes
     * mostrados para su verificación en las pruebas.</p>
     */
    static class LoginStub extends Login {
        private String email;
        private char[] password;
        private String lastMessage;
        private String lastTitle;
        private int lastMessageType;

        /**
         * Constructor del stub de login.
         *
         * @param email Correo electrónico a devolver en getEmail()
         * @param password Contraseña a devolver en getPassword()
         */
        public LoginStub(String email, String password) {
            this.email = email;
            this.password = password.toCharArray();
        }

        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public char[] getPassword() {
            return password;
        }

        @Override
        public void showMessage(String message, String title, int messageType) {
            this.lastMessage = message;
            this.lastTitle = title;
            this.lastMessageType = messageType;
            System.out.println(title + ": " + message);
        }

        @Override
        public void setMessage(String message) {
            this.lastMessage = message;
            System.out.println("Vista setMessage: " + message);
        }

        @Override
        public void close() {
            System.out.println("Cerrar Login");
        }
    }

    /**
     * Configura el entorno de prueba antes de cada test.
     *
     * <p>Inicializa una instancia de {@link LoginStub} con credenciales vacías
     * y crea un {@link LoginControlador} asociado a esta vista stub.</p>
     */
    @BeforeEach
    void setUp() {
        vista = new LoginStub("", "");
        controlador = new LoginControlador(vista) {
            protected void openMainWindow(String role, int userId) {
                System.out.println("openMainWindow llamado con rol: " + role);
            }
        };
    }

    /**
     * Prueba el intento de login con credenciales vacías.
     *
     * <p>Verifica que el controlador muestre el mensaje de error adecuado cuando
     * se intenta iniciar sesión sin proporcionar email ni contraseña.</p>
     */
    @Test
    void testLoginConDatosVacios() {
        controlador.handleLogin();

        assertEquals("El correo y la contraseña no pueden estar vacíos.", vista.lastMessage,
                "Debería mostrar mensaje de campos vacíos");
        assertEquals("Error de Entrada", vista.lastTitle,
                "Debería mostrar título de error adecuado");
        assertEquals(JOptionPane.WARNING_MESSAGE, vista.lastMessageType,
                "Debería usar el tipo de mensaje de advertencia");
    }

    /**
     * Prueba el intento de login con credenciales inválidas.
     *
     * <p>Verifica que el controlador maneje adecuadamente el caso cuando se
     * proporcionan credenciales que no existen en el sistema o cuando hay
     * problemas de conexión con la base de datos.</p>
     */
    @Test
    void testLoginConCredencialesInvalidas() {
        // Configurar credenciales de prueba
        vista.email = "usuario@correo.com";
        vista.password = "1234".toCharArray();

        controlador.handleLogin();

        // Verificar que se muestre algún tipo de mensaje de error
        boolean esMensajeErrorCredenciales = vista.lastMessage != null &&
                vista.lastMessage.toLowerCase().contains("incorrect");
        boolean esMensajeErrorConexion = vista.lastTitle != null &&
                vista.lastTitle.equals("Error");

        assertTrue(esMensajeErrorCredenciales || esMensajeErrorConexion,
                "Debería mostrar mensaje de credenciales incorrectas o error de conexión");
    }
}

