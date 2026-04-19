public class Main{
    public Usuario persona;
    public static void main(String[] args){
        Main app = new Main();
        app.login();
    }

    public void login() {
        Login login = new Login();
        persona = login.leerUsuario();

        if (persona instanceof Estudiante) {
            System.out.println("Opciones: Ver horarios, Salir");
        } else if (persona instanceof Profesor) {
            System.out.println("Opciones: Ver horarios, Ingresar sugerencias, Salir");
        } else if (persona instanceof Coordinador) {
            System.out.println("Opciones: Ver horarios, Generar horarios, Eliminar, Guardar, Salir");
        }
    }

}