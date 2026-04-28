package gui;

import aulas.Aula;
import asignaturas.Asignatura;
import datos.Escenarios;
import horario.Bloque;
import horario.EntradaHorario;
import horario.GeneradorHorarios;
import horario.Semestre;
import usuarios.Coordinador;
import usuarios.Profesor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.text.Normalizer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PanelCoordinador extends JPanel {

    private Coordinador coord;
    private List<Semestre> semestres;
    private List<Aula>     aulas;

    private JComboBox<String> combo;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JLabel lblInfo;
    private JTextArea areaConflictos;
    private JTextArea areaMaterias;
    private JTextArea areaProfesores;

    public PanelCoordinador(Coordinador coord, List<Semestre> semestres, List<Aula> aulas) {
        this.coord     = coord;
        this.semestres = semestres;
        this.aulas     = aulas;

        setLayout(new BorderLayout(8, 8));
        setBackground(Colores.FONDO);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        add(construirHeader(),  BorderLayout.NORTH);
        add(construirCentro(),  BorderLayout.CENTER);
        add(construirSur(),     BorderLayout.SOUTH);
    }

    // ── Header con controles ──────────────────────────────────────────
    private JPanel construirHeader() {
        JPanel h = new JPanel(new BorderLayout(10, 0));
        h.setBackground(Colores.PANEL);
        h.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel tit = new JLabel("Panel Coordinador");
        tit.setFont(Colores.TITULO);
        tit.setForeground(Colores.COORDINADOR);
        h.add(tit, BorderLayout.WEST);

        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        ctrl.setOpaque(false);

        JLabel lSem = new JLabel("Semestre:");
        lSem.setFont(Colores.NORMAL);
        lSem.setForeground(Colores.TEXTO_TENUE);
        ctrl.add(lSem);

        combo = new JComboBox<>();
        combo.setFont(Colores.NORMAL);
        combo.setBackground(Colores.FONDO);
        combo.setForeground(Colores.TEXTO);
        semestres.forEach(s -> combo.addItem("Semestre " + s.getNumero()));
        ctrl.add(combo);

        Btn bVer      = new Btn("Ver",          Colores.ACENTO);
        Btn bGenerar  = new Btn("Generar",       Colores.ACENTO2);
        Btn bConfl    = new Btn("Conflictos",    Colores.PELIGRO);
        Btn bMaterias = new Btn("Materias",      Colores.PROFESOR);
        Btn bProfs    = new Btn("Profesores",    Colores.ESTUDIANTE);
        Btn bBuscarProf = new Btn("Buscar Prof", Colores.COORDINADOR);
        Btn bResolver = new Btn("Resolver", Colores.ACENTO2);

        for (Btn b : new Btn[]{bVer, bGenerar, bConfl, bMaterias, bProfs, bBuscarProf, bResolver}) {
            b.setPreferredSize(new Dimension(100, 28));
            ctrl.add(b);
        }

        bVer.addActionListener(e      -> verHorario());
        bGenerar.addActionListener(e  -> generarHorario());
        bConfl.addActionListener(e    -> verConflictos());
        bMaterias.addActionListener(e -> verMaterias());
        bProfs.addActionListener(e    -> verProfesores());
        bBuscarProf.addActionListener(e -> buscarHorarioProfesor());
        bResolver.addActionListener(e -> resolverConflictoSeleccionado());

        h.add(ctrl, BorderLayout.EAST);
        return h;
    }

    // ── Centro: tabla + paneles de info ─────────────────────────────
    private JComponent construirCentro() {
        // Tabla horario
        String[] cols = {"Bloque","Asignatura","Tipo","Profesor","Aula","Estado"};
        modelo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        estilizarTabla();
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.getViewport().setBackground(Colores.FONDO);
        scrollTabla.setBorder(BorderFactory.createLineBorder(Colores.BORDE));

        // Panel de texto inferior (conflictos / materias / profesores)
        JPanel panelTexto = new JPanel(new GridLayout(1, 3, 6, 0));
        panelTexto.setBackground(Colores.FONDO);
        panelTexto.setPreferredSize(new Dimension(0, 180));

        areaConflictos = crearArea(Colores.PELIGRO, new Color(40,15,15));
        areaMaterias   = crearArea(Colores.PROFESOR, Colores.PANEL);
        areaProfesores = crearArea(Colores.ESTUDIANTE, Colores.PANEL);

        panelTexto.add(envolverArea("Conflictos", areaConflictos, Colores.PELIGRO));
        panelTexto.add(envolverArea("Materias",   areaMaterias,   Colores.PROFESOR));
        panelTexto.add(envolverArea("Profesores", areaProfesores, Colores.ESTUDIANTE));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTabla, panelTexto);
        split.setResizeWeight(0.65);
        split.setDividerSize(5);
        split.setBackground(Colores.FONDO);
        return split;
    }

    private JTextArea crearArea(Color fg, Color bg) {
        JTextArea a = new JTextArea("(vacio)");
        a.setFont(Colores.MONO);
        a.setBackground(bg);
        a.setForeground(fg);
        a.setEditable(false);
        a.setBorder(new EmptyBorder(6,6,6,6));
        return a;
    }

    private JPanel envolverArea(String titulo, JTextArea area, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Colores.PANEL);
        JLabel lbl = new JLabel("  " + titulo);
        lbl.setFont(Colores.SUBTIT);
        lbl.setForeground(color);
        lbl.setBorder(new EmptyBorder(4,4,4,4));
        p.add(lbl, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        p.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        return p;
    }

    // ── Sur: barra de estado ─────────────────────────────────────────
    private JPanel construirSur() {
        JPanel sur = new JPanel(new BorderLayout());
        sur.setBackground(Colores.FONDO);
        lblInfo = new JLabel("Seleccione semestre y use los botones.");
        lblInfo.setFont(Colores.PEQUENA);
        lblInfo.setForeground(Colores.TEXTO_TENUE);
        sur.add(lblInfo, BorderLayout.WEST);
        return sur;
    }

    // ── Acciones ─────────────────────────────────────────────────────
    private void verHorario() {
        modelo.setRowCount(0);
        Semestre s = actual();
        if (!s.isGenerado()) {
            lblInfo.setText("Sin horario. Use 'Generar' primero.");
            lblInfo.setForeground(Colores.PELIGRO);
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
        lblInfo.setText("Semestre " + s.getNumero() + "  -  " + s.getHorario().size() + " bloques.");
        lblInfo.setForeground(Colores.ACENTO2);
    }

    private void generarHorario() {
        int idxActual = Math.max(0, combo.getSelectedIndex());
        int inicioLote = (idxActual / 2) * 2;
        int finLoteExclusivo = Math.min(inicioLote + 2, semestres.size());
        List<Semestre> lote = semestres.subList(inicioLote, finLoteExclusivo);

        GeneradorHorarios.generarGlobal(lote, aulas, Escenarios.todosLosSemestres());
        Escenarios.guardarHorariosGenerados();
        int desde = lote.stream().mapToInt(Semestre::getNumero).min().orElse(0);
        int hasta = lote.stream().mapToInt(Semestre::getNumero).max().orElse(0);
        if (desde > 0 && hasta > 0) {
            lblInfo.setText("Generacion por lote: Semestres " + desde + "-" + hasta + " guardados.");
        } else {
            lblInfo.setText("Generacion por lote completada y guardada.");
        }
        lblInfo.setForeground(Colores.ACENTO2);
        verHorario();
        verConflictos();
    }

    private void verConflictos() {
        Semestre s = actual();
        if (!s.isGenerado()) {
            areaConflictos.setText("Genere el horario primero.");
            return;
        }
        List<EntradaHorario> conf = s.getHorario().stream()
                .filter(EntradaHorario::tieneConflicto)
                .collect(Collectors.toList());
        if (conf.isEmpty()) {
            areaConflictos.setText("Sin conflictos en Semestre " + s.getNumero());
        } else {
            StringBuilder sb = new StringBuilder("Semestre " + s.getNumero() + ":\n\n");
            for (EntradaHorario h : conf) {
                sb.append("• ").append(h.getAsignatura().getNombre())
                  .append("\n  Bloque: ").append(h.getBloque())
                  .append("\n  Razon:  ").append(h.getConflicto())
                  .append("\n\n");
            }
            areaConflictos.setText(sb.toString());
        }
    }

    private void verMaterias() {
        Semestre s = actual();
        StringBuilder sb = new StringBuilder();
        s.getAsignaturas().forEach(m ->
            sb.append(String.format("%-26s %d cr.\n", m.getNombre(), m.getCreditos())));
        if (sb.length() == 0) {
            sb.append("Sin materias en Semestre ").append(s.getNumero());
        }
        areaMaterias.setText(sb.toString());
        areaMaterias.setCaretPosition(0);
        lblInfo.setText("Materias filtradas por Semestre " + s.getNumero());
        lblInfo.setForeground(Colores.PROFESOR);
    }

    private void verProfesores() {
        Semestre s = actual();
        Set<String> profesores = new LinkedHashSet<>();
        s.getAsignaturas().forEach(a -> {
            if (a.getProfesor() != null) {
                profesores.add(String.format("%-22s %s",
                        a.getProfesor().getNombre(),
                        a.getProfesor().getDepartamento() + " | Ced: " + a.getProfesor().getCedula()));
            }
        });

        StringBuilder sb = new StringBuilder();
        if (profesores.isEmpty()) {
            sb.append("Sin profesores asignados en Semestre ").append(s.getNumero());
        } else {
            profesores.forEach(p -> sb.append(p).append('\n'));
        }
        areaProfesores.setText(sb.toString());
        areaProfesores.setCaretPosition(0);
        lblInfo.setText("Profesores filtrados por Semestre " + s.getNumero());
        lblInfo.setForeground(Colores.ESTUDIANTE);
    }

    private void buscarHorarioProfesor() {
        String nombre = JOptionPane.showInputDialog(this,
                "Ingrese el nombre del profesor:",
                "Buscar horario de profesor",
                JOptionPane.QUESTION_MESSAGE);
        if (nombre == null) return;

        String objetivo = normalizar(nombre);
        if (objetivo.isBlank()) {
            lblInfo.setText("Debe ingresar un nombre de profesor valido.");
            lblInfo.setForeground(Colores.PELIGRO);
            return;
        }

        StringBuilder sb = new StringBuilder();
        List<EntradaHorario> entradasEncontradas = new ArrayList<>();
        for (Semestre s : Escenarios.todosLosSemestres()) {
            if (!s.isGenerado()) continue;

            List<EntradaHorario> delProfesor = s.getHorario().stream()
                    .filter(h -> h.getAsignatura() != null && h.getAsignatura().getProfesor() != null)
                    .filter(h -> normalizar(h.getAsignatura().getProfesor().getNombre()).contains(objetivo))
                    .collect(Collectors.toList());

            if (delProfesor.isEmpty()) continue;
            entradasEncontradas.addAll(delProfesor);

            sb.append("Semestre ").append(s.getNumero()).append(":\n");
            for (EntradaHorario h : delProfesor) {
                sb.append(String.format("- %-30s %s | Aula %d",
                        h.getAsignatura().getNombre(),
                        h.getBloque(),
                        h.getAula().getNumero()));
                if (h.tieneConflicto()) {
                    sb.append("  [!] ").append(h.getConflicto());
                }
                sb.append('\n');
            }
            sb.append('\n');
        }

        if (sb.length() == 0) {
            areaProfesores.setText("No se encontraron horarios para \"" + nombre.trim() + "\".\n"
                    + "Nota: solo se consultan semestres ya generados.");
            lblInfo.setText("Sin coincidencias para el profesor ingresado.");
            lblInfo.setForeground(Colores.PELIGRO);
            return;
        }

        sb.append("Horario por dia y hora:\n");
        for (Map.Entry<String, List<EntradaHorario>> e : agruparPorDiaYHora(entradasEncontradas).entrySet()) {
            sb.append(e.getKey()).append(":\n");
            for (EntradaHorario h : e.getValue()) {
                int semestre = semestreDeEntrada(h);
                sb.append(String.format("  %s-%s | Semestre %d | %-26s | Aula %d",
                        h.getBloque().getInicio(),
                        h.getBloque().getFin(),
                        semestre,
                        h.getAsignatura().getNombre(),
                        h.getAula().getNumero()));
                if (h.tieneConflicto()) {
                    sb.append("  [!] ").append(h.getConflicto());
                }
                sb.append('\n');
            }
            sb.append('\n');
        }

        areaProfesores.setText(sb.toString());
        areaProfesores.setCaretPosition(0);
        lblInfo.setText("Horario consolidado del profesor: " + nombre.trim());
        lblInfo.setForeground(Colores.COORDINADOR);
    }

    private void resolverConflictoSeleccionado() {
        Semestre semestreActual = actual();
        if (!semestreActual.isGenerado()) {
            lblInfo.setText("Primero genere y visualice un horario con conflictos.");
            lblInfo.setForeground(Colores.PELIGRO);
            return;
        }

        EntradaHorario conflicto = conflictoSeleccionado(semestreActual);
        if (conflicto == null) {
            lblInfo.setText("Seleccione una fila con conflicto en la tabla.");
            lblInfo.setForeground(Colores.PELIGRO);
            return;
        }

        String[] opciones = {
                "Reasignar profesor (liberar otro curso)",
                "Pasar curso a virtual",
                "Contratar profesor",
                "Agregar aula",
                "Cancelar"
        };
        int op = JOptionPane.showOptionDialog(
                this,
                "Conflicto: " + conflicto.getAsignatura().getNombre() + "\nSeleccione una accion de coordinacion:",
                "Resolver conflicto",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        if (op < 0 || op == 4) return;

        switch (op) {
            case 0:
                resolverReasignandoProfesor(semestreActual, conflicto);
                break;
            case 1:
                resolverComoVirtual(semestreActual, conflicto);
                break;
            case 2:
                resolverContratandoProfesor(semestreActual, conflicto);
                break;
            case 3:
                resolverAgregandoAula(semestreActual, conflicto);
                break;
            default:
                return;
        }

        verHorario();
        verConflictos();
    }

    private EntradaHorario conflictoSeleccionado(Semestre semestreActual) {
        int fila = tabla.getSelectedRow();
        if (fila >= 0 && fila < semestreActual.getHorario().size()) {
            EntradaHorario e = semestreActual.getHorario().get(fila);
            if (e.tieneConflicto()) return e;
        }
        return semestreActual.getHorario().stream()
                .filter(EntradaHorario::tieneConflicto)
                .findFirst()
                .orElse(null);
    }

    private void resolverComoVirtual(Semestre semestreActual, EntradaHorario conflicto) {
        boolean aplicado = Escenarios.marcarAsignaturaVirtual(conflicto.getAsignatura().getNombre());
        if (!aplicado) {
            lblInfo.setText("Solo asignaturas teoricas pueden pasar a virtual.");
            lblInfo.setForeground(Colores.PELIGRO);
            return;
        }

        regenerarSemestres(Arrays.asList(semestreActual));
        lblInfo.setText("Curso movido a modalidad virtual y horario recalculado.");
        lblInfo.setForeground(Colores.ACENTO2);
    }

    private void resolverContratandoProfesor(Semestre semestreActual, EntradaHorario conflicto) {
        String departamento = seleccionarDepartamento(conflicto);
        if (departamento == null || departamento.isBlank()) return;

        String sugerido = "Prof. " + departamento;
        String nombre = JOptionPane.showInputDialog(this,
                "Nombre del nuevo profesor (" + departamento + "):",
                sugerido);
        if (nombre == null) return;
        if (nombre.isBlank()) nombre = sugerido;

        String cedula = JOptionPane.showInputDialog(this,
                "Cedula del profesor:",
                "");
        if (cedula == null || cedula.isBlank()) {
            lblInfo.setText("Debe ingresar una cedula para registrar el profesor.");
            lblInfo.setForeground(Colores.PELIGRO);
            return;
        }

        Profesor nuevo = Escenarios.contratarProfesor(nombre.trim(), departamento, cedula.trim());
        if (nuevo == null) {
            lblInfo.setText("No fue posible registrar el profesor. Cedula duplicada o datos invalidos.");
            lblInfo.setForeground(Colores.PELIGRO);
            return;
        }

        conflicto.getAsignatura().setProfesor(nuevo);
        regenerarSemestres(Arrays.asList(semestreActual));
        lblInfo.setText("Profesor contratado y asignado. Horario recalculado.");
        lblInfo.setForeground(Colores.ACENTO2);
    }

    private void resolverAgregandoAula(Semestre semestreActual, EntradaHorario conflicto) {
        String tipo = conflicto.getAsignatura().tipoAula();

        String nombre = JOptionPane.showInputDialog(this,
                "Nombre de la nueva aula (" + tipo + "):",
                "Aula " + (tipo.equals("laboratorio") ? "Lab" : "T"));
        if (nombre == null || nombre.isBlank()) return;

        String numeroTxt = JOptionPane.showInputDialog(this,
                "Numero de aula (vacio = auto):",
                "");
        int numero = parseEntero(numeroTxt, -1);

        String capacidadTxt = JOptionPane.showInputDialog(this,
                "Capacidad:",
                "30");
        int capacidad = parseEntero(capacidadTxt, 30);

        Aula nueva = Escenarios.agregarAula(tipo, nombre.trim(), numero, "Edif. Coordinacion", capacidad);
        if (nueva == null) {
            lblInfo.setText("No se pudo registrar la nueva aula.");
            lblInfo.setForeground(Colores.PELIGRO);
            return;
        }
        if (aulas.stream().noneMatch(a -> a.getNumero() == nueva.getNumero())) {
            aulas.add(nueva);
        }

        regenerarSemestres(Arrays.asList(semestreActual));
        lblInfo.setText("Nueva aula agregada y horario recalculado.");
        lblInfo.setForeground(Colores.ACENTO2);
    }

    private void resolverReasignandoProfesor(Semestre semestreActual, EntradaHorario conflicto) {
        String departamento = seleccionarDepartamento(conflicto);
        if (departamento == null || departamento.isBlank()) return;

        List<Profesor> candidatos = Escenarios.profesoresPorDepartamento(departamento);
        if (candidatos.isEmpty()) {
            lblInfo.setText("No hay profesores en el departamento " + departamento + ".");
            lblInfo.setForeground(Colores.PELIGRO);
            return;
        }

        Profesor profSeleccionado = (Profesor) JOptionPane.showInputDialog(
                this,
                "Profesor a reasignar (" + departamento + "):",
                "Reasignar profesor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                candidatos.toArray(),
                candidatos.get(0)
        );
        if (profSeleccionado == null) return;

        List<CursoAsignado> cursos = cursosAsignadosDeProfesorEnOtrosSemestres(profSeleccionado, semestreActual);
        if (cursos.isEmpty()) {
            lblInfo.setText("Ese profesor no tiene cursos en otros semestres para liberar.");
            lblInfo.setForeground(Colores.PELIGRO);
            return;
        }

        CursoAsignado cursoALiberar = (CursoAsignado) JOptionPane.showInputDialog(
                this,
                "Seleccione el curso a eliminar para liberar al profesor:",
                "Liberar curso",
                JOptionPane.QUESTION_MESSAGE,
                null,
                cursos.toArray(),
                cursos.get(0)
        );
        if (cursoALiberar == null) return;

        removerCursoDeSemestre(cursoALiberar.semestre, cursoALiberar.nombreAsignatura);
        conflicto.getAsignatura().setProfesor(profSeleccionado);
        regenerarSemestres(Arrays.asList(semestreActual));

        lblInfo.setText("Profesor reasignado y curso liberado en Semestre " + cursoALiberar.semestre.getNumero() + ".");
        lblInfo.setForeground(Colores.ACENTO2);
    }

    private void removerCursoDeSemestre(Semestre semestre, String nombreAsignatura) {
        String objetivo = normalizar(nombreAsignatura);
        semestre.getAsignaturas().removeIf(a -> normalizar(a.getNombre()).equals(objetivo));

        List<EntradaHorario> restante = semestre.getHorario().stream()
                .filter(h -> normalizar(h.getAsignatura().getNombre()).equals(objetivo) == false)
                .collect(Collectors.toList());
        semestre.reemplazarHorario(restante);
    }

    private List<CursoAsignado> cursosAsignadosDeProfesorEnOtrosSemestres(Profesor profesor, Semestre excluir) {
        List<CursoAsignado> out = new ArrayList<>();
        for (Semestre s : Escenarios.todosLosSemestres()) {
            if (s == excluir || !s.isGenerado()) continue;

            Optional<EntradaHorario> primera = s.getHorario().stream()
                    .filter(h -> h.getAsignatura() != null && h.getAsignatura().getProfesor() != null)
                    .filter(h -> normalizar(h.getAsignatura().getProfesor().getNombre()).equals(normalizar(profesor.getNombre())))
                    .findFirst();
            if (primera.isPresent()) {
                out.add(new CursoAsignado(s, primera.get().getAsignatura().getNombre()));
            }
        }
        return out;
    }

    private String seleccionarDepartamento(EntradaHorario conflicto) {
        String deptoDefault = null;
        if (conflicto.getAsignatura() != null && conflicto.getAsignatura().getProfesor() != null) {
            deptoDefault = conflicto.getAsignatura().getProfesor().getDepartamento();
        }

        List<String> departamentos = Escenarios.obtenerDepartamentos();
        if (departamentos.isEmpty()) {
            departamentos = Arrays.asList("Computacion");
        }

        Object seleccionado = JOptionPane.showInputDialog(
                this,
                "Departamento del curso/conflicto:",
                "Seleccionar departamento",
                JOptionPane.QUESTION_MESSAGE,
                null,
                departamentos.toArray(),
                deptoDefault != null ? deptoDefault : departamentos.get(0)
        );
        return seleccionado == null ? null : seleccionado.toString();
    }

    private void regenerarSemestres(List<Semestre> objetivos) {
        for (Semestre s : objetivos) {
            s.reemplazarHorario(new ArrayList<>());
        }
        GeneradorHorarios.generarGlobal(objetivos, aulas, Escenarios.todosLosSemestres());
        Escenarios.guardarHorariosGenerados();
    }

    private int parseEntero(String valor, int porDefecto) {
        if (valor == null || valor.isBlank()) return porDefecto;
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException ex) {
            return porDefecto;
        }
    }

    private String textoAula(EntradaHorario h) {
        if (h.getAula() == null) return "-";
        if (h.getAula().getNombre() != null && h.getAula().getNombre().startsWith("Virtual")) {
            return "Virtual";
        }
        return "Aula " + h.getAula().getNumero();
    }

    private static class CursoAsignado {
        private final Semestre semestre;
        private final String nombreAsignatura;

        private CursoAsignado(Semestre semestre, String nombreAsignatura) {
            this.semestre = semestre;
            this.nombreAsignatura = nombreAsignatura;
        }

        @Override
        public String toString() {
            return "Semestre " + semestre.getNumero() + " - " + nombreAsignatura;
        }
    }

    private Map<String, List<EntradaHorario>> agruparPorDiaYHora(List<EntradaHorario> entradas) {
        List<EntradaHorario> ordenadas = new ArrayList<>(entradas);
        ordenadas.sort(Comparator
                .comparingInt((EntradaHorario h) -> indiceDia(h.getBloque().getDia()))
                .thenComparing(h -> h.getBloque().getInicio())
                .thenComparing(h -> h.getBloque().getFin())
                .thenComparing(h -> h.getAsignatura().getNombre()));

        Map<String, List<EntradaHorario>> porDia = new LinkedHashMap<>();
        for (EntradaHorario h : ordenadas) {
            porDia.computeIfAbsent(h.getBloque().getDia(), k -> new ArrayList<>()).add(h);
        }
        return porDia;
    }

    private int indiceDia(String dia) {
        for (int i = 0; i < horario.Bloque.DIAS.length; i++) {
            if (horario.Bloque.DIAS[i].equalsIgnoreCase(dia)) return i;
        }
        return Integer.MAX_VALUE;
    }

    private int semestreDeEntrada(EntradaHorario entrada) {
        for (Semestre s : Escenarios.todosLosSemestres()) {
            if (!s.isGenerado()) continue;
            if (s.getHorario().contains(entrada)) return s.getNumero();
        }
        return -1;
    }

    private String normalizar(String s) {
        if (s == null) return "";
        String n = s.toLowerCase(Locale.ROOT).trim();
        return Normalizer.normalize(n, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
    }

    private Semestre actual() {
        return semestres.get(combo.getSelectedIndex());
    }

    private void estilizarTabla() {
        tabla.setBackground(Colores.PANEL);
        tabla.setForeground(Colores.TEXTO);
        tabla.setFont(Colores.NORMAL);
        tabla.setRowHeight(26);
        tabla.setGridColor(Colores.BORDE);
        tabla.setSelectionBackground(Colores.COORDINADOR);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.getTableHeader().setBackground(Colores.COORDINADOR);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(Colores.SUBTIT);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String estado = (String) t.getModel().getValueAt(r, 5);
                if (sel) {
                    setBackground(Colores.COORDINADOR);
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
