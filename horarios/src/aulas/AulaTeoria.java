package aulas;

public class AulaTeoria extends Aula {
    private boolean aire;
    private boolean multimedia;

    public AulaTeoria(String nombre, int numero, String ubicacion,
                      int capacidad, boolean aire, boolean multimedia) {
        super(nombre, numero, ubicacion, capacidad);
        this.aire       = aire;
        this.multimedia = multimedia;
    }

    public boolean isAire()       { return aire; }
    public boolean isMultimedia() { return multimedia; }
    public void setAire(boolean a)       { this.aire = a; }
    public void setMultimedia(boolean m) { this.multimedia = m; }

    @Override public String tipo() { return "teoria"; }

    @Override
    public String detalles() {
        return toString()
             + "\nAire: " + (aire ? "Si" : "No")
             + " | Multimedia: " + (multimedia ? "Si" : "No");
    }
}
