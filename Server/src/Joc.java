import java.util.ArrayList;

public class Joc {

    private Protocol protocol; // clase para llamar a los protocolos del socket

    private int singlePlayer; // Modo single o multiplayer

    private Jugador jugador1, jugador2; // Jugadores

    private Jugador[] jugadorList; // Lista de jugadores

    private int bett, turn, AuxScore, pTurn; // bett = dinero apostado en total, turn = turno que esta jugandose ahora (hasta 3), AuxScore = score para la IA de j2, pTurn = Jugador que empieza

    private boolean notPlayed1 = false, notPlayed2 = false, playing; // notplayed1 = j1 no ha jugado, notPlayed2 = j2 no ha jugado, playing = j no se ha plantado

    private ArrayList<Integer> d = new ArrayList<>(); // lista de dados que el jugador ha decidido lockear

    private String state = "";

    Joc (String[] args) {

        protocol = new Protocol(args);
        singlePlayer = Integer.parseInt(args[1]);

    }

    public void newGame(){

        while (true) {

            if (singlePlayer == 1) {

                while(protocol.readStrt(0) == -1) {



                }

                jugador1 = new Jugador(5, protocol.readStrt(0));
                jugador2 = new Jugador(5, jugador1.getId() + 1);

                jugadorList = new Jugador[]{jugador1, jugador2};

                jugarSinglePlayer();

                if (state == "EXIT") {

                    //notsarted

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

                bett += 2;

                protocol.loot(0, bett);
                protocol.loot(1, bett);
                pTurn = protocol.turn();
                protocol.play(0, pTurn);
                protocol.play(1, pTurn);
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

                        case "BETT":

                            bett();
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

                    }

                    if(protocol.readPass()){

                        state = "PASS";
                    }

                    else if(protocol.readExit()) {

                        state = "EXIT";

                    }

                } while(playing || turn < 3);

            }

            else {

                jugaIA();

            }

        }

        sendPuntuacio();

    }

    public void jugarMultiPlayer() {

        while (!notPlayed1 && !notPlayed2) {

            if(pTurn == 0){

                do {

                    switch (state) {

                        case "BETT":

                            bett();
                            break;

                        case "TAKE":

                            //enviem informacio dels daus i esperem a que hi hagi una resposta per part de l'usuari

                            break;

                        case "PASS":

                            // jugador decideix plantar-se i juga l'altre jugador, en cas de que els dos hagin jugat s'acaba la partida

                            break;

                    }

                } while(!protocol.readExit());

            }

            else {

                do {

                    switch (state) {

                        case "BETT":

                            bett();
                            break;

                        case "TAKE":

                            //enviem informacio dels daus i esperem a que hi hagi una resposta per part de l'usuari

                            break;

                        case "PASS":

                            // jugador decideix plantar-se i juga l'altre jugador, en cas de que els dos hagin jugat s'acaba la partida

                            break;

                    }

                } while(!protocol.readExit());

            }

        }

    }

    private void bett() {

        protocol.cash(0, jugadorList[0].getDinero());

        while (!protocol.readBett(0)) {



        }

        bett += 2;

        protocol.loot(0, bett);

        pTurn = protocol.turn();

        protocol.play(0, pTurn);

        state = "TAKE";

    }

    private void take(int jugadorActual) {

        jugadorList[jugadorActual].getNumber();
        protocol.dice(jugadorList[jugadorActual].getDiceList());

        int j = 0;

        while(true) {

            d = protocol.readTake();

            if(d.get(0) != -1) {

                break;

            }


        }

        if (d.size() == 5)  {

            playing = false;

        }

        jugadorList[jugadorActual].sortDices(d);

        protocol.pnts(jugadorList[jugadorActual].calculcateScore());

        turn++;

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

    /*

    public void jugar() {

        while (!notPlayed1 && !notPlayed2) {

            if(singlePlayer == 1){

                if (pTurn == 0) {

                    it(0);

                    notPlayed1 = true;
                    pTurn = 1;

                }

                playing = true;

            }

            else if (singlePlayer == 2) {

                if (pTurn == 0) {

                    it(0);
                    //j1 juega;
                    notPlayed1 = true;
                    pTurn = 1;

                }

                else {

                    it(1);
                    //j2 juega;
                    notPlayed2 = true;
                    pTurn = 0;

                }

            }

            if (!mode && pTurn == 0 && notPlayed1) {

                calculateScore();

                pTurn = 1;

                notPlayed1 = true;

            }

            else if (!mode && pTurn == 1 && notPlayed2) {

                calculateScore();

                pTurn = 0;

                notPlayed2 = true;

            }

        }

        sendPuntuacio();

    }

     */

    private void sendPuntuacio() {

        protocol.pnts(jugadorList[0].getPuntuacion());

        if(jugadorList[0].getPuntuacion() > jugadorList[1].getPuntuacion()) {

            protocol.wins(0);

        }

        else if (jugadorList[0].getPuntuacion() < jugadorList[1].getPuntuacion()){

            protocol.wins(1);

        }

        else {

            protocol.wins(2);

        }

    }

    private void it(int ju) {

        while (playing && turn < 3){

            jugadorList[ju].getNumber();
            protocol.dice(jugadorList[ju].getDiceList());

            int j = 0;

            while(j < 10) {

                d = protocol.readTake();
                // arrayList con la posicion de los dados que hay que lockear
                j++;

            }

            if (d.size() == 5)  {

                playing = false;

            }

            jugadorList[ju].sortDices(d);

            if (protocol.readPass()){

                playing = false;

            }

            if (protocol.readExit()){

                // eliminar jugador llista

            }

            turn++;

        }

    }

    private void jugaIA () {

        while (playing && turn < 3) {

            jugadorList[1].getNumber();

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

            protocol.take(0, jugadorList[1].getLockedDices());

            int resultat = jugadorList[1].calculcateScore();

            if (resultat > 6) {

                playing = false;

            }

            turn ++;

        }

        notPlayed2 = true;
        pTurn = 0;

    }

}