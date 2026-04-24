import javax.swing.*;

public class Main {
    public Usuario persona;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaLogin ventana = new VentanaLogin();
            ventana.mostrar();
        });
    }
}


