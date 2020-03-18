import java.io.IOException;
import java.util.ArrayList;

public class Joc {

    private Protocol protocol; // clase para llamar a los protocolos del socket

    private int singlePlayer; // Modo single o multiplayer

    private Jugador jugador1, jugador2; // Jugadores

    private Jugador[] jugadorList; // Lista de jugadores

    private int bett, turn, pTurn; // bett = dinero apostado en total, turn = turno que esta jugandose ahora (hasta 3), AuxScore = score para la IA de j2, pTurn = Jugador que empieza

    private boolean notPlayed1 = false, notPlayed2 = false, playing; // notplayed1 = j1 no ha jugado, notPlayed2 = j2 no ha jugado, playing = j no se ha plantado

    private ArrayList<Integer> d = new ArrayList<>(); // lista de dados que el jugador ha decidido lockear

    private String state = "";

    private boolean keePlaying = false;

    Joc (String[] args) throws IOException {

        protocol = new Protocol(args);
        singlePlayer = Integer.parseInt(args[1]);

    }

    public void newGame(){

        while (true) {

            if (singlePlayer == 1) {

                while(protocol.readStrt(0) == -1 || !keePlaying) {



                }

                if(!keePlaying) {

                    jugador1 = new Jugador(5, protocol.readStrt(0));
                    jugador2 = new Jugador(5000, jugador1.getId() + 1);

                    keePlaying = true;

                    jugadorList = new Jugador[]{jugador1, jugador2};

                }

                bettSingle();
                jugarSinglePlayer();

                if (state == "EXIT") {

                    jugador1 = null;
                    keePlaying = false;

                }

            }

            else if(singlePlayer == 2) {

                while(protocol.readStrt(0) == -1 && protocol.readStrt(1) == -1) {



                }

                jugador1 = new Jugador(5, protocol.readStrt(0));
                jugador2 = new Jugador(5, protocol.readStrt(1));

                jugadorList = new Jugador[]{jugador1, jugador2};

                protocol.cash(0, jugadorList[0].getDinero());
                protocol.cash(1, jugadorList[1].getDinero());

                while(!protocol.readBett(0) && !protocol.readBett(1)) {



                }

                bettMulti();

                jugarMultiPlayer();

            }

        }

    }

    public void jugarSinglePlayer() {

        while (!notPlayed1 && !notPlayed2) {

            playing = true;
            turn = 0;

            if(pTurn == 0){

                do {

                    switch (state) {

                        case "DICE":

                            dice(0);
                            break;

                        case "TAKE":

                            take(0);
                            break;

                        case "PASS":

                            pass();
                            break;

                        case "EXIT":

                            exit();
                            break;

                        default:
                            break;

                    }

                    if(protocol.readExit(0)) {

                        state = "EXIT";

                    }


                } while(playing || turn < 3);

                protocol.pnts(0, jugadorList[0].getPuntuacion());

            }

            else {

                jugaIA();

            }

        }

        sendPuntuacio(0);

    }

    public void jugarMultiPlayer() {

        while (!notPlayed1 && !notPlayed2) {

            playing = true;
            turn = 0;

            if(pTurn == 0){

                do {

                    switch (state) {

                        case "DICE":

                            dice(0);
                            protocol.dice(1, jugadorList[0].getDiceList());
                            break;

                        case "TAKE":

                            take(0);
                            protocol.take(1, jugadorList[0].getLockedDices().size(), jugadorList[0].getLockedDices());
                            break;

                        case "PASS":

                            pass();
                            protocol.pass(1, jugadorList[0].getId());
                            break;

                        case "EXIT":

                            exit();
                            break;

                        default:
                            break;

                    }

                    if(protocol.readExit(0)) {

                        state = "EXIT";

                    }


                } while(playing || turn < 3);

                protocol.pnts(0, jugadorList[0].getPuntuacion());
                protocol.pnts(1, jugadorList[0].getPuntuacion());

                state = "DICE";

            }

            else {

                do {

                    switch (state) {

                        case "DICE":

                            dice(1);
                            protocol.dice(0, jugadorList[1].getDiceList());
                            break;

                        case "TAKE":

                            take(1);
                            protocol.take(0, jugadorList[1].getLockedDices().size(), jugadorList[1].getLockedDices());
                            break;

                        case "PASS":

                            pass();
                            protocol.pass(0, jugadorList[1].getId());
                            break;

                        case "EXIT":

                            exit();
                            break;

                        default:
                            break;

                    }

                    if(protocol.readExit(1)) {

                        state = "EXIT";

                    }


                } while(playing || turn < 3);

                protocol.pnts(0, jugadorList[1].getPuntuacion());
                protocol.pnts(1, jugadorList[0].getPuntuacion());

            }

            state = "DICE";

        }

        sendPuntuacio(0);
        sendPuntuacio(1);

    }

    private void bettSingle() {

        protocol.cash(0, jugadorList[0].getDinero());

        while (!protocol.readBett(0)) {



        }

        jugadorList[0].setDinero(-1);

        bett += 2;

        protocol.loot(0, bett);

        pTurn = protocol.turn();

        protocol.play(0, pTurn);

        state = "DICE";


    }

    private void bettMulti() {

        protocol.cash(0, jugadorList[0].getDinero());
        protocol.cash(1, jugadorList[1].getDinero());

        while(!protocol.readBett(0) && !protocol.readBett(1)) {



        }

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

        state = "DICE";

    }

    private void dice(int jugadorActual) {

        jugadorList[jugadorActual].getNumber();
        protocol.dice(jugadorActual, jugadorList[jugadorActual].getDiceList());

        while(true) {

            d = protocol.readTake(jugadorActual);

            if(d.get(0) != -1) {

                state = "TAKE";
                break;

            }

            else if(protocol.readPass(jugadorActual)) {

                state = "PASS";
                break;

            }

            else if(protocol.readExit(jugadorActual)) {

                state = "EXIT";
                break;

            }


        }

    }

    private void take(int jugadorActual) {

        if (d.size() == 5)  {

            playing = false;

        }

        jugadorList[jugadorActual].sortDices(d);

        turn++;

        if (turn < 3) {

            state = "DICE";

        }

    }

    private void pass() {

        if (pTurn == 0) {

            pTurn = 1;

        }

        else {

            pTurn = 0;

        }

        playing = false;



    }

    private void exit() {

        System.out.println("USUARI VOL FINALITZAR KEKW");

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

            protocol.dice(0, jugadorList[1].getDiceList());

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

            protocol.take(0,jugadorList[1].getLockedDices().size(), jugadorList[1].getLockedDices());

            int resultat = jugadorList[1].calculcateScore();

            if (resultat > 6) {

                playing = false;
                protocol.pass(0, 8080);

            }

            turn ++;

        }

        protocol.pnts(0, jugadorList[1].getPuntuacion());

        notPlayed2 = true;
        pTurn = 0;

    }

}