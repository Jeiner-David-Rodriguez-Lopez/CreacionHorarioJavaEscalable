package asignaturas;

import usuarios.Profesor;
import java.util.List;

public class Practica extends Asignatura {
    private String so;
    private List<String> materiales;

    public Practica(String nombre, int creditos, Profesor profesor,
                    boolean esCarrera, String so, List<String> materiales) {
        super(nombre, creditos, profesor, esCarrera);
        this.so         = so;
        this.materiales = materiales;
    }

    public String getSo()               { return so; }
    public List<String> getMateriales() { return materiales; }
    public void setSo(String s)                 { this.so = s; }
    public void setMateriales(List<String> m)   { this.materiales = m; }

    @Override public String tipo()     { return "Practica"; }
    @Override public String tipoAula() { return "laboratorio"; }

    @Override
    public String detalles() {
        return "Tipo: Practica"
             + "\nCreditos: " + creditos + " (" + horasSemanales() + " hrs/sem)"
             + "\nProfesor: " + (profesor != null ? profesor.getNombre() : "Sin asignar")
             + "\nS.O.: " + so
             + "\nMateriales: " + String.join(", ", materiales)
             + "\nCarrera propia: " + (esCarrera ? "Si" : "Servicio");
    }
}
