import java.util.Scanner;

public class Login {
    public Usuario leerUsuario() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Ingrese su ID (1=Estudiante, 2=Profesor, 3=Coordinador): ");
        String id = sc.nextLine();

        switch (id) {
            case "1":
                return new Estudiante();
            case "2":
                return new Profesor();
            case "3":
                return new Coordinador();
            default:
                System.out.println("ID inválido");
                return null;
        }
    }
}
