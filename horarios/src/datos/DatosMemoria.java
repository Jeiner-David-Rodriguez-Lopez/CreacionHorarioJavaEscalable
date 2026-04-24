package datos;

import asignaturas.Asignatura;
import asignaturas.Practica;
import asignaturas.Teorica;
import aulas.Aula;
import aulas.AulaLab;
import aulas.AulaTeoria;
import horario.Bloque;
import horario.Semestre;
import usuarios.Profesor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementacion en memoria que carga datos desde JSON.
 * Si el JSON falla, usa respaldo quemado.
 */
public class DatosMemoria implements FuenteAsignaturas {

    private static final String JSON_REL_PATH_1 = "horarios/src/datos/DatosMemoriaJSON.json";
    private static final String JSON_REL_PATH_2 = "src/datos/DatosMemoriaJSON.json";

    private final List<Profesor>   profesores = new ArrayList<>();
    private final List<Asignatura> materias   = new ArrayList<>();
    private final List<Teorica>    teoricas   = new ArrayList<>();
    private final List<Practica>   practicas  = new ArrayList<>();
    private final List<Aula>       aulas      = new ArrayList<>();
    private final Map<Integer, List<Asignatura>> materiasPorSemestre = new HashMap<>();
    private final Random random = new Random();

    public DatosMemoria() {
        boolean cargado = cargarDesdeJson();
        if (!cargado) {
            cargarProfesoresRespaldo();
            cargarMateriasRespaldo();
            cargarAulasRespaldo();
            poblarSemestresPorMateria();
        }
    }

    private boolean cargarDesdeJson() {
        String json = leerJson();
        if (json == null || json.isBlank()) return false;
        try {
            cargarProfesoresDesdeJson(json);
            cargarMateriasDesdeJson(json);
            cargarAulasDesdeJson(json);
            poblarSemestresPorMateria();
            return !profesores.isEmpty() && !materias.isEmpty() && !aulas.isEmpty();
        } catch (Exception ex) {
            profesores.clear();
            materias.clear();
            teoricas.clear();
            practicas.clear();
            aulas.clear();
            materiasPorSemestre.clear();
            return false;
        }
    }

