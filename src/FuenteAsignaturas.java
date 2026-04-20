import java.util.List;

public interface FuenteAsignaturas {
    // Materias generales
    List<Asignatura> obtenerMaterias();
    void agregarMateria(Asignatura m);
    void eliminarMateria(String nombre);
    void actualizarCreditosMateria(String nombre, int nuevosCreditos);

    // Asignaturas teóricas
    List<AsignaturaTeorica> obtenerAsignaturasTeoricas();
    void agregarAsignaturaTeorica(AsignaturaTeorica a);
    void eliminarAsignaturaTeorica(String nombre);

    // Asignaturas prácticas
    List<AsignaturaPractica> obtenerAsignaturasPracticas();
    void agregarAsignaturaPractica(AsignaturaPractica a);
    void eliminarAsignaturaPractica(String nombre);

    // Profesores
    List<Profesor> obtenerProfesores();
    void agregarProfesor(Profesor p);
    void eliminarProfesor(String nombre);
    void actualizarDepartamentoProfesor(String nombre, String nuevoDepartamento);
}
