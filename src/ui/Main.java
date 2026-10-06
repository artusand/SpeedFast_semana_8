package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Main extends JFrame {

    private JTabbedPane pestanas;

    public Main() {
        setTitle("SpeedFast - Gestión de despachos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);   // centra la ventana en la pantalla
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("SpeedFast", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        add(titulo, BorderLayout.NORTH);

        PanelRepartidores panelRepartidores = new PanelRepartidores();
        PanelPedidos panelPedidos = new PanelPedidos();
        PanelEntregas panelEntregas = new PanelEntregas();

        pestanas = new JTabbedPane();
        pestanas.addTab("Repartidores", panelRepartidores);
        pestanas.addTab("Pedidos", panelPedidos);
        pestanas.addTab("Entregas", panelEntregas);
        add(pestanas, BorderLayout.CENTER);


        pestanas.addChangeListener(e -> {
            int indice = pestanas.getSelectedIndex();
            if (indice == 0) {
                panelRepartidores.cargarTabla();
            } else if (indice == 1) {
                panelPedidos.cargarTabla();
            } else if (indice == 2) {
                panelEntregas.actualizar();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}