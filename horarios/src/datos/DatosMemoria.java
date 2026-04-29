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
import java.util.LinkedHashSet;
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
    private final ResolucionesCoordinadorJSON.Datos resoluciones = new ResolucionesCoordinadorJSON.Datos();
    private final Random random = new Random();

    public DatosMemoria() {
        boolean cargado = cargarDesdeJson();
        if (!cargado) {
            cargarProfesoresRespaldo();
            cargarMateriasRespaldo();
            cargarAulasRespaldo();
            poblarSemestresPorMateria();
        }
        aplicarResolucionesPersistidas();
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
            String cedula = repararTexto(obtenerTexto(obj, "cedula"));
            if (nombre == null || nombre.isBlank()) continue;
            if (dep == null || dep.isBlank()) dep = "General";
            String key = normalizar(nombre) + "|" + normalizar(dep);
            if (!dedupe.add(key)) continue;
            if (cedula == null || cedula.isBlank()) {
                cedula = String.format("J-%04d", idx++);
            }
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

    public Profesor agregarProfesorPersistente(String nombre, String departamento) {
        return agregarProfesorPersistente(nombre, departamento, null);
    }

    public Profesor agregarProfesorPersistente(String nombre, String departamento, String cedulaIngresada) {
        String n = repararTexto(nombre);
        String d = repararTexto(departamento);
        if (n == null || n.isBlank() || d == null || d.isBlank()) return null;

        String cedulaLimpia = cedulaNormalizada(cedulaIngresada);
        if (cedulaLimpia != null && existeCedula(cedulaLimpia)) return null;

        Profesor porNombre = profesorPorNombre(n);
        if (porNombre != null) {
            return porNombre;
        }

        String cedula = cedulaLimpia != null ? cedulaLimpia : siguienteCedulaExterna();
        Profesor nuevo = new Profesor(n.trim(), cedula, d.trim(), disponibilidadPorDefecto());
        profesores.add(nuevo);

        resoluciones.profesoresExtra.add(new ResolucionesCoordinadorJSON.ProfesorExtra(
                nuevo.getNombre(), nuevo.getDepartamento(), nuevo.getCedula(), serializarDisponibilidad(nuevo.getDisponibilidad())));
        guardarResoluciones();
        return nuevo;
    }

    public boolean actualizarDisponibilidadProfesor(String cedula, List<Bloque> disponibilidad) {
        String objetivo = cedulaNormalizada(cedula);
        if (objetivo == null) return false;
        Profesor profesor = profesorPorCedula(objetivo);
        if (profesor == null) return false;

        List<Bloque> nueva = (disponibilidad == null || disponibilidad.isEmpty())
                ? disponibilidadPorDefecto()
                : new ArrayList<>(disponibilidad);
        profesor.setDisponibilidad(nueva);

        ResolucionesCoordinadorJSON.ProfesorExtra extra = buscarProfesorExtraPorCedula(objetivo);
        List<String> serializada = serializarDisponibilidad(nueva);
        if (extra == null) {
            resoluciones.profesoresExtra.add(new ResolucionesCoordinadorJSON.ProfesorExtra(
                    profesor.getNombre(), profesor.getDepartamento(), profesor.getCedula(), serializada));
        } else {
            resoluciones.profesoresExtra.remove(extra);
            resoluciones.profesoresExtra.add(new ResolucionesCoordinadorJSON.ProfesorExtra(
                    extra.nombre, extra.departamento, extra.cedula, serializada));
        }
        guardarResoluciones();
        return true;
    }

    public Aula agregarAulaPersistente(String tipo, String nombre, int numero, String ubicacion, int capacidad) {
        String t = tipo == null ? "" : tipo.trim().toLowerCase(Locale.ROOT);
        String n = repararTexto(nombre);
        String u = repararTexto(ubicacion);
        if (!"teoria".equals(t) && !"laboratorio".equals(t)) return null;
        if (n == null || n.isBlank()) return null;
        if (u == null || u.isBlank()) u = "Edif. Anexo";
        if (capacidad <= 0) capacidad = 30;

        if (numero <= 0 || existeAulaNumero(numero)) {
            numero = siguienteNumeroAula();
        }

        Aula aula;
        if ("laboratorio".equals(t)) {
            aula = new AulaLab(n.trim(), numero, u.trim(), capacidad, Arrays.asList("PC", "JDK", "IDE"));
        } else {
            aula = new AulaTeoria(n.trim(), numero, u.trim(), capacidad, true, true);
        }
        aulas.add(aula);

        resoluciones.aulasExtra.add(new ResolucionesCoordinadorJSON.AulaExtra(
                t, aula.getNombre(), aula.getNumero(), aula.getUbicacion(), aula.getCapacidad()));
        guardarResoluciones();
        return aula;
    }

    public boolean marcarAsignaturaVirtualPersistente(String nombreAsignatura) {
        if (nombreAsignatura == null || nombreAsignatura.isBlank()) return false;
        Asignatura a = buscarMateriaPorNombre(nombreAsignatura);
        if (!(a instanceof Teorica)) {
            return false;
        }
        ((Teorica) a).setModalidad("Virtual");

        boolean existe = resoluciones.materiasVirtuales.stream()
                .anyMatch(m -> normalizar(m).equals(normalizar(a.getNombre())));
        if (!existe) {
            resoluciones.materiasVirtuales.add(a.getNombre());
            guardarResoluciones();
        }
        return true;
    }

    public boolean marcarAsignaturaPresencialPersistente(String nombreAsignatura) {
        if (nombreAsignatura == null || nombreAsignatura.isBlank()) return false;
        Asignatura a = buscarMateriaPorNombre(nombreAsignatura);
        if (!(a instanceof Teorica)) {
            return false;
        }
        ((Teorica) a).setModalidad("Presencial");

        boolean removida = resoluciones.materiasVirtuales.removeIf(
                m -> normalizar(m).equals(normalizar(a.getNombre())));
        if (removida) {
            guardarResoluciones();
        }
        return true;
    }

    public List<String> obtenerDepartamentos() {
        Set<String> out = new LinkedHashSet<>();
        for (Profesor p : profesores) {
            if (p.getDepartamento() != null && !p.getDepartamento().isBlank()) {
                out.add(p.getDepartamento());
            }
        }
        return new ArrayList<>(out);
    }

    public List<Profesor> obtenerProfesoresPorDepartamento(String departamento) {
        String objetivo = normalizar(departamento);
        List<Profesor> out = new ArrayList<>();
        for (Profesor p : profesores) {
            if (normalizar(p.getDepartamento()).equals(objetivo)) {
                out.add(p);
            }
        }
        return out;
    }

    public Profesor buscarProfesorPorCedula(String cedula) {
        return profesorPorCedula(cedulaNormalizada(cedula));
    }

    @Override public List<Asignatura> obtenerMaterias()  { return materias; }
    @Override public List<Teorica>    obtenerTeoricas()  { return teoricas; }
    @Override public List<Practica>   obtenerPracticas() { return practicas; }
    @Override public List<Profesor>   obtenerProfesores(){ return profesores; }

    private void aplicarResolucionesPersistidas() {
        ResolucionesCoordinadorJSON.Datos datos = ResolucionesCoordinadorJSON.cargar();
        resoluciones.profesoresExtra.clear();
        resoluciones.profesoresExtra.addAll(datos.profesoresExtra);
        resoluciones.aulasExtra.clear();
        resoluciones.aulasExtra.addAll(datos.aulasExtra);
        resoluciones.materiasVirtuales.clear();
        resoluciones.materiasVirtuales.addAll(datos.materiasVirtuales);

        for (ResolucionesCoordinadorJSON.ProfesorExtra p : resoluciones.profesoresExtra) {
            Profesor existente = profesorPorCedula(cedulaNormalizada(p.cedula));
            if (existente != null) {
                List<Bloque> disponibilidad = deserializarDisponibilidad(p.disponibilidad);
                if (!disponibilidad.isEmpty()) {
                    existente.setDisponibilidad(disponibilidad);
                }
                continue;
            }
            if (profesorPorNombre(p.nombre) != null) continue;
            profesores.add(new Profesor(
                    p.nombre,
                    p.cedula == null || p.cedula.isBlank() ? "EXT-" + (profesores.size() + 1) : p.cedula,
                    p.departamento,
                    disponibilidadDesdePersistencia(p.disponibilidad)));
        }

        for (ResolucionesCoordinadorJSON.AulaExtra a : resoluciones.aulasExtra) {
            if (existeAulaNumero(a.numero)) continue;
            if ("laboratorio".equalsIgnoreCase(a.tipo)) {
                aulas.add(new AulaLab(a.nombre, a.numero, a.ubicacion, a.capacidad, Arrays.asList("PC", "JDK", "IDE")));
            } else {
                aulas.add(new AulaTeoria(a.nombre, a.numero, a.ubicacion, a.capacidad, true, true));
            }
        }

        for (String materiaVirtual : resoluciones.materiasVirtuales) {
            Asignatura a = buscarMateriaPorNombre(materiaVirtual);
            if (a instanceof Teorica) {
                ((Teorica) a).setModalidad("Virtual");
            }
        }
    }

    private void guardarResoluciones() {
        ResolucionesCoordinadorJSON.guardar(resoluciones);
    }

    private boolean existeAulaNumero(int numero) {
        for (Aula a : aulas) {
            if (a.getNumero() == numero) return true;
        }
        return false;
    }

    private int siguienteNumeroAula() {
        int max = 100;
        for (Aula a : aulas) {
            if (a.getNumero() > max) max = a.getNumero();
        }
        return max + 1;
    }

    private Asignatura buscarMateriaPorNombre(String nombre) {
        String objetivo = normalizar(nombre);
        for (Asignatura a : materias) {
            if (normalizar(a.getNombre()).equals(objetivo)) return a;
        }
        return null;
    }

    private Profesor profesorPorCedula(String cedula) {
        if (cedula == null) return null;
        for (Profesor p : profesores) {
            if (cedula.equalsIgnoreCase(cedulaNormalizada(p.getCedula()))) {
                return p;
            }
        }
        return null;
    }

    private boolean existeCedula(String cedula) {
        return profesorPorCedula(cedula) != null;
    }

    private String cedulaNormalizada(String cedula) {
        if (cedula == null) return null;
        String c = cedula.trim();
        return c.isEmpty() ? null : c;
    }

    private String siguienteCedulaExterna() {
        int idx = profesores.size() + 1;
        String c;
        do {
            c = "EXT-" + String.format("%04d", idx++);
        } while (existeCedula(c));
        return c;
    }

    private ResolucionesCoordinadorJSON.ProfesorExtra buscarProfesorExtraPorCedula(String cedula) {
        if (cedula == null) return null;
        for (ResolucionesCoordinadorJSON.ProfesorExtra p : resoluciones.profesoresExtra) {
            if (cedula.equalsIgnoreCase(cedulaNormalizada(p.cedula))) return p;
        }
        return null;
    }

    private List<String> serializarDisponibilidad(List<Bloque> disponibilidad) {
        List<String> out = new ArrayList<>();
        if (disponibilidad == null) return out;
        for (Bloque b : disponibilidad) {
            if (b == null) continue;
            out.add(b.getDia() + "|" + b.getInicio() + "|" + b.getFin());
        }
        return out;
    }

    private List<Bloque> deserializarDisponibilidad(List<String> datos) {
        List<Bloque> out = new ArrayList<>();
        if (datos == null) return out;
        for (String raw : datos) {
            if (raw == null || raw.isBlank()) continue;
            String[] p = raw.split("\\|");
            if (p.length != 3) continue;
            out.add(new Bloque(p[0], p[1], p[2]));
        }
        return out;
    }

    private List<Bloque> disponibilidadDesdePersistencia(List<String> datos) {
        List<Bloque> d = deserializarDisponibilidad(datos);
        return d.isEmpty() ? disponibilidadPorDefecto() : d;
    }
}
