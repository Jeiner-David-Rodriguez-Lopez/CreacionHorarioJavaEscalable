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
        generarGlobal(semestres, aulas, semestres);
    }

    public static void generarGlobal(List<Semestre> semestresObjetivo, List<Aula> aulas, List<Semestre> semestresExistentes) {
        Map<String, Set<String>> ocupacionProfesores = new HashMap<>();
        Map<Integer, Set<String>> ocupacionAulas = new HashMap<>();
        Random rnd = new Random();

        precargarOcupacion(semestresExistentes, ocupacionProfesores, ocupacionAulas);

        for (Semestre s : semestresObjetivo) {
            if (s.isGenerado()) {
                continue;
            }
            s.generar(aulas, ocupacionProfesores, ocupacionAulas, rnd);
        }
    }

    private static void precargarOcupacion(
            List<Semestre> semestres,
            Map<String, Set<String>> ocupacionProfesores,
            Map<Integer, Set<String>> ocupacionAulas
    ) {
        if (semestres == null) return;

        for (Semestre semestre : semestres) {
            if (semestre == null || !semestre.isGenerado()) continue;

            for (EntradaHorario entrada : semestre.getHorario()) {
                if (entrada == null || entrada.getBloque() == null || entrada.getAula() == null) continue;

                String bloqueKey = entrada.getBloque().getDia() + "|" + entrada.getBloque().getInicio();
                int numeroAula = entrada.getAula().getNumero();
                ocupacionAulas.computeIfAbsent(numeroAula, k -> new java.util.HashSet<>()).add(bloqueKey);

                if (entrada.getAsignatura() != null && entrada.getAsignatura().getProfesor() != null) {
                    String nombreProfesor = entrada.getAsignatura().getProfesor().getNombre();
                    ocupacionProfesores.computeIfAbsent(nombreProfesor, k -> new java.util.HashSet<>()).add(bloqueKey);
                }
            }
        }
    }
}
