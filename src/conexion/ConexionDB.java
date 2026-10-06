package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    // Direccion de la base de datos (los parametros evitan errores comunes con MySQL 8)
    private static final String URL = "jdbc:mysql://localhost:3306/speedfast_db"
            + "?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true";

    // Usuario exclusivo del proyecto (creado con el script de la semana 8)
    private static final String USER = "speedfast_user";

    private static final String PASSWORD = "SpeedFast2026!";

    // Devuelve una conexion nueva cada vez que se llama
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}