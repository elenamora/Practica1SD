import java.util.ArrayList;

public class Jugador {

    private int puntuacion;
    private int dinero;
    private boolean ganado;

    public Dados dado;

    private int id;

    Jugador(int dInicial, int iden) {

        id = iden;
        puntuacion = 0;
        dinero = dInicial;
        ganado = false;
        dado = new Dados();

    }

    public void setPuntuacion(int p) {

        puntuacion = p;

    }

    public int getPuntuacion() {

        return puntuacion;

    }

    public void setDinero(int d) {

        dinero += d;

    }

    public int getDinero() {

        return dinero;

    }

    public void setGanado(boolean g) {

        ganado = g;

    }

    public boolean getGanado() {


        return ganado;

    }

    public int getId() {

        return id;

    }

    public void getNumber() {

        dado.getNumber();

    }

    public int[] getDiceList() {

        return dado.getDiceList();

    }

    public void sortDices(ArrayList<Integer> d) {

        dado.sortDices(d);

    }

    public boolean[] getLockedList() {

        return dado.getLockedList();

    }

    public ArrayList<Integer> getLockedDices() {

        return dado.getLockedDices();

    }

    public void addLockedDice(int i) {

        dado.addLockedDice(i);

    }

    public int calculcateScore() {

        return dado.calculateScore();

    }

}
