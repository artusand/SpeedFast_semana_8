package dao;

import conexion.ConexionDB;
import model.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepartidorDAO {

    // CREATE: inserta un repartidor nuevo.
    // Devuelve true solo si realmente se inserto una fila.
    public boolean create(Repartidor repartidor) throws SQLException {

        String sql = "INSERT INTO repartidores (nombre) VALUES (?)";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, repartidor.getNombre());

            // executeUpdate devuelve cuantas filas fueron afectadas
            int filas = sentencia.executeUpdate();
            return filas > 0;
        }
    }

    // READ: devuelve la lista de todos los repartidores
    public List<Repartidor> readAll() throws SQLException {

        List<Repartidor> repartidores = new ArrayList<>();

        String sql = "SELECT id, nombre FROM repartidores ORDER BY id";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                Repartidor repartidor = new Repartidor();
                repartidor.setId(resultado.getInt("id"));
                repartidor.setNombre(resultado.getString("nombre"));
                repartidores.add(repartidor);
            }
        }

        return repartidores;
    }

    // UPDATE: cambia el nombre del repartidor segun su id
    public boolean update(Repartidor repartidor) throws SQLException {

        String sql = "UPDATE repartidores SET nombre = ? WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, repartidor.getNombre());
            sentencia.setInt(2, repartidor.getId());

            return sentencia.executeUpdate() > 0;
        }
    }

    // DELETE: elimina el repartidor segun su id
    public boolean delete(int id) throws SQLException {

        String sql = "DELETE FROM repartidores WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() > 0;
        }
    }
}