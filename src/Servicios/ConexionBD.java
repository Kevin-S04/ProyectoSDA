package Servicios;

import java.sql.Connection;
import  java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para manejar la conexión con la base de datos de Clever Cloud.
 * Este servicio se encarga exclusivamente de proporcionar la conexión.
 */
public class ConexionBD {

    /** Credenciales para la conexión a la base de datos. */
    private final String usuario= "uiehom61yke3tgm7";
    private final String url= "jdbc:mysql://bp9hdboswpcowkxrzabg-mysql.services.clever-cloud.com:3306/bp9hdboswpcowkxrzabg";
    private final String password="8WKWXC5GkYlbsat1GG4m";

    /**
     * Constructor de la clase. Intenta establecer una conexión para verificar su éxito.
     */
    public ConexionBD(){

        try {
            // Carga explícita del driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(url, usuario, password);
            System.out.println("Conexion exitosa a Clever Cloud");
            conn.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Driver MySQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
    }

    /**
     * Obtiene y retorna una nueva conexión a la base de datos.
     * @return Un objeto Connection que representa la conexión a la base de datos.
     * @throws SQLException Si ocurre un error de conexión.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }
}