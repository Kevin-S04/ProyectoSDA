package Controladores;

import Controladores.AdminControlador;
import Vistas.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase {@link AdminControlador}.
 *
 * <p>Esta clase de pruebas verifica el comportamiento del controlador administrativo
 * utilizando una versión simple de la vista {@link Admin} para testing.</p>
 */
class AdminControladorTest {
    private Admin vista;
    private AdminControlador controlador;

    /**
     * Configura el entorno de prueba antes de cada test.
     *
     * <p>Inicializa una instancia simple de {@link Admin} y crea un nuevo
     * {@link AdminControlador} asociado a esta vista.</p>
     */
    @BeforeEach
    void setUp() {
        vista = new Admin(); // Necesitarás una versión simple de Admin para testing
        controlador = new AdminControlador(vista);
    }

    // --------------------------
    // Pruebas para gestión de usuarios
    // --------------------------

    /**
     * Prueba que la carga de usuarios no lance excepciones.
     *
     * <p>Verifica que el método {@link AdminControlador#cargarUsuarios()} pueda
     * ejecutarse sin generar errores.</p>
     */
    @Test
    void testCargarUsuarios() {
        assertDoesNotThrow(() -> controlador.cargarUsuarios(),
                "Debería cargar usuarios sin lanzar excepciones");
    }

    /**
     * Prueba la creación de usuario con datos válidos.
     *
     * <p>Verifica que el método {@link AdminControlador#crearUsuario(String, String, String, String, String, String)}
     * pueda ejecutarse correctamente cuando se proporcionan datos válidos.</p>
     */
    @Test
    void testCrearUsuarioConDatosValidos() {
        assertDoesNotThrow(() -> controlador.crearUsuario(
                "Test User",
                "test@test.com",
                "password123",
                "Ganadero",
                "123456789",
                "Test Address"
        ), "Debería crear usuario con datos válidos");
    }

    /**
     * Prueba la creación de usuario con datos vacíos.
     *
     * <p>Verifica que el método {@link AdminControlador#crearUsuario(String, String, String, String, String, String)}
     * maneje adecuadamente el caso cuando se proporcionan datos vacíos.</p>
     */
    @Test
    void testCrearUsuarioConDatosVacios() {
        assertDoesNotThrow(() -> controlador.crearUsuario(
                "", "", "", "", "", ""
        ), "Debería manejar datos vacíos sin lanzar excepciones");
    }

    // --------------------------
    // Pruebas para gestión de productos
    // --------------------------

    /**
     * Prueba que la carga de productos no lance excepciones.
     *
     * <p>Verifica que el método {@link AdminControlador#cargarProductos()} pueda
     * ejecutarse sin generar errores.</p>
     */
    @Test
    void testCargarProductos() {
        assertDoesNotThrow(() -> controlador.cargarProductos(),
                "Debería cargar productos sin lanzar excepciones");
    }

    /**
     * Prueba la creación de producto con datos válidos.
     *
     * <p>Verifica que el método {@link AdminControlador#crearProducto(String, String, String, String, double, String, int)}
     * pueda ejecutarse correctamente cuando se proporcionan datos válidos.</p>
     */
    @Test
    void testCrearProductoConDatosValidos() {
        assertDoesNotThrow(() -> controlador.crearProducto(
                "Test Product",
                "Alimento",
                "Cerdo",
                "Test Description",
                10.99,
                "Test Presentation",
                100
        ), "Debería crear producto con datos válidos");
    }

    // --------------------------
    // Pruebas para gestión de pedidos
    // --------------------------

    /**
     * Prueba que la carga de pedidos no lance excepciones.
     *
     * <p>Verifica que el método {@link AdminControlador#cargarPedidos()} pueda
     * ejecutarse sin generar errores.</p>
     */
    @Test
    void testCargarPedidos() {
        assertDoesNotThrow(() -> controlador.cargarPedidos(),
                "Debería cargar pedidos sin lanzar excepciones");
    }

    /**
     * Prueba la actualización de estado de un pedido existente.
     *
     * <p>Verifica que el método {@link AdminControlador#actualizarEstadoEnBD(int, String)}
     * pueda ejecutarse correctamente para un pedido existente.</p>
     *
     * <p><strong>Nota:</strong> Requiere un ID de pedido existente en la base de datos de prueba.</p>
     */
    @Test
    void testActualizarEstadoPedido() {
        // Necesitarías un ID de pedido existente en tu BD de prueba
        int pedidoIdExistente = 1; // Cambiar por un ID real de tu BD de prueba
        assertDoesNotThrow(() -> controlador.actualizarEstadoEnBD(
                pedidoIdExistente,
                "Procesado"
        ), "Debería actualizar estado de pedido existente");
    }

    // --------------------------
    // Prueba para historial de ventas
    // --------------------------

    /**
     * Prueba que la carga del historial de ventas no lance excepciones.
     *
     * <p>Verifica que el método {@link AdminControlador#cargarHistorialVentas()} pueda
     * ejecutarse sin generar errores.</p>
     */
    @Test
    void testCargarHistorialVentas() {
        assertDoesNotThrow(() -> controlador.cargarHistorialVentas(),
                "Debería cargar historial de ventas sin lanzar excepciones");
    }
}