import javax.swing.*;

public class Login {
    public Usuario leerUsuario(String id) {
        switch (id) {
            case "1":
                return new Estudiante();
            case "2":
                return new Profesor();
            case "3":
                FuenteAsignaturas fuente = new FuenteAsignaturasMemoria();
                return new Coordinador(fuente);
            default:
                JOptionPane.showMessageDialog(null, "ID invalido");
                return null;
        }
    }
}
