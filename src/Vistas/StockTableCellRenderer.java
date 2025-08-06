package Vistas;

import Servicios.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Un renderer de celdas de tabla para la columna de 'Stock'.
 * <p>
 * Resalta visualmente las celdas cuyo valor de stock está por debajo de un umbral crítico,
 * facilitando la rápida identificación de productos que necesitan reabastecimiento.
 */
public class StockTableCellRenderer extends DefaultTableCellRenderer {

    /** El umbral por debajo del cual el stock se considera bajo. */
    private static final int UMBRAL_STOCK_BAJO = 10;

    /** Color de fondo para las celdas con stock normal. */
    private static final Color COLOR_NORMAL = Color.WHITE;

    /** Color de fondo para las celdas con stock bajo (un amarillo pálido para advertencia). */
    private static final Color COLOR_ADVERTENCIA_FONDO = new Color(255, 253, 230);

    /** Color de texto para las celdas con stock bajo. */
    private static final Color COLOR_ADVERTENCIA_TEXTO = new Color(153, 101, 21);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Llama al método de la superclase para obtener el componente base
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Centra el texto en la celda
        setHorizontalAlignment(SwingConstants.CENTER);

        if (value instanceof Integer) {
            int stock = (Integer) value;
            if (stock < UMBRAL_STOCK_BAJO) {
                // Si el stock es bajo, cambia los colores
                setBackground(COLOR_ADVERTENCIA_FONDO);
                setForeground(COLOR_ADVERTENCIA_TEXTO);
                setFont(getFont().deriveFont(Font.BOLD));
            } else {
                // Si no, usa los colores por defecto
                setBackground(COLOR_NORMAL);
                setForeground(UIUtils.COLOR_TEXTO_NORMAL);
                setFont(getFont().deriveFont(Font.PLAIN));
            }
        }

        // Maneja el color de fondo de la selección para que no se pierda el resaltado
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }

        return this;
    }
}