    private String leerJson() {
        List<String> candidates = Arrays.asList(JSON_REL_PATH_1, JSON_REL_PATH_2, "datos/DatosMemoriaJSON.json");
        for (String c : candidates) {
            try {
                Path p = Path.of(c);
                if (Files.exists(p)) {
                    return Files.readString(p, StandardCharsets.UTF_8);
                }
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    private void cargarProfesoresRespaldo() {
        profesores.addAll(Arrays.asList(
            new Profesor("Carlos Perez",       "1-0001", "Idiomas",
                Arrays.asList(new Bloque("Lunes",    "07:00", "07:50"))),
            new Profesor("Juan Perez",         "1-0002", "Idiomas",
                Arrays.asList(new Bloque("Martes",   "07:00", "07:50"))),
            new Profesor("Pedro Perez",        "1-0003", "Idiomas",
                Arrays.asList(new Bloque("Miercoles","07:00", "07:50"))),
            new Profesor("Jean Marin",         "1-0004", "Ciencias",
                Arrays.asList(new Bloque("Lunes",    "09:45", "10:35"))),
            new Profesor("Oscar Viquez",       "1-0005", "Idiomas",
                Arrays.asList(new Bloque("Jueves",   "07:00", "07:50"))),
            new Profesor("Ana Padilla",        "1-0006", "Computacion",
                Arrays.asList(new Bloque("Lunes",    "07:55", "08:45"))),
            new Profesor("Julia Campos",       "1-0007", "Computacion",
                Arrays.asList(new Bloque("Martes",   "09:45", "10:35"))),
            new Profesor("Francisco Hernandez","1-0008", "Computacion",
                Arrays.asList(new Bloque("Viernes",  "07:00", "07:50"))),
            new Profesor("Allan Torrez",       "1-0009", "Ciencias",
                Arrays.asList(new Bloque("Miercoles","12:30", "13:20"))),
            new Profesor("Marcos Zamora",      "1-0010", "Computacion",
                Arrays.asList(new Bloque("Jueves",   "09:45", "10:35"))),
            new Profesor("Julio Rodriguez",    "1-0011", "Ciencias",
                Arrays.asList(new Bloque("Viernes",  "09:45", "10:35"))),
            new Profesor("Fabian Lopez",       "1-0012", "Ciencias",
                Arrays.asList(new Bloque("Lunes",    "12:30", "13:20")))
        ));
    }

    private void cargarMateriasRespaldo() {
        // Teoricas
        Teorica prog1  = new Teorica("Programacion I",        4, profesorPorNombre("Ana Padilla"),         true,  "https://tec.ac.cr/prog1",   "Presencial");
        Teorica disc   = new Teorica("Matematica Discreta",   3, profesorPorNombre("Marcos Zamora"),       false, "https://tec.ac.cr/disc",    "Presencial");
        Teorica bd     = new Teorica("Bases de Datos",        4, profesorPorNombre("Julia Campos"),        true,  "https://tec.ac.cr/bd",      "Presencial");
        Teorica redes  = new Teorica("Redes de Computadoras", 3, profesorPorNombre("Francisco Hernandez"), true,  "https://tec.ac.cr/redes",   "Presencial");
        Teorica prog2  = new Teorica("Programacion II",       4, profesorPorNombre("Ana Padilla"),         true,  "https://tec.ac.cr/prog2",   "Presencial");
        Teorica mat    = new Teorica("Matematica",            2, profesorPorNombre("Marcos Zamora"),       false, "https://tec.ac.cr/mat",     "Presencial");
        Teorica eng1   = new Teorica("Ingles Tecnico I",      4, profesorPorNombre("Carlos Perez"),        false, "https://tec.ac.cr/eng1",    "Presencial");
        Teorica eng2   = new Teorica("Ingles Tecnico II",     3, profesorPorNombre("Oscar Viquez"),        false, "https://tec.ac.cr/eng2",    "Presencial");
        Teorica qf     = new Teorica("Quimica y Fisica",      3, profesorPorNombre("Jean Marin"),          false, "https://tec.ac.cr/qf",      "Presencial");
        Teorica prog3  = new Teorica("Programacion III",      4, profesorPorNombre("Julia Campos"),        true,  "https://tec.ac.cr/prog3",   "Presencial");
        Teorica eng3   = new Teorica("Ingles Tecnico III",    4, profesorPorNombre("Carlos Perez"),        false, "https://tec.ac.cr/eng3",    "Presencial");
        Teorica engC   = new Teorica("Ingles Conversacional", 3, profesorPorNombre("Oscar Viquez"),        false, "https://tec.ac.cr/engconv", "Presencial");

        // Practica
        Practica labQF = new Practica("Lab. Quimica y Fisica", 2, profesorPorNombre("Allan Torrez"), false, "Windows 11",
                Arrays.asList("Microscopio virtual", "Guia Lab Fisica", "PhET Simulations"));

        // Agregar a listas
        teoricas.addAll(Arrays.asList(prog1,disc,bd,redes,prog2,mat,eng1,eng2,qf,prog3,eng3,engC));
        practicas.add(labQF);

        materias.addAll(teoricas);
        materias.addAll(practicas);
    }

    private void cargarAulasRespaldo() {
        aulas.add(new AulaTeoria("Aula T-01", 101, "Edif. A", 35, true, true));
        aulas.add(new AulaTeoria("Aula T-02", 102, "Edif. A", 40, true, true));
        aulas.add(new AulaTeoria("Aula T-03", 103, "Edif. A", 35, false, true));
        aulas.add(new AulaLab("Lab-01", 201, "Edif. B", 24, Arrays.asList("PC Intel i5", "JDK", "IDE")));
        aulas.add(new AulaLab("Lab-02", 202, "Edif. B", 24, Arrays.asList("PC AMD", "JDK", "IDE")));
    }

    private void cargarProfesoresDesdeJson(String json) {
        String arr = extraerArreglo(json, "Profesores");
        if (arr == null) return;

        Set<String> dedupe = new HashSet<>();
        int idx = 1;
        for (String obj : extraerObjetos(arr)) {
            String nombre = repararTexto(obtenerTexto(obj, "nombre"));
            String dep = repararTexto(obtenerTexto(obj, "Departamento"));
            if (nombre == null || nombre.isBlank()) continue;
            if (dep == null || dep.isBlank()) dep = "General";
            String key = normalizar(nombre) + "|" + normalizar(dep);
            if (!dedupe.add(key)) continue;
            String cedula = String.format("J-%04d", idx++);
            profesores.add(new Profesor(nombre, cedula, dep, disponibilidadPorDefecto()));
        }
    }

    private void cargarMateriasDesdeJson(String json) {
        String arr = extraerArreglo(json, "Materias");
        if (arr == null) return;

        for (String obj : extraerObjetos(arr)) {
            String nombre = repararTexto(obtenerTexto(obj, "nombre"));
            String departamento = repararTexto(obtenerTexto(obj, "Departamento"));
            int creditos = obtenerEntero(obj, "creditos", "créditos", "crÃ©ditos");
            int semestre = obtenerEntero(obj, "semestre");
            if (nombre == null || nombre.isBlank() || creditos <= 0 || semestre <= 0) continue;

            Profesor profesor = profesorAleatorioPorDepartamento(departamento);
            boolean esPractica = esMateriaPractica(nombre);
            Asignatura mat;
            if (esPractica) {
                mat = new Practica(
                    nombre,
                    creditos,
                    profesor,
                    true,
                    "Windows/Linux",
                    Arrays.asList("Guia", "Computadora")
                );
                practicas.add((Practica) mat);
            } else {
                mat = new Teorica(
                    nombre,
                    creditos,
                    profesor,
                    true,
                    "https://tec.ac.cr/" + slug(nombre),
                    "Presencial"
                );
                teoricas.add((Teorica) mat);
            }
            materias.add(mat);
            materiasPorSemestre.computeIfAbsent(semestre, k -> new ArrayList<>()).add(mat);
        }
    }

    private void cargarAulasDesdeJson(String json) {
        Map<String, Aula> porClave = new LinkedHashMap<>();

        String teoricasJson = extraerArreglo(json, "AsignaturasTeoricas");
        if (teoricasJson != null) {
            for (String obj : extraerObjetos(teoricasJson)) {
                String nombre = repararTexto(obtenerTexto(obj, "aulaTeorica"));
                if (nombre == null || nombre.isBlank()) continue;
                String key = "T:" + nombre.trim().toLowerCase(Locale.ROOT);
                porClave.computeIfAbsent(key, k -> {
                    int numero = numeroAula(nombre, 100 + porClave.size() + 1);
                    return new AulaTeoria(nombre.trim(), numero, "Edif. Teoria", 35, true, true);
                });
            }
        }

        String practicasJson = extraerArreglo(json, "AsignaturasPracticas");
        if (practicasJson != null) {
            for (String obj : extraerObjetos(practicasJson)) {
                String nombre = repararTexto(obtenerTexto(obj, "aulaPractica"));
                if (nombre == null || nombre.isBlank()) continue;
                String key = "P:" + nombre.trim().toLowerCase(Locale.ROOT);
                porClave.computeIfAbsent(key, k -> {
                    int numero = numeroAula(nombre, 200 + porClave.size() + 1);
                    return new AulaLab(nombre.trim(), numero, "Edif. Laboratorios", 24,
                        Arrays.asList("PC", "JDK", "IDE"));
                });
            }
        }

        aulas.addAll(porClave.values());
    }

    private List<String> extraerObjetos(String arrayJson) {
        List<String> objs = new ArrayList<>();
        Matcher m = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL).matcher(arrayJson);
        while (m.find()) {
            objs.add(m.group(1));
        }
        return objs;
    }

    private String extraerArreglo(String json, String nombre) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(nombre) + "\"\\s*:\\s*\\[(.*?)]\\s*(,|\\})", Pattern.DOTALL);
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private String obtenerTexto(String obj, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);
        Matcher m = p.matcher(obj);
        return m.find() ? m.group(1).trim() : null;
    }

