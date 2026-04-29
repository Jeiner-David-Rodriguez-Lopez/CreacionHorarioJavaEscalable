package datos;

import asignaturas.Asignatura;
import aulas.Aula;
import horario.Semestre;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class InfoAcademicaPersistencia {
    private static final Path RUTA_PRINCIPAL = Path.of("horarios", "data", "info_academica.dat");
    private static final Path RUTA_SECUNDARIA = Path.of("data", "info_academica.dat");
    private static final String VERSION = "INFO_ACADEMICA_V1";
    private static final String[] AMENIDADES = {
            "Aire acondicionado",
            "Equipo multimedia",
            "Aire acondicionado y equipo multimedia"
    };

    private static final Map<Integer, String> amenidadesPorAula = new HashMap<>();
    private static final Map<String, CursoInfo> cursos = new HashMap<>();
    private static final Random random = new Random();
    private static boolean cargado = false;

    private InfoAcademicaPersistencia() {
    }

    public static synchronized String amenidadesAula(Aula aula) {
        cargarSiHaceFalta();
        if (aula == null || esVirtual(aula)) return "-";
        String valor = amenidadesPorAula.get(aula.getNumero());
        if (valor == null) {
            valor = AMENIDADES[random.nextInt(AMENIDADES.length)];
            amenidadesPorAula.put(aula.getNumero(), valor);
            guardar();
        }
        return valor;
    }

    public static synchronized CursoInfo infoCurso(Semestre semestre, Asignatura asignatura) {
        cargarSiHaceFalta();
        String key = cursoKey(semestre, asignatura);
        CursoInfo info = cursos.get(key);
        if (info == null) {
            int cupo = 20 + random.nextInt(11);
            int registrados = semestre != null && semestre.isGenerado() ? random.nextInt(cupo + 1) : 0;
            info = new CursoInfo(cupo, registrados);
            cursos.put(key, info);
            guardar();
        }
        return info;
    }

    public static synchronized boolean matricular(Semestre semestre, Asignatura asignatura) {
        CursoInfo info = infoCurso(semestre, asignatura);
        if (info.registrados >= info.cupo) return false;
        info.registrados++;
        guardar();
        return true;
    }

    private static void cargarSiHaceFalta() {
        if (cargado) return;
        cargado = true;
        String contenido = leerArchivo();
        if (contenido == null || contenido.isBlank()) return;

        String[] lineas = contenido.split("\\R");
        if (lineas.length == 0 || !VERSION.equals(lineas[0].trim())) return;

        for (int i = 1; i < lineas.length; i++) {
            String linea = lineas[i].trim();
            if (linea.isEmpty()) continue;
            String[] p = linea.split("\\|", -1);
            try {
                if (p.length == 3 && "A".equals(p[0])) {
                    amenidadesPorAula.put(Integer.parseInt(p[1]), decode(p[2]));
                } else if (p.length == 5 && "C".equals(p[0])) {
                    String key = p[1] + "|" + decode(p[2]);
                    cursos.put(key, new CursoInfo(Integer.parseInt(p[3]), Integer.parseInt(p[4])));
                }
            } catch (Exception ignored) {
            }
        }
    }

    private static void guardar() {
        StringBuilder sb = new StringBuilder(VERSION).append('\n');
        for (Map.Entry<Integer, String> e : amenidadesPorAula.entrySet()) {
            sb.append("A|")
              .append(e.getKey()).append('|')
              .append(encode(e.getValue()))
              .append('\n');
        }
        for (Map.Entry<String, CursoInfo> e : cursos.entrySet()) {
            String[] partes = e.getKey().split("\\|", 2);
            if (partes.length != 2) continue;
            CursoInfo info = e.getValue();
            sb.append("C|")
              .append(partes[0]).append('|')
              .append(encode(partes[1])).append('|')
              .append(info.cupo).append('|')
              .append(info.registrados)
              .append('\n');
        }
        escribirArchivo(sb.toString());
    }

    private static String cursoKey(Semestre semestre, Asignatura asignatura) {
        int numeroSemestre = semestre == null ? 0 : semestre.getNumero();
        String nombre = asignatura == null ? "" : normalizar(asignatura.getNombre());
        return numeroSemestre + "|" + nombre;
    }

    private static boolean esVirtual(Aula aula) {
        return aula.getNombre() != null && aula.getNombre().startsWith("Virtual");
    }

    private static String encode(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String b64) {
        byte[] data = Base64.getDecoder().decode(b64);
        return new String(data, StandardCharsets.UTF_8);
    }

    private static void escribirArchivo(String contenido) {
        IOException error = null;
        for (Path ruta : new Path[]{RUTA_PRINCIPAL, RUTA_SECUNDARIA}) {
            try {
                Path parent = ruta.getParent();
                if (parent != null) Files.createDirectories(parent);
                Files.writeString(ruta, contenido, StandardCharsets.UTF_8);
                return;
            } catch (IOException ex) {
                error = ex;
            }
        }
        if (error != null) {
            System.err.println("No se pudo guardar info academica: " + error.getMessage());
        }
    }

    private static String leerArchivo() {
        for (Path ruta : new Path[]{RUTA_PRINCIPAL, RUTA_SECUNDARIA}) {
            try {
                if (Files.exists(ruta)) return Files.readString(ruta, StandardCharsets.UTF_8);
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    private static String normalizar(String s) {
        if (s == null) return "";
        String n = s.toLowerCase().trim();
        return Normalizer.normalize(n, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
    }

    public static class CursoInfo {
        private final int cupo;
        private int registrados;

        private CursoInfo(int cupo, int registrados) {
            this.cupo = cupo;
            this.registrados = registrados;
        }

        public int getCupo() {
            return cupo;
        }

        public int getRegistrados() {
            return registrados;
        }
    }
}
