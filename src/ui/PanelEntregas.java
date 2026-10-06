package ui;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import model.Entrega;
import model.Pedido;
import model.Repartidor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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

public class PanelEntregas extends JPanel {

    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    private JComboBox<ItemCombo> cmbPedido;
    private JComboBox<ItemCombo> cmbRepartidor;
    private JComboBox<ItemCombo> cmbFiltroPedido;
    private JComboBox<ItemCombo> cmbFiltroRepartidor;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JButton btnGuardar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JTable tablaEntregas;
    private DefaultTableModel modeloTabla;

    private List<Entrega> entregasMostradas = new ArrayList<>();

    private int idSeleccionado = 0;

    private boolean cargando = false;

    public PanelEntregas() {
        armarPantalla();
        actualizar();
    }

    private void armarPantalla() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        cmbPedido = new JComboBox<>();
        cmbRepartidor = new JComboBox<>();
        fila1.add(new JLabel("Pedido:"));
        fila1.add(cmbPedido);
        fila1.add(new JLabel("Repartidor:"));
        fila1.add(cmbRepartidor);

        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtFecha = new JTextField(10);
        txtHora = new JTextField(8);
        fila2.add(new JLabel("Fecha (AAAA-MM-DD):"));
        fila2.add(txtFecha);
        fila2.add(new JLabel("Hora (HH:MM:SS):"));
        fila2.add(txtHora);

        JPanel fila3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnGuardar = new JButton("Guardar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        fila3.add(btnGuardar);
        fila3.add(btnEditar);
        fila3.add(btnEliminar);
        fila3.add(btnLimpiar);

        JPanel panelFormulario = new JPanel(new GridLayout(3, 1));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos de la entrega"));
        panelFormulario.add(fila1);
        panelFormulario.add(fila2);
        panelFormulario.add(fila3);

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtrar listado"));
        cmbFiltroPedido = new JComboBox<>();
        cmbFiltroRepartidor = new JComboBox<>();
        panelFiltros.add(new JLabel("Por pedido:"));
        panelFiltros.add(cmbFiltroPedido);
        panelFiltros.add(new JLabel("Por repartidor:"));
        panelFiltros.add(cmbFiltroRepartidor);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelFormulario, BorderLayout.NORTH);
        panelSuperior.add(panelFiltros, BorderLayout.CENTER);

