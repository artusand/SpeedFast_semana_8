package ui;

import dao.RepartidorDAO;
import model.Repartidor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class PanelRepartidores extends JPanel {

    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    private JTextField txtNombre;
    private JButton btnGuardar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JTable tablaRepartidores;
    private DefaultTableModel modeloTabla;

    private int idSeleccionado = 0;

    public PanelRepartidores() {
        armarPantalla();
        cargarTabla();
    }

    private void armarPantalla() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFormulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del repartidor"));

        txtNombre = new JTextField(20);
        btnGuardar = new JButton("Guardar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombre);
        panelFormulario.add(btnGuardar);
        panelFormulario.add(btnEditar);
        panelFormulario.add(btnEliminar);
        panelFormulario.add(btnLimpiar);

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaRepartidores = new JTable(modeloTabla);
        tablaRepartidores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(panelFormulario, BorderLayout.NORTH);
        add(new JScrollPane(tablaRepartidores), BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        tablaRepartidores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });
    }

    public void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Repartidor> repartidores = repartidorDAO.readAll();
            for (Repartidor r : repartidores) {
                modeloTabla.addRow(new Object[]{r.getId(), r.getNombre()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la lista de repartidores: " + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarFila() {
        int fila = tablaRepartidores.getSelectedRow();
        if (fila >= 0) {
            idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
            txtNombre.setText(String.valueOf(modeloTabla.getValueAt(fila, 1)));
        }
    }

    private String validarNombre() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nombre del repartidor.",
                    "Validacion", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return null;
        }
        if (nombre.length() > 100) {
            JOptionPane.showMessageDialog(this, "El nombre no puede superar los 100 caracteres.",
                    "Validacion", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return nombre;
    }

    private void guardar() {
        if (idSeleccionado != 0) {
            JOptionPane.showMessageDialog(this,
                    "Hay un repartidor seleccionado. Presione Limpiar para registrar uno nuevo "
                    + "o Editar para modificarlo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = validarNombre();
        if (nombre == null) {
            return;
        }

        try {
            boolean guardado = repartidorDAO.create(new Repartidor(nombre));
            if (guardado) {
                JOptionPane.showMessageDialog(this, "Repartidor guardado correctamente.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el repartidor.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editar() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un repartidor de la tabla para editarlo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = validarNombre();
        if (nombre == null) {
            return;
        }

        try {
            boolean editado = repartidorDAO.update(new Repartidor(idSeleccionado, nombre));
            if (editado) {
                JOptionPane.showMessageDialog(this, "Repartidor actualizado correctamente.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el repartidor.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un repartidor de la tabla para eliminarlo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar al repartidor seleccionado?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean eliminado = repartidorDAO.delete(idSeleccionado);
            if (eliminado) {
                JOptionPane.showMessageDialog(this, "Repartidor eliminado correctamente.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el repartidor.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {

            if (e.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(this,
                        "No se puede eliminar: el repartidor tiene entregas asociadas. "
                        + "Elimine primero esas entregas.",
                        "No se puede eliminar", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiar() {
        txtNombre.setText("");
        idSeleccionado = 0;
        tablaRepartidores.clearSelection();
        txtNombre.requestFocus();
    }
}