import java.io.IOException;
import java.util.ArrayList;

public class Joc {

    private Protocol protocol;

    private Jugador jugador1, jugador2;
    private Jugador[] jugadorList;

    private int bett, turn, pTurn;
    private boolean singlePlayer, playing, notPlayed1 = false, notPlayed2 = false, keePlaying = false, diced = false;

    private String state = "";

    private ArrayList<Integer> d = new ArrayList<>();

    Joc (String[] args) throws IOException {

        protocol = new Protocol(args);

        if (Integer.parseInt(args[1]) == 1) {

            singlePlayer = true;

        }

        else {

            singlePlayer = false;

        }

    }

    public void newGame() {

        while (true) {

            if (singlePlayer) {

                jugarSingle();

            }

            else {

                jugarMulti();

            }

        }

    }

    public void jugarSingle() {

        if (!keePlaying) {

            strt();

        }

        while (!notPlayed1 && !notPlayed2) {

            playing = true;
            turn = 0;

            if (pTurn == 0) {

                gameState(0);

                protocol.pnts(0,jugadorList[0].getId(), jugadorList[0].getPuntuacion());

            }

            else {

                jugaIA();

            }

        }

        if (!state.equals("EXIT")) {

            sendPuntuacio(0);

        }

    }

    public void jugarMulti() {

        if (!keePlaying) {

            strt();

        }

        while (!notPlayed1 && !notPlayed2) {

            playing = true;
            turn = 0;

            if (pTurn == 0) {

                gameState(0);

                protocol.pnts(0,jugadorList[0].getId(), jugadorList[0].getPuntuacion());
                protocol.pnts(1,jugadorList[0].getId(), jugadorList[0].getPuntuacion());

            }

            else {

                gameState(1);

                protocol.pnts(0,jugadorList[1].getId(), jugadorList[1].getPuntuacion());
                protocol.pnts(1,jugadorList[1].getId(), jugadorList[0].getPuntuacion());

            }

        }

        sendPuntuacio(0);
        sendPuntuacio(1);

    }
    private void strt() {


        if (singlePlayer) {

            int id = protocol.readStrt(0);

            jugador1 = new Jugador(5, id);
            jugador2 = new Jugador(5000, jugador1.getId() + 1);

            jugadorList = new Jugador[]{jugador1, jugador2};

            protocol.cash(0, jugadorList[0].getDinero());

        }

        else {

            int id0 = protocol.readStrt(0);
            int id1 = protocol.readStrt(1);

            jugador1 = new Jugador(5, id0);
            jugador2 = new Jugador(5, id1);

            jugadorList = new Jugador[]{jugador1, jugador2};

        }

        keePlaying = true;

    }

    private void bettSingle() {

        jugadorList[0].setDinero(-1);

        bett += 2;

        protocol.loot(0, bett);

        pTurn = protocol.turn();

        pTurn = 0;

        protocol.play(0, pTurn);

        state  = "DICE";

    }

    private void bettMulti() {

        protocol.cash(0, jugadorList[0].getDinero());
        protocol.cash(1, jugadorList[1].getDinero());

        jugadorList[0].setDinero(-1);
        jugadorList[1].setDinero(-1);

        bett += 2;

        protocol.loot(0, bett);
        protocol.loot(1, bett);

        pTurn = protocol.turn();

        if(pTurn == 0) {

            protocol.play(0, 0);
            protocol.play(1, 1);

        }

        else {

            protocol.play(0, 1);
            protocol.play(1, 0);

        }

    }

    private void dice(int jugadorActual) {

        jugadorList[jugadorActual].getNumber();

        if(singlePlayer) {

            if (jugadorActual == 0) {

                protocol.dice(0,jugadorList[jugadorActual].getId() , jugadorList[jugadorActual].getDiceList());

            }

            else {

                protocol.dice(0,8080 , jugadorList[jugadorActual].getDiceList());

            }

        }

        else {

            protocol.dice(0,jugadorList[jugadorActual].getId(), jugadorList[jugadorActual].getDiceList());
            protocol.dice(1,jugadorList[jugadorActual].getId(), jugadorList[jugadorActual].getDiceList());

        }

        diced = true;

    }

