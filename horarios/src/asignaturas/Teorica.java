package asignaturas;

import usuarios.Profesor;

public class Teorica extends Asignatura {
    private String urlApuntes;
    private String modalidad;

    public Teorica(String nombre, int creditos, Profesor profesor,
                   boolean esCarrera, String urlApuntes, String modalidad) {
        super(nombre, creditos, profesor, esCarrera);
        this.urlApuntes = urlApuntes;
        this.modalidad  = modalidad;
    }

    public String getUrlApuntes()       { return urlApuntes; }
    public String getModalidad()        { return modalidad; }
    public void setUrlApuntes(String u) { this.urlApuntes = u; }
    public void setModalidad(String m)  { this.modalidad = m; }

    @Override public String tipo()     { return "Teorica"; }
    @Override public String tipoAula() { return "teoria"; }

    @Override
    public String detalles() {
        return "Tipo: Teorica"
             + "\nCreditos: " + creditos + " (" + horasSemanales() + " hrs/sem)"
             + "\nProfesor: " + (profesor != null ? profesor.getNombre() : "Sin asignar")
             + "\nModalidad: " + modalidad
             + "\nApuntes: " + urlApuntes
             + "\nCarrera propia: " + (esCarrera ? "Si" : "Servicio");
    }
}
