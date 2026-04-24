package horario;

import asignaturas.Asignatura;
import aulas.Aula;

public class EntradaHorario {
    private Asignatura asignatura;
    private Aula aula;
    private Bloque bloque;
    private String conflicto;

    public EntradaHorario(Asignatura asignatura, Aula aula, Bloque bloque) {
        this.asignatura = asignatura;
        this.aula       = aula;
        this.bloque     = bloque;
        this.conflicto  = null;
    }

    public Asignatura getAsignatura()    { return asignatura; }
    public Aula getAula()                { return aula; }
    public Bloque getBloque()            { return bloque; }
    public String getConflicto()         { return conflicto; }
    public void setConflicto(String c)   { this.conflicto = c; }
    public boolean tieneConflicto()      { return conflicto != null; }

    @Override public String toString() {
        return bloque + " | " + asignatura.getNombre() + " | Aula " + aula.getNumero()
             + (tieneConflicto() ? " [!] " + conflicto : "");
    }
}
