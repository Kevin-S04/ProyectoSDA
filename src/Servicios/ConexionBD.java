package Servicios;
import java.sql.Connection;
import  java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

        //Atributos par ala conexión
         private String usuario= "uiehom61yke3tgm7";
         private String   url= "jdbc:mysql://uiehom61yke3tgm7:8WKWXC5GkYlbsat1GG4m@bp9hdboswpcowkxrzabg-mysql.services.clever-cloud.com:3306/bp9hdboswpcowkxrzabg";
         private String password="8WKWXC5GkYlbsat1GG4m";

         public ConexionBD(){

             //Verifica que la conexión sea exitosa
             try{
                 Connection conn = DriverManager.getConnection(url,usuario,password);
                 System.out.println("Conexion exitosa a Clever Cloud");
                 conn.close();
             }catch (SQLException e){
                 System.out.println("Error al conectar: "+ e.getMessage());
             }
         }

}
