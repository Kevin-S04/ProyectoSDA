package Vistas;

import Controladores.TransportistaControlador;
import Servicios.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Vista rediseñada para el rol de Transportista, enfocada en la claridad y la acción rápida.
 * <p>
 * Muestra los envíos asignados en una tabla y presenta los detalles del envío seleccionado
 * en un panel dedicado para facilitar la consulta de direcciones e información del cliente.
 */
public class Transportista extends JFrame {

    private final TransportistaControlador controlador;
    private final int idTransportista;

    // --- Componentes Estructurales ---
    private JMenuBar menuBar;
    private JLabel statusBar;

    // --- Componentes de la Vista Principal ---
    private JTable enviosTable;
    private DefaultTableModel enviosTableModel;
    private JTextArea detallesEnvioArea; // Panel para mostrar detalles del envío seleccionado

    /**
     * Constructor para la vista Transportista.
     * @param idTransportista El ID del transportista que ha iniciado sesión.
     */
    public Transportista(int idTransportista) {
        this.idTransportista = idTransportista;
        this.controlador = new TransportistaControlador(this, idTransportista);

        setTitle("Panel de Transportista - ProyectoSDA");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        crearMenuBar();
        setJMenuBar(menuBar);

        // --- Panel Principal ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(UIUtils.BORDE_PANELES);
        mainPanel.setBackground(UIUtils.COLOR_FONDO);

        // --- Barra de Herramientas ---
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);

        JButton actualizarBtn = new JButton("Actualizar Estado de Envío");
        UIUtils.estilizarBoton(actualizarBtn, UIUtils.COLOR_PRINCIPAL);
        actualizarBtn.addActionListener(e -> controlador.actualizarEstadoEnvio());

        JButton refrescarBtn = new JButton("Refrescar Lista");
        UIUtils.estilizarBoton(refrescarBtn, UIUtils.COLOR_SECUNDARIO);
        refrescarBtn.addActionListener(e -> controlador.cargarEnviosAsignados());

        toolBar.add(actualizarBtn);
        toolBar.add(refrescarBtn);

        // --- Contenido (Tabla y Detalles) ---
        // Modelo y Tabla de Envíos
        enviosTableModel = new DefaultTableModel(new String[]{"ID Envío", "ID Pedido", "Fecha Asignación", "Estado", "Dirección Entrega"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enviosTable = new JTable(enviosTableModel);
        configurarTabla(enviosTable);

        // Listener para mostrar detalles cuando se selecciona una fila
        enviosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                controlador.mostrarDetallesEnvioSeleccionado();
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(enviosTable);
        tableScrollPane.setBorder(UIUtils.BORDE_TITULADO("Mis Envíos Asignados"));

        // Panel de Detalles del Envío
        detallesEnvioArea = new JTextArea();
        detallesEnvioArea.setFont(UIUtils.FUENTE_NORMAL.deriveFont(16f));
        detallesEnvioArea.setEditable(false);
        detallesEnvioArea.setLineWrap(true);
        detallesEnvioArea.setWrapStyleWord(true);
        detallesEnvioArea.setMargin(new Insets(10, 10, 10, 10));
        detallesEnvioArea.setText("Selecciona un envío de la tabla para ver sus detalles aquí.");

        JScrollPane detailsScrollPane = new JScrollPane(detallesEnvioArea);
        detailsScrollPane.setBorder(UIUtils.BORDE_TITULADO("Detalles del Envío Seleccionado"));

        // Split Pane para dividir la tabla y los detalles
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, detailsScrollPane);
        splitPane.setResizeWeight(0.65); // La tabla ocupa el 65% del espacio
        splitPane.setOpaque(false);

        mainPanel.add(toolBar, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // --- Barra de Estado ---
        statusBar = new JLabel(" Listo");
        statusBar.setFont(UIUtils.FUENTE_NORMAL);
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        // --- Ensamblado Final ---
        add(mainPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // --- Carga Inicial ---
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                controlador.cargarEnviosAsignados();
            }
        });
    }

    private void crearMenuBar() {
        menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setFont(UIUtils.FUENTE_NORMAL);

        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar Sesión");
        itemCerrarSesion.setFont(UIUtils.FUENTE_NORMAL);
        itemCerrarSesion.addActionListener(e -> controlador.cerrarSesion());

        menuArchivo.add(itemCerrarSesion);
        menuBar.add(menuArchivo);
    }

    private void configurarTabla(JTable table) {
        table.setFont(UIUtils.FUENTE_NORMAL);
        table.setRowHeight(30);
        table.getTableHeader().setFont(UIUtils.FUENTE_ETIQUETA);
        table.getTableHeader().setBackground(UIUtils.COLOR_CABECERA_TABLA);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    // --- Getters y Setters para el Controlador ---

    /**
     * @return El modelo de datos de la tabla de envíos.
     */
    public DefaultTableModel getEnviosTableModel() {
        return enviosTableModel;
    }

    /**
     * @return El ID del envío seleccionado en la tabla, o -1 si no hay selección.
     */
    public int getSelectedEnvioId() {
        int selectedRow = enviosTable.getSelectedRow();
        if (selectedRow != -1) {
            return (int) enviosTableModel.getValueAt(selectedRow, 0);
        }
        return -1;
    }

    /**
     * @return El área de texto donde se muestran los detalles del envío.
     */
    public JTextArea getDetallesEnvioArea() {
        return detallesEnvioArea;
    }
}