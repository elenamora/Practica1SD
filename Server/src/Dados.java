import java.util.ArrayList;
import java.util.Random;

public class Dados {

    private int[] diceList = {0, 0, 0, 0, 0};
    private boolean[] lockedList = {false, false, false, false, false};
    private ArrayList<Integer> lockedDices = new ArrayList<>();

    public void getNumber() {

        Random r = new Random();

        for (int i = 0; i<5; i++) {

            if (lockedList[i] == false){

                diceList[i] = r.nextInt(6) + 1;

            }

        }

    } // Randomizar los dados que el jugador haya decidido no lockear

    public void sortDices(ArrayList<Integer> d) {

        int e = 0;

        while(d.size() > 0) {

            if (diceList[d.get(e)] == 6 && lockedDices.size() == 0) {

                lockedList[d.get(e)] = true;
                lockedDices.add(diceList[d.get(e)]);

                d.remove(e);
                e = 0;

            }

            else if(!lockedList[d.get(e)] && lockedDices.size() < 3) {

                int l = lockedDices.get(lockedDices.size() - 1);

                if ((l-1) == diceList[d.get(e)]) {

                    lockedList[d.get(e)] = true;
                    lockedDices.add(diceList[d.get(e)]);

                    d.remove(e);
                    e = 0;

                }

            }

            else {

                // error

            }

            e ++;

        }

    } // Lockea los dados que el jugador haya lockeado xd

    public int calculateScore() {

        int score = 0;

        if(lockedDices.size() == 3) {

            for (int i = 0; i < 5; i ++) {

                if (lockedList[i] == false) {

                    score += diceList[i];

                }

            }

        }

        return score;

    } // Suma los valores de los dados que el jugador no haya lockeado, si la lista de lockeados es menor que 3, devuelve 0 :(

    public int[] getDiceList() {

        return diceList;

    } // Devuelve los valores de los dados

    public boolean[] getLockedList() {

        return lockedList;

    } // Devuelve los dados lockeados por el jugador

    public ArrayList<Integer> getLockedDices() {

        return lockedDices;

    } // Devuelve los dados lockeados (6, 5...)

    public void addLockedDice(int i) {

        lockedList[i] = true;

        lockedDices.add(diceList[i]);

    }

}
