package ui;

import dao.PedidoDAO;
import model.Pedido;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class PanelPedidos extends JPanel {

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private static final String[] TIPOS = {"COMIDA", "ENCOMIENDA", "EXPRESS"};
    private static final String[] ESTADOS = {"PENDIENTE", "EN_REPARTO", "ENTREGADO"};

    private JTextField txtDireccion;
    private JComboBox<String> cmbTipo;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbFiltroEstado;
    private JComboBox<String> cmbFiltroTipo;
    private JButton btnGuardar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JTable tablaPedidos;
    private DefaultTableModel modeloTabla;

    private int idSeleccionado = 0;

    public PanelPedidos() {
        armarPantalla();
        cargarTabla();
    }

    private void armarPantalla() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtDireccion = new JTextField(25);
        cmbTipo = new JComboBox<>(TIPOS);
        cmbEstado = new JComboBox<>(ESTADOS);
        fila1.add(new JLabel("Dirección:"));
        fila1.add(txtDireccion);
        fila1.add(new JLabel("Tipo:"));
        fila1.add(cmbTipo);
        fila1.add(new JLabel("Estado:"));
        fila1.add(cmbEstado);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnGuardar = new JButton("Guardar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        fila2.add(btnGuardar);
        fila2.add(btnEditar);
        fila2.add(btnEliminar);
        fila2.add(btnLimpiar);

        JPanel panelFormulario = new JPanel(new GridLayout(2, 1));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del pedido"));
        panelFormulario.add(fila1);
        panelFormulario.add(fila2);

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtrar listado"));
        cmbFiltroEstado = new JComboBox<>(conTodos(ESTADOS));
        cmbFiltroTipo = new JComboBox<>(conTodos(TIPOS));
        panelFiltros.add(new JLabel("Estado:"));
        panelFiltros.add(cmbFiltroEstado);
        panelFiltros.add(new JLabel("Tipo:"));
        panelFiltros.add(cmbFiltroTipo);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.NORTH);
        panelSuperior.add(panelFiltros, BorderLayout.CENTER);

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Dirección", "Tipo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaPedidos = new JTable(modeloTabla);
        tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tablaPedidos), BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        cmbFiltroEstado.addActionListener(e -> cargarTabla());
        cmbFiltroTipo.addActionListener(e -> cargarTabla());

        tablaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });
    }

    private String[] conTodos(String[] opciones) {
        String[] resultado = new String[opciones.length + 1];
        resultado[0] = "TODOS";
        for (int i = 0; i < opciones.length; i++) {
            resultado[i + 1] = opciones[i];
        }
        return resultado;
    }

    public void cargarTabla() {
        String estado = cmbFiltroEstado.getSelectedItem().toString();
        String tipo = cmbFiltroTipo.getSelectedItem().toString();

        if (estado.equals("TODOS")) {
            estado = null;
        }
        if (tipo.equals("TODOS")) {
            tipo = null;
        }

        modeloTabla.setRowCount(0);
        try {
            List<Pedido> pedidos = pedidoDAO.readAll(estado, tipo);
            for (Pedido p : pedidos) {
                modeloTabla.addRow(new Object[]{p.getId(), p.getDireccion(), p.getTipo(), p.getEstado()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la lista de pedidos: " + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarFila() {
        int fila = tablaPedidos.getSelectedRow();
        if (fila >= 0) {
            idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
            txtDireccion.setText(String.valueOf(modeloTabla.getValueAt(fila, 1)));
            cmbTipo.setSelectedItem(modeloTabla.getValueAt(fila, 2));
            cmbEstado.setSelectedItem(modeloTabla.getValueAt(fila, 3));
        }
    }

    private String validarDireccion() {
        String direccion = txtDireccion.getText().trim();
        if (direccion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la dirección del pedido.",
                    "Validacion", JOptionPane.WARNING_MESSAGE);
            txtDireccion.requestFocus();
            return null;
        }
        if (direccion.length() > 100) {
            JOptionPane.showMessageDialog(this, "La dirección no puede superar los 100 caracteres.",
                    "Validacion", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return direccion;
    }

    private void guardar() {
        if (idSeleccionado != 0) {
            JOptionPane.showMessageDialog(this,
                    "Hay un pedido seleccionado. Presione Limpiar para registrar uno nuevo "
                    + "o Editar para modificarlo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String direccion = validarDireccion();
        if (direccion == null) {
            return;
        }

        String tipo = cmbTipo.getSelectedItem().toString();
        String estado = cmbEstado.getSelectedItem().toString();

        try {
            boolean guardado = pedidoDAO.create(new Pedido(direccion, tipo, estado));
            if (guardado) {
                JOptionPane.showMessageDialog(this, "Pedido guardado correctamente.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el pedido.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editar() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido de la tabla para editarlo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String direccion = validarDireccion();
        if (direccion == null) {
            return;
        }

        String tipo = cmbTipo.getSelectedItem().toString();
        String estado = cmbEstado.getSelectedItem().toString();

        try {
            boolean editado = pedidoDAO.update(new Pedido(idSeleccionado, direccion, tipo, estado));
            if (editado) {
                JOptionPane.showMessageDialog(this, "Pedido actualizado correctamente.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el pedido.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido de la tabla para eliminarlo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar el pedido seleccionado?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean eliminado = pedidoDAO.delete(idSeleccionado);
            if (eliminado) {
                JOptionPane.showMessageDialog(this, "Pedido eliminado correctamente.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el pedido.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(this,
                        "No se puede eliminar: el pedido tiene entregas asociadas. "
                        + "Elimine primero esas entregas.",
                        "No se puede eliminar", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        txtDireccion.setText("");
        cmbTipo.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        idSeleccionado = 0;
        tablaPedidos.clearSelection();
        txtDireccion.requestFocus();
    }
}