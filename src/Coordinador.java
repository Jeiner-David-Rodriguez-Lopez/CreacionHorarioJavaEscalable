import java.util.List;

public class Coordinador extends Usuario implements FuenteAsignaturas {

    private FuenteAsignaturas fuente; // referencia a la fuente de datos (JSON)

    public Coordinador(FuenteAsignaturas fuente) {
        this.fuente = fuente;
    }

    // ===== Métodos de consulta =====
    @Override
    public List<Asignatura> obtenerMaterias() {
        return fuente.obtenerMaterias();
    }

    @Override
    public void agregarMateria(Asignatura m) {
        fuente.agregarMateria(m);
    }

    @Override
    public void eliminarMateria(String nombre) {
        fuente.eliminarMateria(nombre);
    }

    @Override
    public List<Profesor> obtenerProfesores() {
        return fuente.obtenerProfesores();
    }

    @Override
    public void agregarProfesor(Profesor p) {
        fuente.agregarProfesor(p);
    }

    @Override
    public void eliminarProfesor(String nombre) {
        fuente.eliminarProfesor(nombre);
    }

    @Override
    public void actualizarDepartamentoProfesor(String nombre, String nuevoDepartamento) {
        fuente.actualizarDepartamentoProfesor(nombre, nuevoDepartamento);
    }

    @Override
    public List<AsignaturaTeorica> obtenerAsignaturasTeoricas() {
        return fuente.obtenerAsignaturasTeoricas();
    }

    @Override
    public void agregarAsignaturaTeorica(AsignaturaTeorica a) {
        fuente.agregarAsignaturaTeorica(a);
    }

    @Override
    public void eliminarAsignaturaTeorica(String nombre) {
        fuente.eliminarAsignaturaTeorica(nombre);
    }

    @Override
    public List<AsignaturaPractica> obtenerAsignaturasPracticas() {
        return fuente.obtenerAsignaturasPracticas();
    }

    @Override
    public void agregarAsignaturaPractica(AsignaturaPractica a) {
        fuente.agregarAsignaturaPractica(a);
    }

    @Override
    public void eliminarAsignaturaPractica(String nombre) {
        fuente.eliminarAsignaturaPractica(nombre);
    }

    // ===== Métodos de modificación =====
    public void actualizarCreditosMateria(String nombre, int nuevosCreditos) {
        fuente.actualizarCreditosMateria(nombre, nuevosCreditos);
    }

    public void cambiarNombreMateria(String nombreViejo, String nombreNuevo) {
        // lógica para buscar la materia y cambiar su nombre
        List<Asignatura> materias = fuente.obtenerMaterias();
        for (Asignatura m : materias) {
            if (m.getNombre().equalsIgnoreCase(nombreViejo)) {
                m.setNombre(nombreNuevo);
                break;
            }
        }
    }


    /*
    public void asignarAulaTeorica(String nombreAsignatura, String nuevaAula) {
        List<AsignaturaTeorica> teoricas = fuente.obtenerAsignaturasTeoricas();
        for (AsignaturaTeorica a : teoricas) {
            if (a.getNombre().equalsIgnoreCase(nombreAsignatura)) {
                a.setAulaTeorica(nuevaAula);
                break;
            }
        }
    }

    public void asignarAulaPractica(String nombreAsignatura, String nuevaAula) {
        List<AsignaturaPractica> practicas = fuente.obtenerAsignaturasPracticas();
        for (AsignaturaPractica a : practicas) {
            if (a.getNombre().equalsIgnoreCase(nombreAsignatura)) {
                a.setAulaPractica(nuevaAula);
                break;
            }
        }
    }*/
}

