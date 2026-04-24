package datos;

import asignaturas.Asignatura;
import asignaturas.Teorica;
import asignaturas.Practica;
import usuarios.Profesor;
import horario.Bloque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementacion en memoria con datos quemados.
 * Contiene los 12 profesores y 13 materias del JSON original.
 */
public class DatosMemoria implements FuenteAsignaturas {

    private final List<Profesor>   profesores = new ArrayList<>();
    private final List<Asignatura> materias   = new ArrayList<>();
    private final List<Teorica>    teoricas   = new ArrayList<>();
    private final List<Practica>   practicas  = new ArrayList<>();

    public DatosMemoria() {
        cargarProfesores();
        cargarMaterias();
    }

    private void cargarProfesores() {
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

    private Profesor prof(String nombre) {
        return profesores.stream()
                .filter(p -> p.getNombre().equals(nombre))
                .findFirst().orElse(null);
    }

    private void cargarMaterias() {
        // Teoricas
        Teorica prog1  = new Teorica("Programacion I",        4, prof("Ana Padilla"),         true,  "https://tec.ac.cr/prog1",   "Presencial");
        Teorica disc   = new Teorica("Matematica Discreta",   3, prof("Marcos Zamora"),        false, "https://tec.ac.cr/disc",    "Presencial");
        Teorica bd     = new Teorica("Bases de Datos",        4, prof("Julia Campos"),         true,  "https://tec.ac.cr/bd",      "Presencial");
        Teorica redes  = new Teorica("Redes de Computadoras", 3, prof("Francisco Hernandez"),  true,  "https://tec.ac.cr/redes",   "Presencial");
        Teorica prog2  = new Teorica("Programacion II",       4, prof("Ana Padilla"),          true,  "https://tec.ac.cr/prog2",   "Presencial");
        Teorica mat    = new Teorica("Matematica",            2, prof("Marcos Zamora"),         false, "https://tec.ac.cr/mat",     "Presencial");
        Teorica eng1   = new Teorica("Ingles Tecnico I",      4, prof("Carlos Perez"),         false, "https://tec.ac.cr/eng1",    "Presencial");
        Teorica eng2   = new Teorica("Ingles Tecnico II",     3, prof("Oscar Viquez"),         false, "https://tec.ac.cr/eng2",    "Presencial");
        Teorica qf     = new Teorica("Quimica y Fisica",      3, prof("Jean Marin"),           false, "https://tec.ac.cr/qf",      "Presencial");
        Teorica prog3  = new Teorica("Programacion III",      4, prof("Julia Campos"),         true,  "https://tec.ac.cr/prog3",   "Presencial");
        Teorica eng3   = new Teorica("Ingles Tecnico III",    4, prof("Carlos Perez"),         false, "https://tec.ac.cr/eng3",    "Presencial");
        Teorica engC   = new Teorica("Ingles Conversacional", 3, prof("Oscar Viquez"),         false, "https://tec.ac.cr/engconv", "Presencial");

        // Practica
        Practica labQF = new Practica("Lab. Quimica y Fisica", 2, prof("Allan Torrez"), false, "Windows 11",
                Arrays.asList("Microscopio virtual", "Guia Lab Fisica", "PhET Simulations"));

        // Agregar a listas
        teoricas.addAll(Arrays.asList(prog1,disc,bd,redes,prog2,mat,eng1,eng2,qf,prog3,eng3,engC));
        practicas.add(labQF);

        materias.addAll(teoricas);
        materias.addAll(practicas);
    }

    @Override public List<Asignatura> obtenerMaterias()  { return materias; }
    @Override public List<Teorica>    obtenerTeoricas()  { return teoricas; }
    @Override public List<Practica>   obtenerPracticas() { return practicas; }
    @Override public List<Profesor>   obtenerProfesores(){ return profesores; }
}
