import javax.swing.*;
import java.util.List;

public class VentanaCoordinador {
    private FuenteAsignaturas fuente;

    public VentanaCoordinador(FuenteAsignaturas fuente) {
        this.fuente = fuente;
    }

    public void mostrar() {
        JFrame frame = new JFrame("Opciones del Coordinador");
        JButton verAsignaturasBtn = new JButton("Ver Asignaturas");

        verAsignaturasBtn.addActionListener(e -> {
            List<Asignatura> asignaturas = fuente.obtenerAsignaturas();
            StringBuilder sb = new StringBuilder("Lista de Asignaturas:\n");
            for (Asignatura a : asignaturas) {
                sb.append(a.getNombre()).append(" - Créditos: ").append(a.getCreditos()).append("\n");
            }
            JOptionPane.showMessageDialog(frame, sb.toString());
        });

        frame.setLayout(new java.awt.FlowLayout());
        frame.add(verAsignaturasBtn);
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

