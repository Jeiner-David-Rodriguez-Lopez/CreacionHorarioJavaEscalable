package datos;

import aulas.Aula;
import horario.Semestre;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Escenarios derivados de DatosMemoria JSON.
 * 1: semestres 1-2, 2: semestres 3-4, 3: semestres 5-6, 4: semestres 7-8.
 */
public class Escenarios {

    private static final DatosMemoria FUENTE = new DatosMemoria();
    private static final Map<Integer, Semestre> SEMESTRES = new LinkedHashMap<>();
    private static final boolean SOLO_EJECUCION = true;

    static {
        if (SOLO_EJECUCION) {
            HorariosPersistencia.limpiarArchivos();
        }
        for (Semestre s : FUENTE.crearSemestres(1, 12)) {
            SEMESTRES.put(s.getNumero(), s);
        }
        if (!SOLO_EJECUCION) {
            HorariosPersistencia.cargar(SEMESTRES, FUENTE.obtenerAulas());
        }
    }

    public static List<Semestre> escenario1() {
        List<Semestre> semestres = filtrarSemestres(1, 2);
        return semestres.isEmpty() ? filtrarSemestres(1, 1) : semestres;
    }

    public static List<Semestre> escenario2() {
        List<Semestre> semestres = filtrarSemestres(3, 4);
        return semestres.isEmpty() ? filtrarSemestres(2, 3) : semestres;
    }

    public static List<Semestre> escenario3() {
        List<Semestre> semestres = filtrarSemestres(5, 6);
        return semestres.isEmpty() ? filtrarSemestres(4, 5) : semestres;
    }

    public static List<Semestre> escenario4() {
        List<Semestre> semestres = filtrarSemestres(7, 8);
        return semestres.isEmpty() ? filtrarSemestres(6, 7) : semestres;
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

    public static List<Aula> aulasEscenario4() {
        return new ArrayList<>(FUENTE.obtenerAulas());
    }

    public static List<Semestre> todosLosSemestres() {
        return new ArrayList<>(SEMESTRES.values());
    }

    public static void guardarHorariosGenerados() {
        if (!SOLO_EJECUCION) {
            HorariosPersistencia.guardar(SEMESTRES.values());
        }
    }

    public static void limpiarPersistencia() {
        HorariosPersistencia.limpiarArchivos();
    }

    private static List<Semestre> filtrarSemestres(int desde, int hasta) {
        List<Semestre> out = new ArrayList<>();
        for (int s = desde; s <= hasta; s++) {
            Semestre semestre = SEMESTRES.get(s);
            if (semestre != null) {
                out.add(semestre);
            }
        }
        return out;
    }
}
