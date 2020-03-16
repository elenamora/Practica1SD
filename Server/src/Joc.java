import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;

public class Joc {

    private int bett = 0;

    private int turn = 0;

    private boolean playing = false;

    private int[] diceList = {0, 0, 0, 0, 0};

    private boolean[] lockedList = {false, false, false, false, false};

    private ArrayList<Integer> lockedDices = new ArrayList<>();

    private int amm = 0;

    Scanner s = new Scanner(System.in);

    Joc () {



    }

    public void jugar() {

        playing = true;

        while (playing && turn < 3){

            for (int i = 0; i<5; i++) {

                if (lockedList[i] == false){

                    diceList[i] = getNumber();

                }

            }

            System.out.println(diceList[0]);
            System.out.println(diceList[1]);
            System.out.println(diceList[2]);
            System.out.println(diceList[3]);
            System.out.println(diceList[4]);

            int v = s.nextInt();

            while (v != 0) {

                v -= 1;

                if(lockedDices.size() == 0 && diceList[v] == 6 && !lockedList[v]) {

                    lockedList[v] = true;

                    System.out.println("añadiendo: " + Integer.toString(diceList[v]));

                    lockedDices.add(diceList[v]);

                }

                else if (!lockedList[v] && lockedDices.size() < 3 ) {

                    int d = lockedDices.get(lockedDices.size() - 1);

                    if ((d-1) == diceList[v]) {

                        lockedList[v] = true;

                        System.out.println("añadiendo: " + Integer.toString(diceList[v]));

                        lockedDices.add(diceList[v]);

                    }

                }

                v = s.nextInt();

            }

            System.out.println("seguir jugando?");

            int v2 = s.nextInt();

            if (v2 == 0){

                playing = false;

            }

            else {

                turn++;

            }


        }

        System.out.println("Longitud lista: ");
        System.out.println(lockedDices.size());

        if (lockedDices.size() < 3){

            System.out.println("Has perdido");

        }

        else {

            for (int j = 0; j < 5; j ++) {

                if (lockedList[j] == false) {

                    bett += diceList[j];

                }

            }

            System.out.println("Has ganado!!");
            System.out.println(bett);

        }

    }

    private int getNumber() {

        Random r = new Random();

        return r.nextInt(6) + 1;

    }

}