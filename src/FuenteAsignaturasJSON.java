import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FuenteAsignaturasJSON implements FuenteAsignaturas {
    private static final String NOMBRE_ARCHIVO = "asignaturas.json";
    private final Path archivoPath;
    private List<Asignatura> listaAsignaturas;

    public FuenteAsignaturasJSON() {
        archivoPath = resolverRutaArchivo();
        listaAsignaturas = cargarDesdeJSON();
    }

    @Override
    public List<Asignatura> obtenerAsignaturas() {
        return listaAsignaturas;
    }

    @Override
    public void agregarAsignatura(Asignatura a) {
        listaAsignaturas.add(a);
        guardarEnJSON();
    }

    @Override
    public void eliminarAsignatura(String nombre) {
        listaAsignaturas.removeIf(a -> a.getNombre().equalsIgnoreCase(nombre));
        guardarEnJSON();
    }

    @Override
    public void actualizarCreditos(String nombre, int nuevosCreditos) {
        for (Asignatura a : listaAsignaturas) {
            if (a.getNombre().equalsIgnoreCase(nombre)) {
                a.setCreditos(nuevosCreditos);
            }
        }
        guardarEnJSON();
    }

    private List<Asignatura> cargarDesdeJSON() {
        try (Reader reader = Files.newBufferedReader(archivoPath, StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            Asignatura[] array = gson.fromJson(reader, Asignatura[].class);
            if (array == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(array));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void guardarEnJSON() {
        try {
            Path parent = archivoPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (Writer writer = Files.newBufferedWriter(archivoPath, StandardCharsets.UTF_8)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(listaAsignaturas, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Path resolverRutaArchivo() {
        Path[] candidatas = new Path[] {
                Paths.get(NOMBRE_ARCHIVO),
                Paths.get("src", NOMBRE_ARCHIVO)
        };

        for (Path candidata : candidatas) {
            if (Files.exists(candidata)) {
                return candidata;
            }
        }

        return Paths.get("src", NOMBRE_ARCHIVO);
    }
}
