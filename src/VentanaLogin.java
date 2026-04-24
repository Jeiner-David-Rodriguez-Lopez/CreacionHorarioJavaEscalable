import javax.swing.*;

public class VentanaLogin {
    public void mostrar() {
        JFrame frame = new JFrame("Login");
        JTextField campoId = new JTextField(10);
        JButton boton = new JButton("Ingresar");

        boton.addActionListener(e -> {
            String id = campoId.getText();
            Login login = new Login();
            Usuario persona = login.leerUsuario(id);

            if (persona instanceof Estudiante) {
                JOptionPane.showMessageDialog(frame, "Opciones: Ver horarios, Salir");
            } else if (persona instanceof Profesor) {
                JOptionPane.showMessageDialog(frame, "Opciones: Ver horarios, Ingresar sugerencias, Salir");
            } else if (persona instanceof Coordinador) {
                VentanaCoordinador ventanaCoordinador = new VentanaCoordinador((Coordinador) persona);
                ventanaCoordinador.mostrar();
            }
        });

        frame.setLayout(new java.awt.FlowLayout());
        frame.add(new JLabel("Ingrese su ID (1,2,3):"));
        frame.add(campoId);
        frame.add(boton);
        frame.setSize(300, 120);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