    private int obtenerEntero(String obj, String... keys) {
        for (String key : keys) {
            Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(\"?)(\\d+)\\1");
            Matcher m = p.matcher(obj);
            if (m.find()) {
                return Integer.parseInt(m.group(2));
            }
        }
        return -1;
    }

    private List<Bloque> disponibilidadPorDefecto() {
        List<Bloque> disp = new ArrayList<>();
        for (String d : Bloque.DIAS) {
            for (String[] s : Bloque.SLOTS) {
                disp.add(new Bloque(d, s[0], s[1]));
            }
        }
        return disp;
    }

    private Profesor profesorAleatorioPorDepartamento(String deptMateria) {
        String objetivo = normalizar(repararTexto(deptMateria));
        List<Profesor> match = new ArrayList<>();
        for (Profesor p : profesores) {
            if (normalizar(p.getDepartamento()).equals(objetivo)) {
                match.add(p);
            }
        }
        if (match.isEmpty()) match = profesores;
        if (match.isEmpty()) return null;
        return match.get(random.nextInt(match.size()));
    }

    private Profesor profesorPorNombre(String nombre) {
        if (nombre == null) return null;
        String esperado = normalizar(nombre);
        for (Profesor p : profesores) {
            if (normalizar(p.getNombre()).equals(esperado)) return p;
        }
        return null;
    }

