package usuarios;

import horario.Bloque;
import java.util.List;

public class Profesor extends Usuario {
    private String nombre;
    private String cedula;
    private String departamento;
    private List<Bloque> disponibilidad;

    public Profesor(String nombre, String cedula, String departamento, List<Bloque> disponibilidad) {
        this.id             = "2";
        this.nombre         = nombre;
        this.cedula         = cedula;
        this.departamento   = departamento;
        this.disponibilidad = disponibilidad;
    }

    @Override public String getId()         { return id; }
    @Override public String getNombre()     { return nombre; }
    public String getCedula()               { return cedula; }
    public String getDepartamento()         { return departamento; }
    public List<Bloque> getDisponibilidad() { return disponibilidad; }

    public void setNombre(String n)       { this.nombre = n; }
    public void setCedula(String c)       { this.cedula = c; }
    public void setDepartamento(String d) { this.departamento = d; }
    public void setDisponibilidad(List<Bloque> disponibilidad) { this.disponibilidad = disponibilidad; }

    @Override public String toString() { return nombre + " (" + departamento + ")"; }
}
