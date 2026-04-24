public class Profesor extends Usuario{
    private String nombre;
    private String cedula;
    private String id = "2";
    //private horario Aquí va una lista que es horario


    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    @Override
    public String toString() {
        return "Profesor{" +
                "nombre='" + nombre + '\'' +
                ", cedula='" + cedula + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}
