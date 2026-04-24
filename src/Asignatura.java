import java.util.ArrayList;
import java.util.List;
public class Asignatura {
    private String nombre;
    private int creditos;
    private static List<Asignatura> listaAsignaturas = new ArrayList<>();



    public Asignatura(String nombre, int creditos) {
        this.nombre = nombre;
        this.creditos = creditos;
    }


    // Getters
    public String getNombre() { return nombre; }
    public int getCreditos() { return creditos; }

    public static List<Asignatura> getListaAsignaturas() {
        return listaAsignaturas;
    }

    public static void setListaAsignaturas(List<Asignatura> listaAsignaturas) {
        Asignatura.listaAsignaturas = listaAsignaturas;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Asignatura{" +
                "nombre='" + nombre + '\'' +
                ", creditos=" + creditos +
                '}';
    }
}