    private boolean esMateriaPractica(String nombre) {
        String n = normalizar(nombre);
        return n.contains("laboratorio") || n.startsWith("lab");
    }

    private int numeroAula(String nombre, int fallback) {
        Matcher m = Pattern.compile("(\\d+)").matcher(nombre);
        return m.find() ? Integer.parseInt(m.group(1)) : fallback;
    }

    private String slug(String s) {
        String n = normalizar(s).replace(' ', '-');
        return n.replaceAll("[^a-z0-9\\-]", "");
    }

    private String repararTexto(String s) {
        if (s == null) return null;
        try {
            String fixed = new String(s.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            if (fixed.contains("�")) return s;
            return fixed;
        } catch (Exception ex) {
            return s;
        }
    }

    private String normalizar(String s) {
        if (s == null) return "";
        String n = repararTexto(s).toLowerCase(Locale.ROOT).trim();
        n = Normalizer.normalize(n, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        n = n.replace("ã±", "n").replace("Ã±", "n");
        return n;
    }

    private void poblarSemestresPorMateria() {
        if (!materiasPorSemestre.isEmpty()) return;
        int sem = 1;
        materiasPorSemestre.putIfAbsent(sem, new ArrayList<>());
        for (Asignatura a : materias) {
            materiasPorSemestre.get(sem).add(a);
            if (materiasPorSemestre.get(sem).size() >= 5) {
                sem++;
                materiasPorSemestre.putIfAbsent(sem, new ArrayList<>());
            }
        }
    }

    public List<Semestre> crearSemestres(int desde, int hasta) {
        List<Semestre> salida = new ArrayList<>();
        for (int s = desde; s <= hasta; s++) {
            List<Asignatura> mats = materiasPorSemestre.getOrDefault(s, Collections.emptyList());
            if (!mats.isEmpty()) {
                salida.add(new Semestre(s, new ArrayList<>(mats)));
            }
        }
        return salida;
    }

    public List<Aula> obtenerAulas() {
        return new ArrayList<>(aulas);
    }

    @Override public List<Asignatura> obtenerMaterias()  { return materias; }
    @Override public List<Teorica>    obtenerTeoricas()  { return teoricas; }
    @Override public List<Practica>   obtenerPracticas() { return practicas; }
    @Override public List<Profesor>   obtenerProfesores(){ return profesores; }
}
