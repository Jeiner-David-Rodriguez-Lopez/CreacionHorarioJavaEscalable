package asignaturas;

import usuarios.Profesor;

public abstract class Asignatura {
    protected String nombre;
    protected int creditos;
    protected Profesor profesor;
    protected boolean esCarrera;

    public Asignatura(String nombre, int creditos, Profesor profesor, boolean esCarrera) {
        this.nombre    = nombre;
        this.creditos  = creditos;
        this.profesor  = profesor;
        this.esCarrera = esCarrera;
    }

    public String getNombre()     { return nombre; }
    public int getCreditos()      { return creditos; }
    public Profesor getProfesor() { return profesor; }
    public boolean isEsCarrera()  { return esCarrera; }

    public void setNombre(String n)     { this.nombre = n; }
    public void setCreditos(int c)      { this.creditos = c; }
    public void setProfesor(Profesor p) { this.profesor = p; }

    public int horasSemanales() { return creditos * 3; }

    public abstract String tipo();
    public abstract String tipoAula();
    public abstract String detalles();

    @Override public String toString() {
        return nombre + " [" + tipo() + ", " + creditos + " cr.]";
    }
}
