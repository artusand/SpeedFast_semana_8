package dao;

import conexion.ConexionDB;
import model.Pedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    // CREATE: inserta un pedido nuevo
    public boolean create(Pedido pedido) throws SQLException {

        String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, pedido.getDireccion());
            sentencia.setString(2, pedido.getTipo());
            sentencia.setString(3, pedido.getEstado());

            return sentencia.executeUpdate() > 0;
        }
    }

    // READ: todos los pedidos, sin filtros
    public List<Pedido> readAll() throws SQLException {
        return readAll(null, null);
    }

    // READ con filtros opcionales: si estado o tipo es null, ese filtro no se aplica
    public List<Pedido> readAll(String estado, String tipo) throws SQLException {

        List<Pedido> pedidos = new ArrayList<>();

        String sql = "SELECT id, direccion, tipo, estado FROM pedidos WHERE 1=1";

        // Solo se agrega la condicion si el usuario eligio ese filtro
        if (estado != null) {
            sql += " AND estado = ?";
        }
        if (tipo != null) {
            sql += " AND tipo = ?";
        }
        sql += " ORDER BY id";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            // Posicion del siguiente signo ? que hay que rellenar
            int posicion = 1;
            if (estado != null) {
                sentencia.setString(posicion, estado);
                posicion++;
            }
            if (tipo != null) {
                sentencia.setString(posicion, tipo);
                posicion++;
            }

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    Pedido pedido = new Pedido();
                    pedido.setId(resultado.getInt("id"));
                    pedido.setDireccion(resultado.getString("direccion"));
                    pedido.setTipo(resultado.getString("tipo"));
                    pedido.setEstado(resultado.getString("estado"));
                    pedidos.add(pedido);
                }
            }
        }

        return pedidos;
    }

    // UPDATE: modifica direccion, tipo y estado segun el id
    public boolean update(Pedido pedido) throws SQLException {

        String sql = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, pedido.getDireccion());
            sentencia.setString(2, pedido.getTipo());
            sentencia.setString(3, pedido.getEstado());
            sentencia.setInt(4, pedido.getId());

            return sentencia.executeUpdate() > 0;
        }
    }

    // DELETE: elimina el pedido segun su id
    public boolean delete(int id) throws SQLException {

        String sql = "DELETE FROM pedidos WHERE id = ?";

        try (Connection conexion = ConexionDB.conectar();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            return sentencia.executeUpdate() > 0;
        }
    }
}