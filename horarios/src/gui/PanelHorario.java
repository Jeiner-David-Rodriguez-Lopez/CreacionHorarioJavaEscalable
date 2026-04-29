package gui;

import aulas.Aula;
import asignaturas.Asignatura;
import datos.Escenarios;
import datos.InfoAcademicaPersistencia;
import horario.Bloque;
import horario.EntradaHorario;
import horario.Semestre;
import usuarios.Profesor;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final boolean esVistaProfesor;
    private final boolean esVistaEstudiante;
    private JDesktopPane escritorio;
    private JInternalFrame frameCursos;

    public PanelHorario(List<Semestre> semestres, List<Aula> aulas,
                        String titulo, Color colorRol) {
        this.semestres = semestres;
        this.aulas     = aulas;
        this.esVistaProfesor = titulo != null && titulo.toLowerCase().contains("profesor");
        this.esVistaEstudiante = titulo != null && titulo.toLowerCase().contains("estudiante");

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

        if (esVistaEstudiante) {
            Btn cursos = new Btn("Cursos y aulas", colorRol);
            cursos.setPreferredSize(new Dimension(130, 28));
            cursos.addActionListener(e -> mostrarCursosEstudiante(colorRol));
            sel.add(cursos);
        }

        if (esVistaProfesor) {
            Btn informar = new Btn("Informar asignaturas", colorRol);
            informar.setPreferredSize(new Dimension(180, 28));
            informar.addActionListener(e -> informarAsignaturasProfesor());
            sel.add(informar);
        }
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

        escritorio = new JDesktopPane();
        escritorio.setBackground(Colores.FONDO);
        scroll.setBounds(0, 0, 900, 420);
        escritorio.add(scroll, JLayeredPane.DEFAULT_LAYER);
        escritorio.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                scroll.setBounds(0, 0, escritorio.getWidth(), escritorio.getHeight());
            }
        });
        add(esVistaEstudiante ? escritorio : scroll, BorderLayout.CENTER);

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
                textoAula(h),
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
                if (Color.WHITE.equals(getBackground())) {
                    setForeground(Color.BLACK);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
    }

    private String textoAula(EntradaHorario h) {
        if (h.getAula() == null) return "-";
        if (h.getAula().getNombre() != null && h.getAula().getNombre().startsWith("Virtual")) {
            return "Virtual";
        }
        return "Aula " + h.getAula().getNumero();
    }

    private void mostrarCursosEstudiante(Color colorRol) {
        int idx = combo.getSelectedIndex();
        Semestre semestre = semestres.get(idx);
        if (!semestre.isGenerado()) {
            lblEstado.setText("Horario no disponible. El coordinador debe generarlo primero.");
            lblEstado.setForeground(Colores.PELIGRO);
            return;
        }

        DefaultTableModel modeloCursos = new DefaultTableModel(new String[]{
                "Curso", "Profesor", "Cedula", "Tipo aula", "Aula", "Amenidades", "Cupo", "Registrados"
        }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        List<EntradaHorario> entradas = entradasUnicasPorCurso(semestre);
        for (EntradaHorario h : entradas) {
            Profesor profesor = h.getAsignatura().getProfesor();
            InfoAcademicaPersistencia.CursoInfo info =
                    InfoAcademicaPersistencia.infoCurso(semestre, h.getAsignatura());
            modeloCursos.addRow(new Object[]{
                    h.getAsignatura().getNombre(),
                    profesor != null ? profesor.getNombre() : "-",
                    profesor != null ? profesor.getCedula() : "-",
                    tipoAulaVisible(h.getAsignatura()),
                    textoAula(h),
                    InfoAcademicaPersistencia.amenidadesAula(h.getAula()),
                    info.getCupo(),
                    info.getRegistrados()
            });
        }

        JTable tablaCursos = new JTable(modeloCursos);
        tablaCursos.setBackground(Colores.PANEL);
        tablaCursos.setForeground(Colores.TEXTO);
        tablaCursos.setFont(Colores.PEQUENA);
        tablaCursos.setRowHeight(26);
        tablaCursos.setGridColor(Colores.BORDE);
        tablaCursos.setSelectionBackground(colorRol);
        tablaCursos.setSelectionForeground(Color.WHITE);
        tablaCursos.getTableHeader().setBackground(colorRol);
        tablaCursos.getTableHeader().setForeground(Color.WHITE);
        tablaCursos.getTableHeader().setFont(Colores.PEQUENA);
        tablaCursos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (sel) {
                    setBackground(colorRol);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(r % 2 == 0 ? Colores.PANEL : Colores.FONDO);
                    setForeground(Colores.TEXTO);
                }
                if (Color.WHITE.equals(getBackground())) {
                    setForeground(Color.BLACK);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        JLabel detalle = new JLabel("Seleccione un curso para ver el equipamiento del aula.");
        detalle.setFont(Colores.PEQUENA);
        detalle.setForeground(Colores.TEXTO_TENUE);
        detalle.setBorder(new EmptyBorder(4, 8, 4, 8));
        tablaCursos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarDetalleAula(tablaCursos, entradas, detalle);
        });

        Btn matricular = new Btn("Matricular", colorRol);
        matricular.setPreferredSize(new Dimension(120, 28));
        matricular.addActionListener(e -> matricularCurso(tablaCursos, modeloCursos, entradas, semestre, detalle));

        JPanel sur = new JPanel(new BorderLayout(8, 0));
        sur.setBackground(Colores.PANEL);
        sur.setBorder(new EmptyBorder(6, 6, 6, 6));
        sur.add(detalle, BorderLayout.CENTER);
        sur.add(matricular, BorderLayout.EAST);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Colores.PANEL);
        contenido.add(new JScrollPane(tablaCursos), BorderLayout.CENTER);
        contenido.add(sur, BorderLayout.SOUTH);

        if (frameCursos != null) frameCursos.dispose();
        frameCursos = new JInternalFrame("Cursos disponibles - Estudiante", true, true, true, true);
        frameCursos.setContentPane(contenido);
        frameCursos.setSize(Math.max(780, escritorio.getWidth() - 80), Math.max(300, escritorio.getHeight() - 80));
        frameCursos.setLocation(30, 30);
        frameCursos.setVisible(true);
        escritorio.add(frameCursos, JLayeredPane.PALETTE_LAYER);
        try {
            frameCursos.setSelected(true);
        } catch (java.beans.PropertyVetoException ignored) {
        }

        lblEstado.setText("Lista de cursos, aulas, cupos y matricula cargada para Semestre " + semestre.getNumero() + ".");
        lblEstado.setForeground(Colores.ACENTO2);
    }

    private List<EntradaHorario> entradasUnicasPorCurso(Semestre semestre) {
        Map<String, EntradaHorario> porCurso = new LinkedHashMap<>();
        for (EntradaHorario h : semestre.getHorario()) {
            if (h.getAsignatura() == null) continue;
            porCurso.putIfAbsent(normalizar(h.getAsignatura().getNombre()), h);
        }
        return new ArrayList<>(porCurso.values());
    }

    private void actualizarDetalleAula(JTable tablaCursos, List<EntradaHorario> entradas, JLabel detalle) {
        int fila = tablaCursos.getSelectedRow();
        if (fila < 0 || fila >= entradas.size()) return;
        EntradaHorario h = entradas.get(fila);
        detalle.setText(textoAula(h) + " porta: " + InfoAcademicaPersistencia.amenidadesAula(h.getAula()));
    }

    private void matricularCurso(JTable tablaCursos, DefaultTableModel modeloCursos,
                                 List<EntradaHorario> entradas, Semestre semestre, JLabel detalle) {
        int fila = tablaCursos.getSelectedRow();
        if (fila < 0 || fila >= entradas.size()) {
            detalle.setText("Seleccione un curso antes de matricular.");
            return;
        }
        EntradaHorario h = entradas.get(fila);
        boolean ok = InfoAcademicaPersistencia.matricular(semestre, h.getAsignatura());
        InfoAcademicaPersistencia.CursoInfo info = InfoAcademicaPersistencia.infoCurso(semestre, h.getAsignatura());
        modeloCursos.setValueAt(info.getRegistrados(), fila, 7);
        detalle.setText(ok
                ? "Matricula registrada en " + h.getAsignatura().getNombre() + "."
                : "El curso " + h.getAsignatura().getNombre() + " ya no tiene cupos disponibles.");
    }

    private String tipoAulaVisible(Asignatura asignatura) {
        if (asignatura == null) return "-";
        return "teoria".equalsIgnoreCase(asignatura.tipoAula()) ? "Teorico" : "Practico";
    }

    private void informarAsignaturasProfesor() {
        String cedula = JOptionPane.showInputDialog(this,
                "Ingrese su cedula:",
                "Informar asignaturas",
                JOptionPane.QUESTION_MESSAGE);
        if (cedula == null) return;

        Profesor profesor = Escenarios.profesorPorCedula(cedula.trim());
        if (profesor == null) {
            lblEstado.setText("Cedula no registrada en el sistema.");
            lblEstado.setForeground(Colores.PELIGRO);
            return;
        }

        String cursos = resumenCursosProfesor(profesor);
        JCheckBox[] dias = new JCheckBox[]{
                new JCheckBox("Lunes"), new JCheckBox("Martes"), new JCheckBox("Miercoles"),
                new JCheckBox("Jueves"), new JCheckBox("Viernes")
        };
        JCheckBox manana = new JCheckBox("Manana");
        JCheckBox tarde = new JCheckBox("Tarde");
        manana.setSelected(true);
        tarde.setSelected(true);
        for (JCheckBox dia : dias) dia.setSelected(true);

        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Profesor: " + profesor.getNombre() + " | Cedula: " + profesor.getCedula()));
        panel.add(new JLabel("Asignaturas actuales: " + cursos));
        panel.add(new JLabel("Dias en que desea impartir clases:"));
        for (JCheckBox dia : dias) panel.add(dia);
        panel.add(new JLabel("Jornada por dias seleccionados:"));
        panel.add(manana);
        panel.add(tarde);

        int op = JOptionPane.showConfirmDialog(this, panel, "Informar asignaturas", JOptionPane.OK_CANCEL_OPTION);
        if (op != JOptionPane.OK_OPTION) return;

        List<String> diasSeleccionados = new ArrayList<>();
        for (JCheckBox dia : dias) {
            if (dia.isSelected()) diasSeleccionados.add(dia.getText());
        }
        if (diasSeleccionados.isEmpty() || (!manana.isSelected() && !tarde.isSelected())) {
            lblEstado.setText("Debe seleccionar al menos un dia y una jornada.");
            lblEstado.setForeground(Colores.PELIGRO);
            return;
        }

        List<Bloque> disponibilidad = construirDisponibilidad(diasSeleccionados, manana.isSelected(), tarde.isSelected());
        boolean ok = Escenarios.actualizarDisponibilidadProfesor(profesor.getCedula(), disponibilidad);
        if (!ok) {
            lblEstado.setText("No se pudo guardar su disponibilidad.");
            lblEstado.setForeground(Colores.PELIGRO);
            return;
        }

        int conflictos = Escenarios.marcarConflictosPorDisponibilidad(profesor.getCedula());
        if (conflictos > 0) {
            lblEstado.setText("Sugerencia registrada. Se generaron " + conflictos
                    + " conflictos para que el coordinador los resuelva.");
            lblEstado.setForeground(Colores.PELIGRO);
        } else {
            lblEstado.setText("Disponibilidad registrada para " + profesor.getNombre() + " sin conflictos.");
            lblEstado.setForeground(Colores.ACENTO2);
        }
    }

    private String resumenCursosProfesor(Profesor profesor) {
        String cedula = normalizar(profesor.getCedula());
        Set<String> cursos = new LinkedHashSet<>();
        for (Semestre s : Escenarios.todosLosSemestres()) {
            for (Asignatura a : s.getAsignaturas()) {
                if (a.getProfesor() == null) continue;
                if (normalizar(a.getProfesor().getCedula()).equals(cedula)) {
                    cursos.add(a.getNombre());
                }
            }
        }
        return cursos.isEmpty() ? "sin asignaturas asignadas" : String.join(", ", cursos);
    }

    private List<Bloque> construirDisponibilidad(List<String> dias, boolean incluirManana, boolean incluirTarde) {
        List<Bloque> out = new ArrayList<>();
        Set<String> diasSet = dias.stream().map(this::normalizar).collect(Collectors.toSet());
        for (String dia : Bloque.DIAS) {
            if (!diasSet.contains(normalizar(dia))) continue;
            for (String[] slot : Bloque.SLOTS) {
                boolean esManana = slot[0].compareTo("12:00") < 0;
                if ((esManana && incluirManana) || (!esManana && incluirTarde)) {
                    out.add(new Bloque(dia, slot[0], slot[1]));
                }
            }
        }
        return out;
    }

    private String normalizar(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }
}
