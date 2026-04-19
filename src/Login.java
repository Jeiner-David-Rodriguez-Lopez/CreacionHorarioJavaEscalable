import javax.swing.*;

public class Login {
    public Usuario leerUsuario(String id) {
        switch (id) {
            case "1":
                return new Estudiante();
            case "2":
                return new Profesor();
            case "3":
                FuenteAsignaturas fuente = new FuenteAsignaturasJSON();
                VentanaCoordinador ventana = new VentanaCoordinador(fuente);
                ventana.mostrar();

                return new Coordinador();
            default:
                JOptionPane.showMessageDialog(null, "ID inválido");
                return null;
        }
    }
}

