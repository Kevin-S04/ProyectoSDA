package Servicios;

import Servicios.ConexionBD;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase {@link ConexionBD}.
 *
 * <p>Esta clase verifica el correcto funcionamiento del establecimiento de conexiones
 * con la base de datos MySQL mediante el método {@link ConexionBD#getConnection()}.</p>
 */
public class ConexionBDTest {

    /**
     * Prueba que verifica una conexión exitosa a la base de datos.
     *
     * <p>Este test realiza las siguientes verificaciones:
     * <ol>
     *   <li>Carga explícitamente el driver JDBC de MySQL</li>
     *   <li>Establece una conexión usando {@link ConexionBD#getConnection()}</li>
     *   <li>Verifica que la conexión obtenida no sea nula</li>
     *   <li>Confirma que la conexión esté activa (no cerrada)</li>
     * </ol>
     *
     * <p><strong>Notas:</strong>
     * <ul>
     *   <li>Requiere que el servidor MySQL esté accesible con las credenciales configuradas</li>
     *   <li>La conexión se cierra automáticamente al salir del bloque try-with-resources</li>
     * </ul></p>
     */
    @Test
    void testConexionExitosa() {
        // Arrange: Preparar el objeto bajo prueba
        ConexionBD conexionBD = new ConexionBD();

        try {
            // Act/Assert: Cargar driver y verificar disponibilidad
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            fail("No se encontró el driver MySQL: " + e.getMessage());
        }

        try (Connection conn = conexionBD.getConnection()) {
            // Assert: Verificaciones sobre la conexión
            assertNotNull(conn, "La conexión no debe ser null");
            assertFalse(conn.isClosed(), "La conexión debe estar abierta");

            System.out.println("Conexión establecida exitosamente con: "
                    + conn.getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            fail("Error al conectar con la base de datos: " + e.getMessage());
        }
    }
}
