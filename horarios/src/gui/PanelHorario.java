package gui;

import aulas.Aula;
import horario.EntradaHorario;
import horario.Semestre;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelHorario extends JPanel {

    private List<Semestre> semestres;
    private List<Aula>     aulas;
    private JComboBox<String> combo;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JLabel lblEstado;

    public PanelHorario(List<Semestre> semestres, List<Aula> aulas,
                        String titulo, Color colorRol) {
        this.semestres = semestres;
        this.aulas     = aulas;

        setLayout(new BorderLayout(8, 8));
        setBackground(Colores.FONDO);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Colores.PANEL);
        header.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel tit = new JLabel(titulo);
        tit.setFont(Colores.TITULO);
        tit.setForeground(colorRol);
        header.add(tit, BorderLayout.WEST);

        JPanel sel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        sel.setOpaque(false);

        JLabel lSem = new JLabel("Semestre:");
        lSem.setFont(Colores.NORMAL);
        lSem.setForeground(Colores.TEXTO_TENUE);
        sel.add(lSem);

        combo = new JComboBox<>();
        combo.setFont(Colores.NORMAL);
        combo.setBackground(Colores.FONDO);
        combo.setForeground(Colores.TEXTO);
        semestres.forEach(s -> combo.addItem("Semestre " + s.getNumero()));
        sel.add(combo);

        Btn ver = new Btn("Ver", colorRol);
        ver.setPreferredSize(new Dimension(80, 28));
        ver.addActionListener(e -> cargar());
        sel.add(ver);
        header.add(sel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Tabla
        String[] cols = {"Bloque", "Asignatura", "Tipo", "Profesor", "Aula", "Estado"};
        modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        estilizar(colorRol);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Colores.FONDO);
        scroll.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        add(scroll, BorderLayout.CENTER);

        // Estado
        lblEstado = new JLabel("Seleccione un semestre y presione Ver.");
        lblEstado.setFont(Colores.PEQUENA);
        lblEstado.setForeground(Colores.TEXTO_TENUE);
        lblEstado.setBorder(new EmptyBorder(4, 0, 0, 0));
        add(lblEstado, BorderLayout.SOUTH);
    }

    private void cargar() {
        modelo.setRowCount(0);
        int idx = combo.getSelectedIndex();
        Semestre s = semestres.get(idx);

        if (!s.isGenerado()) {
            lblEstado.setText("Horario no disponible. El coordinador debe generarlo primero.");
            lblEstado.setForeground(Colores.PELIGRO);
            return;
        }

        for (EntradaHorario h : s.getHorario()) {
            modelo.addRow(new Object[]{
                h.getBloque().toString(),
                h.getAsignatura().getNombre(),
                h.getAsignatura().tipo(),
                h.getAsignatura().getProfesor() != null
                    ? h.getAsignatura().getProfesor().getNombre() : "-",
                "Aula " + h.getAula().getNumero(),
                h.tieneConflicto() ? "[!] " + h.getConflicto() : "OK"
            });
        }
        lblEstado.setText("Semestre " + s.getNumero() + "  -  " + s.getHorario().size() + " bloques.");
        lblEstado.setForeground(Colores.ACENTO2);
    }

    private void estilizar(Color colorRol) {
        tabla.setBackground(Colores.PANEL);
        tabla.setForeground(Colores.TEXTO);
        tabla.setFont(Colores.NORMAL);
        tabla.setRowHeight(26);
        tabla.setGridColor(Colores.BORDE);
        tabla.setSelectionBackground(colorRol);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.getTableHeader().setBackground(colorRol);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(Colores.SUBTIT);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String estado = (String) t.getModel().getValueAt(r, 5);
                if (sel) {
                    setBackground(colorRol);
                    setForeground(Color.WHITE);
                } else if (estado.startsWith("[!]")) {
                    setBackground(new Color(80, 20, 20));
                    setForeground(Colores.PELIGRO);
                } else {
                    setBackground(r % 2 == 0 ? Colores.PANEL : Colores.FONDO);
                    setForeground(Colores.TEXTO);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
    }
}
