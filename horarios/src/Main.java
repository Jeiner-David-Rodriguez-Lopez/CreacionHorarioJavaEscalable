import gui.Ventana;
import datos.Escenarios;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        Runtime.getRuntime().addShutdownHook(new Thread(Escenarios::limpiarPersistencia));

        SwingUtilities.invokeLater(() -> new Ventana().setVisible(true));
    }
}
