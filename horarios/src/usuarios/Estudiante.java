package usuarios;

public class Estudiante extends Usuario {
    private String nombre;

    public Estudiante(String nombre) {
        this.id = "1";
        this.nombre = nombre;
    }

    @Override public String getId()     { return id; }
    @Override public String getNombre() { return nombre; }
    @Override public String toString()  { return "Estudiante: " + nombre; }
}
