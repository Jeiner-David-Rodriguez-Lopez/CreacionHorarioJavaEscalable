import javax.swing.*;
import java.util.List;

public class VentanaCoordinador {
    private final Coordinador coordinador;

    public VentanaCoordinador(Coordinador coordinador) {
        this.coordinador = coordinador;
    }

    public void mostrar() {
        JFrame frame = new JFrame("Opciones del Coordinador");

        JButton verAsignaturasBtn = new JButton("Ver Asignaturas");
        JButton verProfesoresBtn = new JButton("Ver Profesores");
        JButton editarCreditosBtn = new JButton("Editar Creditos Materia");

        verAsignaturasBtn.addActionListener(e -> {
            List<Asignatura> materias = coordinador.obtenerMaterias();
            StringBuilder sb = new StringBuilder("Lista de Materias:\n");
            for (Asignatura m : materias) {
                sb.append(m.getNombre())
                        .append(" - Creditos: ")
                        .append(m.getCreditos())
                        .append("\n");
            }
            JOptionPane.showMessageDialog(frame, sb.toString());
        });

        verProfesoresBtn.addActionListener(e -> {
            List<Profesor> profesores = coordinador.obtenerProfesores();
            StringBuilder sb = new StringBuilder("Lista de Profesores:\n");
            if (profesores.isEmpty()) {
                sb.append("No hay profesores registrados.");
            } else {
                for (Profesor p : profesores) {
                    String nombre = p.getNombre() == null ? "(sin nombre)" : p.getNombre();
                    sb.append(nombre).append("\n");
                }
            }
            JOptionPane.showMessageDialog(frame, sb.toString());
        });

        editarCreditosBtn.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog(frame, "Ingrese el nombre de la materia:");
            if (nombre == null || nombre.trim().isEmpty()) {
                return;
            }

            String creditosStr = JOptionPane.showInputDialog(frame, "Ingrese los nuevos creditos:");
            if (creditosStr == null || creditosStr.trim().isEmpty()) {
                return;
            }

            try {
                int nuevosCreditos = Integer.parseInt(creditosStr.trim());
                coordinador.actualizarCreditosMateria(nombre.trim(), nuevosCreditos);
                JOptionPane.showMessageDialog(frame, "Creditos actualizados para " + nombre.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "El valor de creditos debe ser numerico.");
            }
        });

        frame.setLayout(new java.awt.FlowLayout());
        frame.add(verAsignaturasBtn);
        frame.add(verProfesoresBtn);
        frame.add(editarCreditosBtn);

        frame.setSize(420, 280);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
