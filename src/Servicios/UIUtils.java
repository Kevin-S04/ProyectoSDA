package Servicios;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Clase de utilidad para centralizar y gestionar las constantes de la interfaz de usuario.
 * <p>
 * Proporciona una paleta de colores, estilos de fuente, bordes y métodos de estilizado
 * para mantener una apariencia visual consistente y profesional en toda la aplicación.
 */
public class UIUtils {

    // --- PALETA DE COLORES (Ajustada para mejor contraste) ---
    public static final Color COLOR_PRINCIPAL = new Color(0, 120, 215); // Un azul más vivo
    public static final Color COLOR_SECUNDARIO = new Color(81, 101, 117); // Gris Pizarra
    public static final Color COLOR_FONDO = new Color(177, 211, 211);
    public static final Color COLOR_CABECERA_TABLA = new Color(250, 225, 230);
    public static final Color COLOR_ACCION_POSITIVA = new Color(40, 167, 69); // Verde Bootstrap
    public static final Color COLOR_ACCION_NEGATIVA = new Color(220, 53, 69);  // Rojo Bootstrap
    public static final Color COLOR_TEXTO_NORMAL = new Color(31, 30, 30);
    public static final Color COLOR_TEXTO_SUTIL = new Color(150, 150, 150);

    // --- FUENTES ---
    public static final Font FUENTE_TITULO_LOGIN = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font FUENTE_SUBTITULO_LOGIN = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FUENTE_ETIQUETA = new Font("Segoe UI", Font.BOLD, 14);

    // --- BORDES ---
    public static final Border BORDE_PANELES = BorderFactory.createEmptyBorder(15, 15, 15, 15);
    public static final Border BORDE_CAMPOS_LOGIN = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
    );

    /**
     * Crea un borde con título estilizado para los paneles.
     * @param title El texto que se mostrará en el título del borde.
     * @return un objeto {@link TitledBorder} con el estilo predefinido.
     */
    public static Border BORDE_TITULADO(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), title,
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                FUENTE_ETIQUETA, COLOR_SECUNDARIO
        );
    }

    /**
     * NUEVO MÉTODO: Aplica un estilo visual consistente a un botón.
     * <p>
     * Establece la fuente, el color de fondo, el borde y elige automáticamente el color
     * del texto (blanco o negro) para garantizar el máximo contraste y legibilidad.
     *
     * @param boton El JButton a estilizar.
     * @param colorDeFondo El color que se usará para el fondo del botón.
     */
    public static void estilizarBoton(JButton boton, Color colorDeFondo) {
        boton.setFont(UIUtils.FUENTE_ETIQUETA);
        boton.setBackground(colorDeFondo);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorDeFondo.darker(), 1),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)
        ));

        // --- LÓGICA DE CONTRASTE AUTOMÁTICO ---
        // Calcula la "luminosidad" del color de fondo.
        double luminosidad = (0.299 * colorDeFondo.getRed() + 0.587 * colorDeFondo.getGreen() + 0.114 * colorDeFondo.getBlue()) / 255;

        // Si el fondo es oscuro (luminosidad < 0.5), el texto es blanco. Si no, es negro.
        if (luminosidad < 0.5) {
            boton.setForeground(Color.WHITE);
        } else {
            boton.setForeground(Color.BLACK);
        }
    }
}