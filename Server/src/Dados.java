import java.util.ArrayList;
import java.util.Collection;
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

    }

    public void sortDices(ArrayList<Integer> d) {

        int e = 0;

        while(d.size() > 0) {

            if (diceList[d.get(e)] == 6 && lockedDices.size() == 0) {

                System.out.println(1);

                lockedList[d.get(e)] = true;
                lockedDices.add(diceList[d.get(e)]);

                d.remove(e);
                e = 0;

            }

            else if (lockedDices.contains(diceList[d.get(e)])) {

                System.out.println(2);

                d.remove(e);
                e = 0;

            }

            else if(!lockedList[d.get(e)] && lockedDices.size() < 3 && lockedDices.size() > 0) {

                System.out.println(3);

                int l = lockedDices.get(lockedDices.size() - 1);

                if ((l-1) == diceList[d.get(e)]) {

                    lockedList[d.get(e)] = true;
                    lockedDices.add(diceList[d.get(e)]);

                    d.remove(e);
                    e = 0;

                }

                else {

                    if(d.contains(5)){

                        e++;

                    }

                    else {

                        d.remove(e);
                        e = 0;

                    }

                }


            }

            else if(diceList[d.get(e)] < 4) {

                System.out.println(4);

                d.remove(e);
                e = 0;

            }

            else {

                e ++;

            }

        }

    }

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

    }

    public int[] getDiceList() {

        return diceList;

    }

    public boolean[] getLockedList() {

        return lockedList;

    }

    public ArrayList<Integer> getLockedDices() {

        return lockedDices;

    }

    public void addLockedDice(int i) {

        lockedList[i] = true;

        lockedDices.add(diceList[i]);

    }

}
