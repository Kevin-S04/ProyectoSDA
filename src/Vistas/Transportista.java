package Vistas;

import Controladores.TransportistaControlador;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Vista para el rol de Transportista. Muestra los envíos asignados y permite
 * actualizar su estado.
 */
public class Transportista extends JFrame {

    private final TransportistaControlador controlador;
    private final int idTransportista;

    // Componentes de la interfaz
    private JTable enviosTable;
    private DefaultTableModel enviosTableModel;
    private JButton actualizarEstadoBtn;
    private JButton salirBtn;

    /**
     * Constructor para la vista Transportista.
     * @param idTransportista El ID del transportista que ha iniciado sesión.
     */
    public Transportista(int idTransportista) {
        this.idTransportista = idTransportista;
        this.controlador = new TransportistaControlador(this, idTransportista);

        // Configuración de la ventana principal
        setTitle("Panel de Transportista");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel superior para el botón de salir
        JPanel topPanel = new JPanel(new BorderLayout());
        salirBtn = new JButton("Salir al Login");
        topPanel.add(salirBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Panel principal para la tabla de envíos
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createTitledBorder("Mis Envíos Asignados"));

        enviosTableModel = new DefaultTableModel(new Object[]{"ID Envío", "ID Pedido", "Fecha Asignación", "Estado", "Dirección Entrega"}, 0);
        enviosTable = new JTable(enviosTableModel);
        JScrollPane enviosScrollPane = new JScrollPane(enviosTable);
        mainPanel.add(enviosScrollPane, BorderLayout.CENTER);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actualizarEstadoBtn = new JButton("Actualizar Estado de Envío");
        botonesPanel.add(actualizarEstadoBtn);
        mainPanel.add(botonesPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Asignación de listeners
        actualizarEstadoBtn.addActionListener(e -> controlador.actualizarEstadoEnvio());
        salirBtn.addActionListener(e -> controlador.cerrarSesion());

        // Al abrir la ventana, cargar los envíos
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent windowEvent) {
                controlador.cargarEnviosAsignados();
            }
        });
    }

    // Getters para el controlador
    /**
     * Obtiene el modelo de la tabla de envíos.
     * @return El modelo de datos de la tabla de envíos.
     */
    public DefaultTableModel getEnviosTableModel() {
        return enviosTableModel;
    }

    /**
     * Obtiene el ID del envío seleccionado en la tabla.
     * @return El ID del envío, o -1 si no hay selección.
     */
    public int getSelectedEnvioId() {
        int selectedRow = enviosTable.getSelectedRow();
        if (selectedRow != -1) {
            return (int) enviosTableModel.getValueAt(selectedRow, 0);
        }
        return -1;
    }
}