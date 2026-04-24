package usuarios;

import asignaturas.Asignatura;
import asignaturas.Teorica;
import asignaturas.Practica;
import datos.FuenteAsignaturas;
import java.util.List;

public class Coordinador extends Usuario {
    private String nombre;
    private FuenteAsignaturas fuente;

    public Coordinador(String nombre, FuenteAsignaturas fuente) {
        this.id     = "3";
        this.nombre = nombre;
        this.fuente = fuente;
    }

    @Override public String getId()     { return id; }
    @Override public String getNombre() { return nombre; }

    public List<Asignatura> obtenerMaterias()   { return fuente.obtenerMaterias(); }
    public List<Profesor>   obtenerProfesores() { return fuente.obtenerProfesores(); }
    public List<Teorica>    obtenerTeoricas()   { return fuente.obtenerTeoricas(); }
    public List<Practica>   obtenerPracticas()  { return fuente.obtenerPracticas(); }

    public void actualizarCreditos(String nombre, int c) {
        fuente.obtenerMaterias().stream()
              .filter(m -> m.getNombre().equalsIgnoreCase(nombre))
              .findFirst().ifPresent(m -> m.setCreditos(c));
    }

    @Override public String toString() { return "Coordinador: " + nombre; }
}