    private void take(int jugadorActual) {

        d = protocol.readTake(0);

        if (d.size() == 5)  {

            playing = false;

        }

        if(d.get(0) != -1) {

            jugadorList[jugadorActual].sortDices(d);

        }

        turn++;

        if (turn < 3 && playing) {

            diced = false;
            state  = "DICE";

        }

    }

    private void pass(int jugadorActual) {

        if (singlePlayer) {

            if (pTurn == 0) {

                pTurn = 1;

            }

            else {

                pTurn = 0;

            }

            playing = false;

        }

        else {

            if (pTurn == 0) {

                protocol.pass(1, jugadorList[jugadorActual].getId());
                pTurn = 1;

            }

            else {

                protocol.pass(1, jugadorList[jugadorActual].getId());
                pTurn = 0;

            }

            playing = false;

        }



    }

    private void exit(int juadorActual) {

        notPlayed1 = true;
        notPlayed2 = true;
        playing = false;
        keePlaying = false;

        jugadorList[juadorActual] = null;

        turn = 4;

    }

    private void gameState(int jugadorActual) {

        do {

            if (!state.equals("DICE") || diced) {

                state = protocol.readElement(jugadorActual);

                System.out.println(state);

            }

            switch (state) {

                case("BETT"):

                    if(singlePlayer) bettSingle();
                    else bettMulti();

                    break;

                case("DICE"):

                    System.out.println("Estamos en dice");
                    dice(jugadorActual);
                    break;

                case("TAKE"):

                    take(jugadorActual);
                    break;

                case("PASS"):

                    pass(jugadorActual);
                    break;

                case("EXIT"):

                    exit(jugadorActual);
                    break;

            }

        } while (playing || turn < 3);

    }

    private void sendPuntuacio(int jugador) {

        if(jugadorList[0].getPuntuacion() > jugadorList[1].getPuntuacion()) {

            jugadorList[1].setDinero(bett);

            bett = 0;

            pTurn = 1;

            protocol.wins(jugador, 0);

        }

        else if (jugadorList[0].getPuntuacion() < jugadorList[1].getPuntuacion()){

            bett = 0;

            pTurn = 0;

            protocol.wins(jugador,1);

        }

        else {

            protocol.wins(jugador,2);

        }

    }

    private void jugaIA () {

        while (playing && turn < 3) {

            jugadorList[1].getNumber();

            protocol.dice(0,8080, jugadorList[1].getDiceList());

            int x = 0;

            while(x < 3) {

                for (int i = 0; i < jugadorList[1].getDiceList().length; i ++) {

                    if (jugadorList[1].getLockedDices().size() == 0 && jugadorList[1].getDiceList()[i] == 6 && !jugadorList[1].getLockedList()[i]) {

                        jugadorList[1].addLockedDice(i);

                    }

                    else if (!jugadorList[1].getLockedList()[i] && jugadorList[1].getLockedDices().size() < 3) {

                        int d = jugadorList[1].getLockedDices().get(jugadorList[1].getLockedDices().size() - 1);

                        if ((d - 1) == jugadorList[1].getDiceList()[i]) {

                            jugadorList[1].addLockedDice(i);

                        }

                    }

                }

                x ++;

            } // Seleccio dels daus a guardar

            protocol.take(0,jugadorList[1].getId(), jugadorList[1].getLockedDices());

            int resultat = jugadorList[1].calculcateScore();

            if (resultat > 6) {

                playing = false;
                protocol.pass(0, 8080);

            }

            turn ++;

        }

        protocol.pnts(0,8080, jugadorList[1].getPuntuacion());

        notPlayed2 = true;
        pTurn = 0;

    }

}