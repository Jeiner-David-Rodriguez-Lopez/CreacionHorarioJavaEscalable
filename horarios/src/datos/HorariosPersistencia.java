package datos;

import asignaturas.Asignatura;
import aulas.Aula;
import horario.Bloque;
import horario.EntradaHorario;
import horario.Semestre;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class HorariosPersistencia {
    private static final Path RUTA_PRINCIPAL = Path.of("horarios", "data", "horarios_generados.dat");
    private static final Path RUTA_SECUNDARIA = Path.of("data", "horarios_generados.dat");
    private static final String VERSION = "HORARIOS_V1";

    private HorariosPersistencia() {
    }

    public static void guardar(Collection<Semestre> semestres) {
        if (semestres == null) return;

        StringBuilder sb = new StringBuilder(VERSION).append('\n');
        for (Semestre s : semestres) {
            if (s == null || !s.isGenerado()) continue;
            for (EntradaHorario e : s.getHorario()) {
                if (e == null || e.getBloque() == null || e.getAula() == null || e.getAsignatura() == null) continue;

                sb.append(s.getNumero()).append('|')
                  .append(e.getAula().getNumero()).append('|')
                  .append(encode(e.getAsignatura().getNombre())).append('|')
                  .append(encode(e.getBloque().getDia())).append('|')
                  .append(encode(e.getBloque().getInicio())).append('|')
                  .append(encode(e.getBloque().getFin())).append('|')
                  .append(encode(e.getConflicto() == null ? "" : e.getConflicto()))
                  .append('\n');
            }
        }

        escribirArchivo(sb.toString());
    }

    public static void cargar(Map<Integer, Semestre> semestresPorNumero, List<Aula> aulas) {
        if (semestresPorNumero == null || semestresPorNumero.isEmpty() || aulas == null) return;

        String contenido = leerArchivo();
        if (contenido == null || contenido.isBlank()) return;

        String[] lineas = contenido.split("\\R");
        if (lineas.length == 0 || !VERSION.equals(lineas[0].trim())) return;

        Map<Integer, Aula> aulaPorNumero = new HashMap<>();
        for (Aula aula : aulas) {
            aulaPorNumero.put(aula.getNumero(), aula);
        }

        Map<Integer, List<EntradaHorario>> entradasPorSemestre = new HashMap<>();

        for (int i = 1; i < lineas.length; i++) {
            String linea = lineas[i].trim();
            if (linea.isEmpty()) continue;

            String[] partes = linea.split("\\|", -1);
            if (partes.length != 7) continue;

            try {
                int numSemestre = Integer.parseInt(partes[0]);
                int numAula = Integer.parseInt(partes[1]);
                String nombreAsig = decode(partes[2]);
                String dia = decode(partes[3]);
                String inicio = decode(partes[4]);
                String fin = decode(partes[5]);
                String conflicto = decode(partes[6]);

                Semestre semestre = semestresPorNumero.get(numSemestre);
                if (semestre == null) continue;
                Aula aula = aulaPorNumero.get(numAula);
                if (aula == null) continue;
                Asignatura asignatura = buscarAsignatura(semestre, nombreAsig);
                if (asignatura == null) continue;

                EntradaHorario entrada = new EntradaHorario(asignatura, aula, new Bloque(dia, inicio, fin));
                if (!conflicto.isBlank()) {
                    entrada.setConflicto(conflicto);
                }
                entradasPorSemestre.computeIfAbsent(numSemestre, k -> new ArrayList<>()).add(entrada);
            } catch (Exception ignored) {
            }
        }

        for (Map.Entry<Integer, List<EntradaHorario>> entry : entradasPorSemestre.entrySet()) {
            Semestre s = semestresPorNumero.get(entry.getKey());
            if (s != null) {
                s.reemplazarHorario(entry.getValue());
            }
        }
    }

    public static void limpiarArchivos() {
        for (Path ruta : new Path[]{RUTA_PRINCIPAL, RUTA_SECUNDARIA}) {
            try {
                Files.deleteIfExists(ruta);
            } catch (IOException ignored) {
            }
        }
    }

    private static Asignatura buscarAsignatura(Semestre semestre, String nombre) {
        String objetivo = normalizar(nombre);
        for (Asignatura a : semestre.getAsignaturas()) {
            if (normalizar(a.getNombre()).equals(objetivo)) {
                return a;
            }
        }
        return null;
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
                if (parent != null) {
                    Files.createDirectories(parent);
                }
                Files.writeString(ruta, contenido, StandardCharsets.UTF_8);
                return;
            } catch (IOException ex) {
                error = ex;
            }
        }
        if (error != null) {
            System.err.println("No se pudo guardar horarios persistidos: " + error.getMessage());
        }
    }

    private static String leerArchivo() {
        for (Path ruta : new Path[]{RUTA_PRINCIPAL, RUTA_SECUNDARIA}) {
            try {
                if (Files.exists(ruta)) {
                    return Files.readString(ruta, StandardCharsets.UTF_8);
                }
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    private static String normalizar(String s) {
        if (s == null) return "";
        String n = s.toLowerCase(Locale.ROOT).trim();
        n = Normalizer.normalize(n, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return n;
    }
}