        modeloTabla = new DefaultTableModel(
                new String[]{"ID", "Pedido", "Repartidor", "Fecha", "Hora"}, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tablaEntregas = new JTable(modeloTabla);
        tablaEntregas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tablaEntregas), BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardar());
        btnEditar.addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        cmbFiltroPedido.addActionListener(e -> {
            if (!cargando) {
                cargarTabla();
            }
        });
        cmbFiltroRepartidor.addActionListener(e -> {
            if (!cargando) {
                cargarTabla();
            }
        });

        tablaEntregas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarFila();
            }
        });

        txtFecha.setText(LocalDate.now().toString());
    }

    public void actualizar() {
        cargarCombos();
        cargarTabla();
    }

    private int idElegido(JComboBox<ItemCombo> combo) {
        ItemCombo item = (ItemCombo) combo.getSelectedItem();
        if (item == null) {
            return 0;
        }
        return item.getId();
    }

    private void seleccionarEnCombo(JComboBox<ItemCombo> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getId() == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void cargarCombos() {
        cargando = true;
        try {
            List<Pedido> pedidos = pedidoDAO.readAll();
            List<Repartidor> repartidores = repartidorDAO.readAll();

            int pedidoElegido = idElegido(cmbPedido);
            int repartidorElegido = idElegido(cmbRepartidor);
            int filtroPedido = idElegido(cmbFiltroPedido);
            int filtroRepartidor = idElegido(cmbFiltroRepartidor);

            cmbPedido.removeAllItems();
            cmbRepartidor.removeAllItems();
            cmbFiltroPedido.removeAllItems();
            cmbFiltroRepartidor.removeAllItems();

            cmbFiltroPedido.addItem(new ItemCombo(0, "TODOS"));
            cmbFiltroRepartidor.addItem(new ItemCombo(0, "TODOS"));

            for (Pedido p : pedidos) {
                String texto = p.getId() + " - " + p.getDireccion();
                cmbPedido.addItem(new ItemCombo(p.getId(), texto));
                cmbFiltroPedido.addItem(new ItemCombo(p.getId(), texto));
            }
            for (Repartidor r : repartidores) {
                String texto = r.getId() + " - " + r.getNombre();
                cmbRepartidor.addItem(new ItemCombo(r.getId(), texto));
                cmbFiltroRepartidor.addItem(new ItemCombo(r.getId(), texto));
            }

            seleccionarEnCombo(cmbPedido, pedidoElegido);
            seleccionarEnCombo(cmbRepartidor, repartidorElegido);
            seleccionarEnCombo(cmbFiltroPedido, filtroPedido);
            seleccionarEnCombo(cmbFiltroRepartidor, filtroRepartidor);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los pedidos y repartidores: " + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        } finally {
            cargando = false;
        }
    }

    public void cargarTabla() {
        int idPedido = idElegido(cmbFiltroPedido);
        int idRepartidor = idElegido(cmbFiltroRepartidor);

        modeloTabla.setRowCount(0);
        entregasMostradas = new ArrayList<>();
        idSeleccionado = 0;

        try {
            List<Entrega> lista;
            if (idPedido != 0) {
                lista = entregaDAO.readPorPedido(idPedido);
                // Si tambien hay filtro de repartidor, se descartan las demas
                if (idRepartidor != 0) {
                    lista.removeIf(entrega -> entrega.getIdRepartidor() != idRepartidor);
                }
            } else if (idRepartidor != 0) {
                lista = entregaDAO.readPorRepartidor(idRepartidor);
            } else {
                lista = entregaDAO.readAll();
            }

            entregasMostradas = lista;
            for (Entrega e : lista) {
                modeloTabla.addRow(new Object[]{
                    e.getId(),
                    e.getIdPedido() + " - " + e.getDireccionPedido(),
                    e.getNombreRepartidor(),
                    e.getFecha(),
                    e.getHora()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la lista de entregas: " + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarFila() {
        int fila = tablaEntregas.getSelectedRow();
        if (fila >= 0 && fila < entregasMostradas.size()) {
            Entrega entrega = entregasMostradas.get(fila);
            idSeleccionado = entrega.getId();
            seleccionarEnCombo(cmbPedido, entrega.getIdPedido());
            seleccionarEnCombo(cmbRepartidor, entrega.getIdRepartidor());
            txtFecha.setText(entrega.getFecha().toString());
            txtHora.setText(entrega.getHora().toString());
        }
    }

    private Entrega leerFormulario() {
        int idPedido = idElegido(cmbPedido);
        int idRepartidor = idElegido(cmbRepartidor);

        if (idPedido == 0 || idRepartidor == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debe existir al menos un pedido y un repartidor. Regístrelos en sus pestañas.",
                    "Validacion", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String fechaTexto = txtFecha.getText().trim();
        String horaTexto = txtHora.getText().trim();

        if (fechaTexto.isEmpty() || horaTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar la fecha y la hora.",
                    "Validacion", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        try {
            // LocalDate y LocalTime revisan que la fecha y la hora existan de verdad
            Date fecha = Date.valueOf(LocalDate.parse(fechaTexto));
            Time hora = Time.valueOf(LocalTime.parse(horaTexto));
            return new Entrega(idPedido, idRepartidor, fecha, hora);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Formato incorrecto.\nFecha: AAAA-MM-DD (ejemplo 2026-10-05)\n"
                    + "Hora: HH:MM:SS (ejemplo 14:30:00)",
                    "Validacion", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void guardar() {
        if (idSeleccionado != 0) {
            JOptionPane.showMessageDialog(this,
                    "Hay una entrega seleccionada. Presione Limpiar para registrar una nueva "
                    + "o Editar para modificarla.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Entrega entrega = leerFormulario();
        if (entrega == null) {
            return;
        }

        try {
            boolean guardado = entregaDAO.create(entrega);
            if (guardado) {
                JOptionPane.showMessageDialog(this, "Entrega registrada correctamente.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar la entrega.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editar() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una entrega de la tabla para editarla.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Entrega entrega = leerFormulario();
        if (entrega == null) {
            return;
        }
        entrega.setId(idSeleccionado);

        try {
            boolean editado = entregaDAO.update(entrega);
            if (editado) {
                JOptionPane.showMessageDialog(this, "Entrega actualizada correctamente.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar la entrega.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        if (idSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una entrega de la tabla para eliminarla.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar la entrega seleccionada?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean eliminado = entregaDAO.delete(idSeleccionado);
            if (eliminado) {
                JOptionPane.showMessageDialog(this, "Entrega eliminada correctamente.");
                limpiar();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar la entrega.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar() {
        txtFecha.setText(LocalDate.now().toString());
        txtHora.setText("");
        idSeleccionado = 0;
        tablaEntregas.clearSelection();
    }
}