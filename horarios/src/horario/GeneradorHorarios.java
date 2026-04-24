package horario;

import aulas.Aula;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class GeneradorHorarios {

    private GeneradorHorarios() {
    }

    public static void generarGlobal(List<Semestre> semestres, List<Aula> aulas) {
        Map<String, Set<String>> ocupacionProfesores = new HashMap<>();
        Map<Integer, Set<String>> ocupacionAulas = new HashMap<>();
        Random rnd = new Random();

        for (Semestre s : semestres) {
            s.generar(aulas, ocupacionProfesores, ocupacionAulas, rnd);
        }
    }
}
