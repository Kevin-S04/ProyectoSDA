import Vistas.Login;
import javax.swing.*;

/**
 * Clase principal que inicia la aplicación.
 * <p>
 * Contiene el método main que configura la apariencia visual y muestra la ventana de login.
 */
public class Main {
    /**
     * El método de entrada principal de la aplicación.
     * <p>
     * Configura el Look and Feel 'Nimbus' para una interfaz de usuario más moderna
     * y luego crea y muestra la ventana de login en el hilo de despacho de eventos
     * (Event Dispatch Thread - EDT) para garantizar la seguridad en el manejo de hilos de Swing.
     *
     * @param args Argumentos de la línea de comandos (no se utilizan en esta aplicación).
     */
    public static void main(String[] args) {
        try {
            // Itera sobre los Look and Feel instalados en el sistema y activa "Nimbus".
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si Nimbus no está disponible, se imprimirá un error pero la aplicación continuará con el L&F por defecto.
            System.err.println("El Look and Feel 'Nimbus' no se pudo encontrar. Se usará el predeterminado.");
        }

        // Se asegura de que la creación de la GUI se ejecute en el EDT.
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}