public class Estudiante extends Usuario {
    private String id = "1";



    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "id='" + id + '\'' +
                '}';
    }
}
