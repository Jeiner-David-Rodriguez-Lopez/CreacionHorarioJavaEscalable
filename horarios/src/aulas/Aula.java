package aulas;

public abstract class Aula {
    protected String nombre;
    protected int numero;
    protected String ubicacion;
    protected int capacidad;

    public Aula(String nombre, int numero, String ubicacion, int capacidad) {
        this.nombre    = nombre;
        this.numero    = numero;
        this.ubicacion = ubicacion;
        this.capacidad = capacidad;
    }

    public String getNombre()    { return nombre; }
    public int getNumero()       { return numero; }
    public String getUbicacion() { return ubicacion; }
    public int getCapacidad()    { return capacidad; }

    public void setNombre(String n)    { this.nombre = n; }
    public void setNumero(int n)       { this.numero = n; }
    public void setUbicacion(String u) { this.ubicacion = u; }
    public void setCapacidad(int c)    { this.capacidad = c; }

    public abstract String tipo();
    public abstract String detalles();

    @Override public String toString() {
        return "Aula " + numero + " - " + nombre + " (" + ubicacion + ", cap:" + capacidad + ")";
    }
}
