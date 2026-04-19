public class Profesor extends Usuario{
    private String nombre;
    private String cedula;
    private String id = "2";
    //private horario Aquí va una lista que es horario


    public String getId() {
        return id;
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
