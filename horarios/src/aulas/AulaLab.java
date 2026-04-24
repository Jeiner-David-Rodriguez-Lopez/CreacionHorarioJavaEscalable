package aulas;

import java.util.List;

public class AulaLab extends Aula {
    private List<String> equipo;

    public AulaLab(String nombre, int numero, String ubicacion,
                   int capacidad, List<String> equipo) {
        super(nombre, numero, ubicacion, capacidad);
        this.equipo = equipo;
    }

    public List<String> getEquipo() { return equipo; }
    public void setEquipo(List<String> e) { this.equipo = e; }
    public int totalEquipo() { return equipo.size(); }

    @Override public String tipo() { return "laboratorio"; }

    @Override
    public String detalles() {
        return toString()
             + "\nEquipo (" + totalEquipo() + "): " + String.join(", ", equipo);
    }
}
