package datos;

import asignaturas.Asignatura;
import asignaturas.Teorica;
import asignaturas.Practica;
import usuarios.Profesor;
import java.util.List;

public interface FuenteAsignaturas {
    List<Asignatura> obtenerMaterias();
    List<Teorica>    obtenerTeoricas();
    List<Practica>   obtenerPracticas();
    List<Profesor>   obtenerProfesores();
}
