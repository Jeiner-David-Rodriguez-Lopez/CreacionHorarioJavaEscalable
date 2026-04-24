package gui;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Btn extends JButton {
    public Btn(String texto, Color color) {
        super(texto);
        setFont(Colores.SUBTIT);
        setBackground(color);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(true);

        Color original = color;
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { setBackground(original.brighter()); }
            public void mouseExited (MouseEvent e) { setBackground(original); }
        });
    }
}
