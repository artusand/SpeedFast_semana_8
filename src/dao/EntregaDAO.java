package dao;

import conexion.ConexionDB;
import model.Entrega;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EntregaDAO {

    // Consulta base: une entregas con pedidos y repartidores para mostrar datos legibles
    private static final String SELECT_BASE =
            "SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora, "
            + "p.direccion, r.nombre "
            + "FROM entregas e "
            + "JOIN pedidos p ON e.id_pedido = p.id "
            + "JOIN repartidores r ON e.id_repartidor = r.id ";

    // CREATE: inserta una entrega nueva
    public boolean create(Entrega entrega) throws SQLException {

        String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, entrega.getIdPedido());
            sentencia.setInt(2, entrega.getIdRepartidor());
            sentencia.setDate(3, entrega.getFecha());
            sentencia.setTime(4, entrega.getHora());

            return sentencia.executeUpdate() > 0;
        }
    }

    // READ: todas las entregas
    public List<Entrega> readAll() throws SQLException {
        return consultar(SELECT_BASE + "ORDER BY e.id", null);
    }

    // READ: solo las entregas de un pedido
    public List<Entrega> readPorPedido(int idPedido) throws SQLException {
        return consultar(SELECT_BASE + "WHERE e.id_pedido = ? ORDER BY e.id", idPedido);
    }

    // READ: solo las entregas de un repartidor
    public List<Entrega> readPorRepartidor(int idRepartidor) throws SQLException {
        return consultar(SELECT_BASE + "WHERE e.id_repartidor = ? ORDER BY e.id", idRepartidor);
    }

    // UPDATE: modifica pedido, repartidor, fecha y hora segun el id
    public boolean update(Entrega entrega) throws SQLException {

        String sql = "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, "
                + "fecha = ?, hora = ? WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, entrega.getIdPedido());
            sentencia.setInt(2, entrega.getIdRepartidor());
            sentencia.setDate(3, entrega.getFecha());
            sentencia.setTime(4, entrega.getHora());
            sentencia.setInt(5, entrega.getId());

            return sentencia.executeUpdate() > 0;
        }
    }

    // DELETE: elimina la entrega segun su id
    public boolean delete(int id) throws SQLException {

        String sql = "DELETE FROM entregas WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() > 0;
        }
    }

    // Metodo auxiliar: ejecuta una consulta y arma la lista de entregas.
    // Si parametro es null, la consulta no lleva filtro.
    private List<Entrega> consultar(String sql, Integer parametro) throws SQLException {

        List<Entrega> entregas = new ArrayList<>();

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            if (parametro != null) {
                sentencia.setInt(1, parametro);
            }

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    Entrega entrega = new Entrega();
                    entrega.setId(resultado.getInt("id"));
                    entrega.setIdPedido(resultado.getInt("id_pedido"));
                    entrega.setIdRepartidor(resultado.getInt("id_repartidor"));
                    entrega.setFecha(resultado.getDate("fecha"));
                    entrega.setHora(resultado.getTime("hora"));
                    entrega.setDireccionPedido(resultado.getString("direccion"));
                    entrega.setNombreRepartidor(resultado.getString("nombre"));
                    entregas.add(entrega);
                }
            }
        }

        return entregas;
    }
}