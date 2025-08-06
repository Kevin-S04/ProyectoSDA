package Vistas;

import Modelos.Producto;
import Servicios.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Un renderer personalizado para mostrar productos en un JList de forma visualmente atractiva.
 * <p>
 * Cada item en la lista mostrará el nombre, la descripción, el precio y el stock del producto
 * en un formato organizado y estilizado.
 */
public class ProductoListRenderer extends JPanel implements ListCellRenderer<Producto> {

    private JLabel nombreLabel;
    private JLabel descripcionLabel;
    private JLabel precioLabel;
    private JLabel stockLabel;
    private JLabel especieLabel;

    public ProductoListRenderer() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // --- Panel de Contenido Principal ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        nombreLabel = new JLabel();
        nombreLabel.setFont(UIUtils.FUENTE_ETIQUETA.deriveFont(16f));
        nombreLabel.setForeground(UIUtils.COLOR_PRINCIPAL);

        especieLabel = new JLabel();
        especieLabel.setFont(UIUtils.FUENTE_NORMAL.deriveFont(Font.ITALIC));
        especieLabel.setForeground(UIUtils.COLOR_TEXTO_SUTIL);

        descripcionLabel = new JLabel();
        descripcionLabel.setFont(UIUtils.FUENTE_NORMAL);
        descripcionLabel.setForeground(UIUtils.COLOR_TEXTO_NORMAL);

        contentPanel.add(nombreLabel);
        contentPanel.add(especieLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(descripcionLabel);

        // --- Panel de Detalles (Precio y Stock) ---
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        detailsPanel.setOpaque(false);

        precioLabel = new JLabel();
        precioLabel.setFont(UIUtils.FUENTE_ETIQUETA.deriveFont(18f));
        precioLabel.setForeground(UIUtils.COLOR_ACCION_POSITIVA);
        precioLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        stockLabel = new JLabel();
        stockLabel.setFont(UIUtils.FUENTE_NORMAL);
        stockLabel.setForeground(UIUtils.COLOR_TEXTO_SUTIL);
        stockLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        detailsPanel.add(precioLabel);
        detailsPanel.add(stockLabel);

        add(contentPanel, BorderLayout.CENTER);
        add(detailsPanel, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Producto> list, Producto producto, int index, boolean isSelected, boolean cellHasFocus) {
        // Asignar los valores del producto a las etiquetas
        nombreLabel.setText(producto.getNombre());
        especieLabel.setText("Para: " + producto.getEspecie());
        descripcionLabel.setText("<html><p style='width:300px'>" + producto.getDescripcion() + "</p></html>");

        precioLabel.setText(String.format("$%.2f", producto.getPrecioUnitario()));
        stockLabel.setText("Stock: " + producto.getStock());

        // Cambiar colores de fondo al seleccionar un item
        if (isSelected) {
            setBackground(UIUtils.COLOR_CABECERA_TABLA);
        } else {
            // Usa el color de fondo principal para los items no seleccionados
            setBackground(Color.WHITE);
        }

        return this;
    }
}