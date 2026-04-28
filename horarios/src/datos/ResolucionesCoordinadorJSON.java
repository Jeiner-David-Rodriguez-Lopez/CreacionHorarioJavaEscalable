package datos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ResolucionesCoordinadorJSON {

    static final class ProfesorExtra {
        final String nombre;
        final String departamento;
        final String cedula;
        final List<String> disponibilidad;

        ProfesorExtra(String nombre, String departamento, String cedula) {
            this(nombre, departamento, cedula, Collections.emptyList());
        }

        ProfesorExtra(String nombre, String departamento, String cedula, List<String> disponibilidad) {
            this.nombre = nombre;
            this.departamento = departamento;
            this.cedula = cedula;
            this.disponibilidad = disponibilidad == null ? Collections.emptyList() : new ArrayList<>(disponibilidad);
        }
    }

    static final class AulaExtra {
        final String tipo;
        final String nombre;
        final int numero;
        final String ubicacion;
        final int capacidad;

        AulaExtra(String tipo, String nombre, int numero, String ubicacion, int capacidad) {
            this.tipo = tipo;
            this.nombre = nombre;
            this.numero = numero;
            this.ubicacion = ubicacion;
            this.capacidad = capacidad;
        }
    }

    static final class Datos {
        final List<ProfesorExtra> profesoresExtra = new ArrayList<>();
        final List<AulaExtra> aulasExtra = new ArrayList<>();
        final List<String> materiasVirtuales = new ArrayList<>();
    }

    private static final Path RUTA_1 = Path.of("horarios", "src", "datos", "ResolucionesCoordinador.json");
    private static final Path RUTA_2 = Path.of("src", "datos", "ResolucionesCoordinador.json");

    private ResolucionesCoordinadorJSON() {
    }

    static Datos cargar() {
        String json = leerJson();
        Datos datos = new Datos();
        if (json == null || json.isBlank()) {
            return datos;
        }

        String arrProfes = extraerArreglo(json, "profesoresExtra");
        if (arrProfes != null) {
            for (String obj : extraerObjetos(arrProfes)) {
                String nombre = obtenerTexto(obj, "nombre");
                String depto = obtenerTexto(obj, "departamento");
                String cedula = obtenerTexto(obj, "cedula");
                List<String> disponibilidad = obtenerArregloTextos(obj, "disponibilidad");
                if (nombre == null || nombre.isBlank() || depto == null || depto.isBlank()) continue;
                if (cedula == null || cedula.isBlank()) {
                    cedula = "EXT-" + (datos.profesoresExtra.size() + 1);
                }
                datos.profesoresExtra.add(new ProfesorExtra(nombre.trim(), depto.trim(), cedula.trim(), disponibilidad));
            }
        }

        String arrAulas = extraerArreglo(json, "aulasExtra");
        if (arrAulas != null) {
            for (String obj : extraerObjetos(arrAulas)) {
                String tipo = obtenerTexto(obj, "tipo");
                String nombre = obtenerTexto(obj, "nombre");
                String ubicacion = obtenerTexto(obj, "ubicacion");
                int numero = obtenerEntero(obj, "numero");
                int capacidad = obtenerEntero(obj, "capacidad");
                if (tipo == null || tipo.isBlank() || nombre == null || nombre.isBlank()) continue;
                if (numero <= 0) continue;
                if (capacidad <= 0) capacidad = 30;
                if (ubicacion == null || ubicacion.isBlank()) ubicacion = "Edif. Anexo";
                datos.aulasExtra.add(new AulaExtra(tipo.trim(), nombre.trim(), numero, ubicacion.trim(), capacidad));
            }
        }

        String arrVirtuales = extraerArreglo(json, "materiasVirtuales");
        if (arrVirtuales != null) {
            Matcher m = Pattern.compile("\"(.*?)\"", Pattern.DOTALL).matcher(arrVirtuales);
            while (m.find()) {
                String materia = m.group(1);
                if (materia != null && !materia.isBlank()) {
                    datos.materiasVirtuales.add(materia.trim());
                }
            }
        }
        return datos;
    }

    static void guardar(Datos datos) {
        if (datos == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"profesoresExtra\": [\n");
        for (int i = 0; i < datos.profesoresExtra.size(); i++) {
            ProfesorExtra p = datos.profesoresExtra.get(i);
            sb.append("    { \"nombre\": \"").append(escape(p.nombre))
              .append("\", \"departamento\": \"").append(escape(p.departamento))
              .append("\", \"cedula\": \"").append(escape(p.cedula)).append("\"");
            sb.append(", \"disponibilidad\": [");
            for (int j = 0; j < p.disponibilidad.size(); j++) {
                sb.append("\"").append(escape(p.disponibilidad.get(j))).append("\"");
                if (j < p.disponibilidad.size() - 1) sb.append(", ");
            }
            sb.append("] }");
            if (i < datos.profesoresExtra.size() - 1) sb.append(',');
            sb.append('\n');
        }
        sb.append("  ],\n");

        sb.append("  \"aulasExtra\": [\n");
        for (int i = 0; i < datos.aulasExtra.size(); i++) {
            AulaExtra a = datos.aulasExtra.get(i);
            sb.append("    { \"tipo\": \"").append(escape(a.tipo))
              .append("\", \"nombre\": \"").append(escape(a.nombre))
              .append("\", \"numero\": ").append(a.numero)
              .append(", \"ubicacion\": \"").append(escape(a.ubicacion))
              .append("\", \"capacidad\": ").append(a.capacidad).append(" }");
            if (i < datos.aulasExtra.size() - 1) sb.append(',');
            sb.append('\n');
        }
        sb.append("  ],\n");

        sb.append("  \"materiasVirtuales\": [");
        for (int i = 0; i < datos.materiasVirtuales.size(); i++) {
            sb.append("\"").append(escape(datos.materiasVirtuales.get(i))).append("\"");
            if (i < datos.materiasVirtuales.size() - 1) sb.append(", ");
        }
        sb.append("]\n");
        sb.append("}\n");

        escribirJson(sb.toString());
    }

    private static String leerJson() {
        for (Path p : Arrays.asList(RUTA_1, RUTA_2)) {
            try {
                if (Files.exists(p)) {
                    return Files.readString(p, StandardCharsets.UTF_8);
                }
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    private static void escribirJson(String json) {
        IOException err = null;
        for (Path p : Arrays.asList(RUTA_1, RUTA_2)) {
            try {
                Path parent = p.getParent();
                if (parent != null) Files.createDirectories(parent);
                Files.writeString(p, json, StandardCharsets.UTF_8);
                return;
            } catch (IOException ex) {
                err = ex;
            }
        }
        if (err != null) {
            System.err.println("No se pudo guardar ResolucionesCoordinador.json: " + err.getMessage());
        }
    }

    private static String extraerArreglo(String json, String nombre) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(nombre) + "\"\\s*:\\s*\\[(.*?)]\\s*(,|\\})", Pattern.DOTALL);
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private static List<String> extraerObjetos(String arrayJson) {
        List<String> out = new ArrayList<>();
        Matcher m = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL).matcher(arrayJson);
        while (m.find()) {
            out.add(m.group(1));
        }
        return out;
    }

    private static String obtenerTexto(String obj, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"(.*?)\"", Pattern.DOTALL);
        Matcher m = p.matcher(obj);
        return m.find() ? m.group(1) : null;
    }

    private static int obtenerEntero(String obj, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(\\d+)");
        Matcher m = p.matcher(obj);
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static List<String> obtenerArregloTextos(String obj, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\\[(.*?)]", Pattern.DOTALL);
        Matcher m = p.matcher(obj);
        if (!m.find()) return Collections.emptyList();
        List<String> out = new ArrayList<>();
        Matcher item = Pattern.compile("\"(.*?)\"", Pattern.DOTALL).matcher(m.group(1));
        while (item.find()) {
            String v = item.group(1);
            if (v != null && !v.isBlank()) out.add(v.trim());
        }
        return out;
    }
}
