package datos;

import aulas.Aula;
import horario.Semestre;

import java.util.ArrayList;
import java.util.List;

/**
 * Escenarios derivados de DatosMemoria JSON.
 * 1: semestres 1-2, 2: semestres 3-4, 3: semestres 5-6.
 */
public class Escenarios {

    private static final DatosMemoria FUENTE = new DatosMemoria();

    public static List<Semestre> escenario1() {
        List<Semestre> semestres = FUENTE.crearSemestres(1, 2);
        return semestres.isEmpty() ? FUENTE.crearSemestres(1, 1) : semestres;
    }

    public static List<Semestre> escenario2() {
        List<Semestre> semestres = FUENTE.crearSemestres(3, 4);
        return semestres.isEmpty() ? FUENTE.crearSemestres(2, 3) : semestres;
    }

    public static List<Semestre> escenario3() {
        List<Semestre> semestres = FUENTE.crearSemestres(5, 6);
        return semestres.isEmpty() ? FUENTE.crearSemestres(4, 5) : semestres;
    }

    public static List<Aula> aulasEscenario1() {
        return new ArrayList<>(FUENTE.obtenerAulas());
    }

    public static List<Aula> aulasEscenario2() {
        return new ArrayList<>(FUENTE.obtenerAulas());
    }

    public static List<Aula> aulasEscenario3() {
        return new ArrayList<>(FUENTE.obtenerAulas());
    }
}
