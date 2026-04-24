package gui;

import usuarios.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorListener;
import javax.swing.event.AncestorEvent;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelLogin extends JPanel {

    public interface Callback { void onLogin(Usuario u); }

    public PanelLogin(Callback callback) {
        setLayout(new GridBagLayout());
        setBackground(Colores.FONDO);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Colores.PANEL);
        card.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Titulo
        JLabel logo = new JLabel("Administra-Horarios", SwingConstants.CENTER);
        logo.setFont(Colores.TITULO);
        logo.setForeground(Colores.ACENTO);
        logo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel("TEC San Carlos  -  IC-2101", SwingConstants.CENTER);
        sub.setFont(Colores.PEQUENA);
        sub.setForeground(Colores.TEXTO_TENUE);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        // Label campo
        JLabel lbl = new JLabel("Ingrese su contrasena:");
        lbl.setFont(Colores.NORMAL);
        lbl.setForeground(Colores.TEXTO);
        lbl.setAlignmentX(CENTER_ALIGNMENT);

        // Campo contrasena
        JPasswordField campo = new JPasswordField(18);
        campo.setFont(Colores.NORMAL);
        campo.setBackground(new Color(40, 40, 60));
        campo.setForeground(Colores.TEXTO);
        campo.setCaretColor(Colores.TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        campo.setMaximumSize(new Dimension(260, 38));
        campo.setAlignmentX(CENTER_ALIGNMENT);

        // Mensaje error
        JLabel lblError = new JLabel(" ");
        lblError.setFont(Colores.PEQUENA);
        lblError.setForeground(Colores.PELIGRO);
        lblError.setAlignmentX(CENTER_ALIGNMENT);

        // Boton
        Btn btnIngresar = new Btn("Ingresar", Colores.ACENTO);
        btnIngresar.setMaximumSize(new Dimension(260, 42));
        btnIngresar.setAlignmentX(CENTER_ALIGNMENT);

        // Pistas
        JLabel pista = new JLabel("est123  /  prof456  /  coord789");
        pista.setFont(Colores.PEQUENA);
        pista.setForeground(Colores.TEXTO_TENUE);
        pista.setAlignmentX(CENTER_ALIGNMENT);

        // Accion login
        ActionListener accion = e -> {
            String pass = new String(campo.getPassword()).trim();
            Login login = new Login();
            Usuario u = login.autenticar(pass);
            if (u != null) {
                lblError.setText(" ");
                campo.setText("");
                callback.onLogin(u);
            } else {
                lblError.setText("Contrasena incorrecta. Intente de nuevo.");
                campo.setText("");
                campo.requestFocus();
            }
        };

        btnIngresar.addActionListener(accion);
        campo.addActionListener(accion); // Enter confirma

        // Armar card
        card.add(logo);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(30));
        card.add(lbl);
        card.add(Box.createVerticalStrut(8));
        card.add(campo);
        card.add(Box.createVerticalStrut(6));
        card.add(lblError);
        card.add(Box.createVerticalStrut(10));
        card.add(btnIngresar);
        card.add(Box.createVerticalStrut(16));
        card.add(pista);

        add(card);
        // Foco automático
        addAncestorListener(new AncestorListener() {
            /**
             * Called when the source or one of its ancestors is made invisible
             * either by setVisible(false) being called or by its being
             * removed from the component hierarchy.  The method is only called
             * if the source has actually become invisible.  For this to be true
             * at least one of its parents must by invisible or it is not in
             * a hierarchy rooted at a Window
             *
             * @param event
             *         an {@code AncestorEvent} signifying a change in an
             *         ancestor-component's display-status
             */
            @Override
            public void ancestorRemoved(AncestorEvent event) {

            }

            /**
             * Called when either the source or one of its ancestors is moved.
             *
             * @param event
             *         an {@code AncestorEvent} signifying a change in an
             *         ancestor-component's display-status
             */
            @Override
            public void ancestorMoved(AncestorEvent event) {

            }

            @Override
            public void ancestorAdded(AncestorEvent e) {
                campo.requestFocusInWindow();
            }

        });
    }
}
