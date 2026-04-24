package datos;

import asignaturas.*;
import aulas.*;
import horario.Bloque;
import horario.Semestre;
import usuarios.Profesor;
import java.util.Arrays;
import java.util.List;

/**
 * Tres escenarios de datos quemados con semestres y aulas.
 */
public class Escenarios {

    // ── Escenario 1: Semestres I y II ────────────────────────────────
    public static List<Semestre> escenario1() {
        Profesor ana    = new Profesor("Ana Padilla",   "1-0006", "Computacion",
                Arrays.asList(new Bloque("Lunes","07:55","08:45")));
        Profesor marcos = new Profesor("Marcos Zamora", "1-0010", "Computacion",
                Arrays.asList(new Bloque("Jueves","09:45","10:35")));
        Profesor carlos = new Profesor("Carlos Perez",  "1-0001", "Idiomas",
                Arrays.asList(new Bloque("Lunes","07:00","07:50")));

        return Arrays.asList(
            new Semestre(1, Arrays.asList(
                new Teorica("Matematica General",    4, marcos, false, "https://tec.ac.cr/mat-gen","Presencial"),
                new Teorica("Introduccion a Prog.",  3, ana,    true,  "https://tec.ac.cr/intro",  "Presencial"),
                new Practica("Lab. Prog. I",         1, ana,    true,  "Windows 11",
                    Arrays.asList("JDK 17","IntelliJ IDEA","Guia Lab 1"))
            )),
            new Semestre(2, Arrays.asList(
                new Teorica("Calculo I",             4, marcos, false, "https://tec.ac.cr/cal1","Presencial"),
                new Teorica("Estructuras Discretas", 3, ana,    true,  "https://tec.ac.cr/disc","Presencial"),
                new Teorica("Ingles Tecnico I",      4, carlos, false, "https://tec.ac.cr/eng1","Presencial")
            ))
        );
    }

    public static List<Aula> aulasEscenario1() {
        return Arrays.asList(
            new AulaTeoria("Aula T-01", 101, "Edif. A", 35, true,  true),
            new AulaTeoria("Aula T-02", 102, "Edif. A", 40, false, true),
            new AulaLab   ("Lab-01",    201, "Edif. B", 25,
                Arrays.asList("PC Intel i5","VS Code","JDK 17"))
        );
    }

    // ── Escenario 2: Semestres III y IV ─────────────────────────────
    public static List<Semestre> escenario2() {
        Profesor luis  = new Profesor("Luis Vargas", "3-3333", "Computacion",
                Arrays.asList(new Bloque("Lunes","09:45","10:35")));
        Profesor maria = new Profesor("Maria Soto",  "4-4444", "Sistemas",
                Arrays.asList(new Bloque("Martes","12:30","13:20")));

        return Arrays.asList(
            new Semestre(3, Arrays.asList(
                new Teorica ("Estructuras de Datos", 4, luis,  true, "https://tec.ac.cr/datos","Presencial"),
                new Practica("Lab. Estructuras",     1, luis,  true, "Linux",
                    Arrays.asList("JDK 17","Eclipse","Guia 2")),
                new Teorica ("Bases de Datos I",     3, maria, true, "https://tec.ac.cr/bd1","Presencial")
            )),
            new Semestre(4, Arrays.asList(
                new Teorica ("POO",      4, luis,  true, "https://tec.ac.cr/poo","Presencial"),
                new Practica("Lab. POO", 1, luis,  true, "Windows 11",
                    Arrays.asList("JDK 17","IntelliJ","Guia 3")),
                new Teorica ("Redes I",  3, maria, true, "https://tec.ac.cr/redes","Presencial")
            ))
        );
    }

    public static List<Aula> aulasEscenario2() {
        return Arrays.asList(
            new AulaTeoria("Aula T-03", 103, "Edif. C", 38, true, true),
            new AulaLab   ("Lab-02",    202, "Edif. B", 30,
                Arrays.asList("PC AMD","Eclipse","JDK 17","MySQL WB")),
            new AulaLab   ("Lab-03",    203, "Edif. B", 28,
                Arrays.asList("PC Intel i7","NetBeans","Packet Tracer"))
        );
    }

    // ── Escenario 3: Semestres V y VI (con conflicto) ────────────────
    public static List<Semestre> escenario3() {
        Profesor pedro = new Profesor("Pedro Alvarado","5-5555","Computacion",
                Arrays.asList(new Bloque("Lunes","07:00","07:50")));
        Profesor sofia = new Profesor("Sofia Jimenez", "6-6666","Sistemas",
                Arrays.asList(new Bloque("Miercoles","14:20","15:10")));

        return Arrays.asList(
            new Semestre(5, Arrays.asList(
                new Teorica ("Inteligencia Artificial", 4, pedro, true, "https://tec.ac.cr/ia","Virtual"),
                new Practica("Lab. IA",      1, pedro, true, "Linux",
                    Arrays.asList("Python 3.11","TensorFlow","Jupyter")),
                new Practica("Lab. Redes II",1, sofia, true, "Linux",
                    Arrays.asList("Wireshark","Packet Tracer"))
            )),
            new Semestre(6, Arrays.asList(
                new Teorica("Ingenieria de Software", 4, sofia, true, "https://tec.ac.cr/soft","Presencial"),
                new Teorica("Compiladores",           3, pedro, true, "https://tec.ac.cr/comp","Presencial")
            ))
        );
    }

    public static List<Aula> aulasEscenario3() {
        return Arrays.asList(
            new AulaTeoria("Aula T-04", 104, "Edif. D", 40, true, false),
            new AulaLab   ("Lab-04",    204, "Edif. D", 20,
                Arrays.asList("PC i9","Docker","VS Code","Python 3.11"))
        );
    }
}
