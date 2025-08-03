import Vistas.Login;
import javax.swing.*;

/**
 * Clase principal que inicia la aplicación.
 * Contiene el método main que crea y muestra la ventana de login.
 */
public class Main {
    /**
     * El método de entrada principal de la aplicación.
     * Utiliza SwingUtilities.invokeLater para asegurar que la interfaz de usuario
     * se cree y se muestre en el hilo de despacho de eventos (Event Dispatch Thread - EDT).
     * @param args Argumentos de la línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}