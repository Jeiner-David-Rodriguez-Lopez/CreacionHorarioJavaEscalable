package horario;

public class Bloque {
    private String dia;
    private String inicio;
    private String fin;

    public static final String[] DIAS = {"Lunes","Martes","Miercoles","Jueves","Viernes"};
    public static final String[][] SLOTS = {
        {"07:00","07:50"}, {"07:55","08:45"}, {"08:50","09:40"},
        {"09:45","10:35"}, {"10:40","11:30"},
        {"12:30","13:20"}, {"13:25","14:15"}, {"14:20","15:10"}, {"15:15","16:00"}
    };

    public Bloque(String dia, String inicio, String fin) {
        this.dia    = dia;
        this.inicio = inicio;
        this.fin    = fin;
    }

    public String getDia()    { return dia; }
    public String getInicio() { return inicio; }
    public String getFin()    { return fin; }

    public boolean chocaCon(Bloque otro) {
        return this.dia.equals(otro.dia) && this.inicio.equals(otro.inicio);
    }

    @Override public String toString() { return dia + " " + inicio + "-" + fin; }
}
