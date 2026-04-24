package horario;

import asignaturas.Asignatura;
import aulas.Aula;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Semestre {
    private int numero;
    private List<Asignatura> asignaturas;
    private List<EntradaHorario> horario;
    private boolean generado;

    public Semestre(int numero, List<Asignatura> asignaturas) {
        this.numero      = numero;
        this.asignaturas = asignaturas;
        this.horario     = new ArrayList<>();
        this.generado    = false;
    }

    public int getNumero()                        { return numero; }
    public List<Asignatura> getAsignaturas()      { return asignaturas; }
    public List<EntradaHorario> getHorario()      { return horario; }
    public boolean isGenerado()                   { return generado; }

    public void generar(List<Aula> aulas) {
        generar(aulas, Collections.emptyMap(), Collections.emptyMap(), new Random());
    }

    public void generar(
            List<Aula> aulas,
            Map<String, Set<String>> ocupacionProfesorGlobal,
            Map<Integer, Set<String>> ocupacionAulaGlobal,
            Random rnd
    ) {
        horario.clear();
        List<Aula> aulasCompatibles = new ArrayList<>(aulas);
        List<Bloque> todosBloques = construirBloques();
        Collections.shuffle(todosBloques, rnd);
        Collections.shuffle(aulasCompatibles, rnd);

        Set<String> ocupacionProfesorLocal = new HashSet<>();
        Set<String> ocupacionAulaLocal = new HashSet<>();

        for (Asignatura a : asignaturas) {
            int horas = Math.max(1, a.horasSemanales());

            for (int i = 0; i < horas; i++) {
                Asignacion asignacion = buscarMejorAsignacion(
                        a, aulasCompatibles, todosBloques,
                        ocupacionProfesorLocal, ocupacionAulaLocal,
                        ocupacionProfesorGlobal, ocupacionAulaGlobal, rnd
                );

                EntradaHorario entrada = new EntradaHorario(a, asignacion.aula, asignacion.bloque);
                if (asignacion.conflicto != null) {
                    entrada.setConflicto(asignacion.conflicto);
                } else {
                    String key = keyBloque(asignacion.bloque);
                    if (a.getProfesor() != null) {
                        String pKey = a.getProfesor().getNombre() + "|" + key;
                        ocupacionProfesorLocal.add(pKey);
                        ocupacionProfesorGlobal
                                .computeIfAbsent(a.getProfesor().getNombre(), k -> new HashSet<>())
                                .add(key);
                    }

                    String aKey = asignacion.aula.getNumero() + "|" + key;
                    ocupacionAulaLocal.add(aKey);
                    ocupacionAulaGlobal
                            .computeIfAbsent(asignacion.aula.getNumero(), k -> new HashSet<>())
                            .add(key);
                }
                horario.add(entrada);
            }
        }
        generado = true;
    }

    private Asignacion buscarMejorAsignacion(
            Asignatura a,
            List<Aula> aulas,
            List<Bloque> bloques,
            Set<String> ocupacionProfesorLocal,
            Set<String> ocupacionAulaLocal,
            Map<String, Set<String>> ocupacionProfesorGlobal,
            Map<Integer, Set<String>> ocupacionAulaGlobal,
            Random rnd
    ) {
        List<Bloque> candidatosBloque = new ArrayList<>(bloques);
        Collections.shuffle(candidatosBloque, rnd);

        for (Bloque b : candidatosBloque) {
            String bloqueKey = keyBloque(b);
            if (profesorOcupado(a, bloqueKey, ocupacionProfesorLocal, ocupacionProfesorGlobal)) continue;

            List<Aula> compatibles = aulasCompatibles(a, aulas, rnd);
            for (Aula au : compatibles) {
                String aKey = au.getNumero() + "|" + bloqueKey;
                boolean aulaLibreLocal = !ocupacionAulaLocal.contains(aKey);
                boolean aulaLibreGlobal = !ocupacionAulaGlobal
                        .getOrDefault(au.getNumero(), Collections.emptySet()).contains(bloqueKey);
                if (aulaLibreLocal && aulaLibreGlobal) {
                    return new Asignacion(b, au, null);
                }
            }
        }

        // Fallback con conflicto
        Bloque bloque = bloques.get(rnd.nextInt(bloques.size()));
        List<Aula> compatibles = aulasCompatibles(a, aulas, rnd);
        Aula aula = compatibles.isEmpty() ? aulas.get(0) : compatibles.get(0);
        String conflicto = "Sin combinacion libre (profesor/aula)";
        return new Asignacion(bloque, aula, conflicto);
    }

    private boolean profesorOcupado(
            Asignatura a,
            String bloqueKey,
            Set<String> ocupacionProfesorLocal,
            Map<String, Set<String>> ocupacionProfesorGlobal
    ) {
        if (a.getProfesor() == null) return false;
        String nombre = a.getProfesor().getNombre();
        String pKey = nombre + "|" + bloqueKey;
        if (ocupacionProfesorLocal.contains(pKey)) return true;
        return ocupacionProfesorGlobal.getOrDefault(nombre, Collections.emptySet()).contains(bloqueKey);
    }

    private List<Aula> aulasCompatibles(Asignatura a, List<Aula> aulas, Random rnd) {
        List<Aula> out = new ArrayList<>();
        for (Aula au : aulas) {
            if (au.tipo().equals(a.tipoAula())) out.add(au);
        }
        Collections.shuffle(out, rnd);
        return out;
    }

    private List<Bloque> construirBloques() {
        List<Bloque> bloques = new ArrayList<>();
        for (String d : Bloque.DIAS) {
            for (String[] s : Bloque.SLOTS) {
                bloques.add(new Bloque(d, s[0], s[1]));
            }
        }
        return bloques;
    }

    private String keyBloque(Bloque b) {
        return b.getDia() + "|" + b.getInicio();
    }

    private static class Asignacion {
        private final Bloque bloque;
        private final Aula aula;
        private final String conflicto;

        private Asignacion(Bloque bloque, Aula aula, String conflicto) {
            this.bloque = bloque;
            this.aula = aula;
            this.conflicto = conflicto;
        }
    }

    @Override public String toString() { return "Semestre " + numero; }
}
