import java.util.List;
public interface FuenteAsignaturas {
    List<Asignatura> obtenerAsignaturas();
    void agregarAsignatura(Asignatura a);
    void eliminarAsignatura(String nombre);
    void actualizarCreditos(String nombre, int nuevosCreditos);
}
