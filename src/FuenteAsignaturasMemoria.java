import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FuenteAsignaturasMemoria implements FuenteAsignaturas {
    private final List<Asignatura> materias = new ArrayList<>();
    private final List<AsignaturaTeorica> asignaturasTeoricas = new ArrayList<>();
    private final List<AsignaturaPractica> asignaturasPracticas = new ArrayList<>();
    private final List<Profesor> profesores = new ArrayList<>();

    public FuenteAsignaturasMemoria() {
        cargarDesdeJson();
        if (materias.isEmpty()) {
            cargarDatosPorDefecto();
        }
    }

    private void cargarDesdeJson() {
        Path ruta = resolverRutaJson();
        if (ruta == null) {
            return;
        }

        try {
            String contenido = Files.readString(ruta, StandardCharsets.UTF_8);
            cargarMaterias(contenido);
            cargarProfesores(contenido);
        } catch (IOException ignored) {
            // Si falla lectura, se usan datos por defecto.
        }
    }

    private Path resolverRutaJson() {
        Path rutaSrc = Paths.get("src", "asignaturas.json");
        if (Files.exists(rutaSrc)) {
            return rutaSrc;
        }

        Path rutaRaiz = Paths.get("asignaturas.json");
        if (Files.exists(rutaRaiz)) {
            return rutaRaiz;
        }

        return null;
    }

    private void cargarMaterias(String contenido) {
        String bloque = extraerBloqueArray(contenido, "Materias");
        if (bloque == null) {
            return;
        }

        Pattern patron = Pattern.compile("\\{\\s*\"nombre\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"creditos\"\\s*:\\s*(\\d+)\\s*\\}");
        Matcher matcher = patron.matcher(bloque);

        while (matcher.find()) {
            String nombre = matcher.group(1);
            int creditos = Integer.parseInt(matcher.group(2));
            materias.add(new Asignatura(nombre, creditos));
        }
    }

    private void cargarProfesores(String contenido) {
        String bloque = extraerBloqueArray(contenido, "Profesores");
        if (bloque == null) {
            return;
        }

        Pattern patron = Pattern.compile("\\{\\s*\"nombre\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"Departamento\"\\s*:\\s*\"([^\"]+)\"\\s*\\}");
        Matcher matcher = patron.matcher(bloque);

        while (matcher.find()) {
            Profesor profesor = new Profesor();
            profesor.setNombre(matcher.group(1));
            profesores.add(profesor);
        }
    }

    private String extraerBloqueArray(String contenido, String nombreBloque) {
        Pattern patron = Pattern.compile("\"" + Pattern.quote(nombreBloque) + "\"\\s*:\\s*\\[(.*?)]", Pattern.DOTALL);
        Matcher matcher = patron.matcher(contenido);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private void cargarDatosPorDefecto() {
        materias.add(new Asignatura("Programacion I", 4));
        materias.add(new Asignatura("Matematica Discreta", 3));
        materias.add(new Asignatura("Bases de Datos", 4));
    }

    @Override
    public List<Asignatura> obtenerMaterias() {
        return materias;
    }

    @Override
    public void agregarMateria(Asignatura m) {
        materias.add(m);
    }

    @Override
    public void eliminarMateria(String nombre) {
        materias.removeIf(m -> m.getNombre().equalsIgnoreCase(nombre));
    }

    @Override
    public void actualizarCreditosMateria(String nombre, int nuevosCreditos) {
        for (Asignatura m : materias) {
            if (m.getNombre().equalsIgnoreCase(nombre)) {
                m.setCreditos(nuevosCreditos);
                return;
            }
        }
    }

    @Override
    public List<AsignaturaTeorica> obtenerAsignaturasTeoricas() {
        return asignaturasTeoricas;
    }

    @Override
    public void agregarAsignaturaTeorica(AsignaturaTeorica a) {
        asignaturasTeoricas.add(a);
    }

    @Override
    public void eliminarAsignaturaTeorica(String nombre) {
        // La clase AsignaturaTeorica no expone propiedades todavia.
    }

    @Override
    public List<AsignaturaPractica> obtenerAsignaturasPracticas() {
        return asignaturasPracticas;
    }

    @Override
    public void agregarAsignaturaPractica(AsignaturaPractica a) {
        asignaturasPracticas.add(a);
    }

    @Override
    public void eliminarAsignaturaPractica(String nombre) {
        // La clase AsignaturaPractica no expone propiedades todavia.
    }

    @Override
    public List<Profesor> obtenerProfesores() {
        return profesores;
    }

    @Override
    public void agregarProfesor(Profesor p) {
        profesores.add(p);
    }

    @Override
    public void eliminarProfesor(String nombre) {
        profesores.removeIf(p -> p.getNombre() != null && p.getNombre().equalsIgnoreCase(nombre));
    }

    @Override
    public void actualizarDepartamentoProfesor(String nombre, String nuevoDepartamento) {
        // Profesor no incluye departamento todavia.
    }
}
