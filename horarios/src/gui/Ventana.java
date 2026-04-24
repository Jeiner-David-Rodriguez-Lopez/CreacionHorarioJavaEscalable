package gui;

import datos.Escenarios;
import aulas.Aula;
import horario.Semestre;
import usuarios.Coordinador;
import usuarios.Estudiante;
import usuarios.Profesor;
import usuarios.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class Ventana extends JFrame {

    private JPanel contenido;

    // Escenario activo (por defecto el 1)
    private List<Semestre> semestresActivos = Escenarios.escenario1();
    private List<Aula>     aulasActivas     = Escenarios.aulasEscenario1();

    public Ventana() {
        setTitle("Administra-Horarios - TEC San Carlos IC-2101");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Colores.FONDO);

        add(construirTopBar(), BorderLayout.NORTH);

        contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Colores.FONDO);
        add(contenido, BorderLayout.CENTER);

        mostrarLogin();
    }

    // ── Barra superior ───────────────────────────────────────────────
    private JPanel construirTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(10, 10, 20));
        bar.setBorder(new EmptyBorder(8, 16, 8, 16));

        JLabel logo = new JLabel("Administra-Horarios  |  IC-2101");
        logo.setFont(Colores.SUBTIT);
        logo.setForeground(Colores.ACENTO);
        bar.add(logo, BorderLayout.WEST);

        // Selector de escenario
        JPanel sel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        sel.setOpaque(false);

        JLabel lbl = new JLabel("Escenario:");
        lbl.setFont(Colores.PEQUENA);
        lbl.setForeground(Colores.TEXTO_TENUE);
        sel.add(lbl);

        String[] nombres = {
            "I y II  - Basicos",
            "III y IV - Intermedios",
            "V y VI  - Avanzados (con conflicto)"
        };
        JComboBox<String> cmbEsc = new JComboBox<>(nombres);
        cmbEsc.setFont(Colores.PEQUENA);
        cmbEsc.setBackground(Colores.PANEL);
        cmbEsc.setForeground(Colores.TEXTO);
        cmbEsc.addActionListener(e -> {
            switch (cmbEsc.getSelectedIndex()) {
                case 0: semestresActivos = Escenarios.escenario1(); aulasActivas = Escenarios.aulasEscenario1(); break;
                case 1: semestresActivos = Escenarios.escenario2(); aulasActivas = Escenarios.aulasEscenario2(); break;
                case 2: semestresActivos = Escenarios.escenario3(); aulasActivas = Escenarios.aulasEscenario3(); break;
            }
            mostrarLogin();
        });
        sel.add(cmbEsc);
        bar.add(sel, BorderLayout.EAST);
        return bar;
    }

    // ── Mostrar login ────────────────────────────────────────────────
    public void mostrarLogin() {
        contenido.removeAll();
        contenido.add(new PanelLogin(this::onLogin), BorderLayout.CENTER);
        contenido.revalidate();
        contenido.repaint();
    }

    // ── Callback de login ────────────────────────────────────────────
    private void onLogin(Usuario u) {
        if (u == null) return;
        contenido.removeAll();

        JPanel panelRol;
        if (u instanceof Estudiante) {
            panelRol = new PanelHorario(semestresActivos, aulasActivas,
                    "Ver Horario - Estudiante", Colores.ESTUDIANTE);
        } else if (u instanceof Profesor) {
            panelRol = new PanelHorario(semestresActivos, aulasActivas,
                    "Ver Horario - Profesor", Colores.PROFESOR);
        } else {
            panelRol = new PanelCoordinador((Coordinador) u, semestresActivos, aulasActivas);
        }

        contenido.add(construirBarraSesion(u.getNombre()), BorderLayout.NORTH);
        contenido.add(panelRol, BorderLayout.CENTER);
        contenido.revalidate();
        contenido.repaint();
    }

    // ── Barra de sesion con boton cerrar ─────────────────────────────
    private JPanel construirBarraSesion(String nombre) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(25, 25, 40));
        bar.setBorder(new EmptyBorder(5, 14, 5, 14));

        JLabel lbl = new JLabel("Sesion iniciada como: " + nombre);
        lbl.setFont(Colores.PEQUENA);
        lbl.setForeground(Colores.TEXTO_TENUE);
        bar.add(lbl, BorderLayout.WEST);

        Btn cerrar = new Btn("Cerrar sesion", new Color(60, 60, 80));
        cerrar.setFont(Colores.PEQUENA);
        cerrar.setPreferredSize(new Dimension(130, 24));
        cerrar.addActionListener(e -> mostrarLogin());
        bar.add(cerrar, BorderLayout.EAST);
        return bar;
    }
}
