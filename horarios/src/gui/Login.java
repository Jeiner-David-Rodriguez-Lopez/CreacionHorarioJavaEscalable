package gui;

import usuarios.Coordinador;
import usuarios.Estudiante;
import usuarios.Profesor;
import usuarios.Usuario;
import datos.DatosMemoria;

public class Login {

    private static final String PASS_ESTUDIANTE  = "est123";
    private static final String PASS_PROFESOR    = "prof456";
    private static final String PASS_COORDINADOR = "coord789";

    /**
     * Autentica al usuario por contrasena.
     * @param password contrasena ingresada
     * @return Usuario correspondiente o null si es invalida
     */
    public Usuario autenticar(String password) {
        if (password == null) return null;
        if (PASS_ESTUDIANTE.equals(password)) {
            return new Estudiante("Estudiante TEC");
        } else if (PASS_PROFESOR.equals(password)) {
            return new Profesor("Profesor Demo", "0-0000", "Computacion",
                    new java.util.ArrayList<>());
        } else if (PASS_COORDINADOR.equals(password)) {
            return new Coordinador("Coordinador TEC", new DatosMemoria());
        }
        return null;
    }
}
