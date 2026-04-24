package horario;

import asignaturas.Asignatura;
import aulas.Aula;
import java.util.ArrayList;
import java.util.List;

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
        horario.clear();
        List<Bloque> bloquesUsados = new ArrayList<>();
        List<Aula>   aulasUsadas   = new ArrayList<>();

        int di = 0, si = 0;

        for (Asignatura a : asignaturas) {
            int horas     = a.horasSemanales();
            int asignadas = 0;

            while (asignadas < horas && di < Bloque.DIAS.length) {
                String[] slot = Bloque.SLOTS[si];
                Bloque b = new Bloque(Bloque.DIAS[di], slot[0], slot[1]);

                // Buscar aula libre del tipo correcto
                Aula aulaOk = null;
                for (Aula au : aulas) {
                    if (!au.tipo().equals(a.tipoAula())) continue;
                    boolean libre = true;
                    for (int k = 0; k < bloquesUsados.size(); k++) {
                        if (bloquesUsados.get(k).chocaCon(b) && aulasUsadas.get(k) == au) {
                            libre = false; break;
                        }
                    }
                    if (libre) { aulaOk = au; break; }
                }

                // Verificar choque de profesor
                boolean profOcupado = false;
                for (EntradaHorario eh : horario) {
                    if (eh.getBloque().chocaCon(b)
                            && eh.getAsignatura().getProfesor() != null
                            && a.getProfesor() != null
                            && eh.getAsignatura().getProfesor().getNombre()
                               .equals(a.getProfesor().getNombre())) {
                        profOcupado = true; break;
                    }
                }

                if (aulaOk != null && !profOcupado) {
                    horario.add(new EntradaHorario(a, aulaOk, b));
                    bloquesUsados.add(b);
                    aulasUsadas.add(aulaOk);
                } else {
                    // Registrar con conflicto
                    Aula fallback = aulas.stream()
                            .filter(au -> au.tipo().equals(a.tipoAula()))
                            .findFirst().orElse(aulas.get(0));
                    EntradaHorario eh = new EntradaHorario(a, fallback, b);
                    eh.setConflicto(profOcupado ? "Profesor ocupado" : "Sin aula compatible");
                    horario.add(eh);
                }

                asignadas++;
                si++;
                if (si >= Bloque.SLOTS.length) { si = 0; di++; }
            }
        }
        generado = true;
    }

    @Override public String toString() { return "Semestre " + numero; }
}
