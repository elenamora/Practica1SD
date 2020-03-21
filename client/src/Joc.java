
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Joc {

    private Partida partida;
    private Protocol protocol;
    private Random r = new Random();
    private Scanner sc;
    private int mode, id, num;
    private int puntuacio = -1;
    private ArrayList daus = new ArrayList();
    public ArrayList dausGuardats = new ArrayList();
    private ArrayList dausMostrar = new ArrayList();
    public int tirades = 3;
    private boolean jugat1, jugat2;


    public Joc(Protocol p, int mode, int id, int num) throws IOException {

        this.id = id;
        this.num = num;
        this.jugat2 = false;
        this.jugat1 = false;
        this.partida = new Partida();
        this.protocol = p;
        this.mode = mode;
        sc = new Scanner(System.in);

        puntuacio = protocol.read_cash();

        System.out.println("El teu CASH és de: " + puntuacio);

        bett();

    }

    public void bett() throws IOException {
        if (puntuacio == 0) {
            System.out.println("No tens diners per seguir jugant");
            guanyarPartida();
            protocol.desconnexio();
            protocol.exit();

        } else {

            protocol.bett();
            System.out.println("Hem fet la BETT");

            System.out.println("El LOOT és " + protocol.read_loot());

            puntuacio -= 1;

            partida.setEstat(Partida.EstatPartida.ROLL);

            turn();
        }

    }

    public void turn() throws IOException {
        int comenca = protocol.read_play();

        if(comenca == 0){
            System.out.println("És el teu torn");
            if(mode == 1)
                jugarAutomatic(num);
            else
                jugar();

        }
        else{
            System.out.println("És el torn del contrincant");
            jugaIA();
        }
    }


    public void jugar() throws IOException {

        while (partida.getPartida()) {

            switch (partida.getEstat()) {

                case ROLL:
                    if (tirades > 0) {

                        daus = protocol.read_dice();

                        System.out.println("\nEls resultats de la tirada són:");
                        for (int i = 1; i < daus.size() + 1; i++) {
                            System.out.println("Dau nº " + i + " és " + daus.get(i-1));
                        }
                        partida.setEstat(Partida.EstatPartida.TAKE);
                    } else {
                        System.out.println("No ens queden més tirades per a fer. Ja hem fet 3.");
                        partida.setEstat(Partida.EstatPartida.PASS);
                    }

                    tirades -= 1;
                    break;

                case TAKE:
                    int dauEscollit = 0, num;
                    List<Integer> escollits = new ArrayList<Integer>();

                    System.out.println("Quants daus et vols quedar?");
                    int cantitat = sc.nextInt();

                    if(cantitat == 0){
                        System.out.println("Què vols fer? 0(ROLL), 1(PASS), 2(EXIT)");
                        int opcio = sc.nextInt();
                        if(opcio == 0) {
                            partida.setEstat(Partida.EstatPartida.ROLL);
                            break;
                        }
                        else if(opcio == 1){
                            partida.setEstat(Partida.EstatPartida.PASS);
                            break;
                        }
                        else if(opcio == 2){
                            partida.setEstat(Partida.EstatPartida.EXIT);
                            break;
                        }
                    }
                    else if(cantitat > 5){
                        System.out.println("Com a màxim et pots quedar 5 daus");
                        break;
                    }
                    else{
                        System.out.println("Quins daus et vols quedar? (1 | 2 | 3 | 4 | 5 )");
                        for(int i = 0; i < cantitat; i++){
                            dauEscollit = sc.nextInt();

                            if(escollits.contains(dauEscollit)){
                                System.out.println("Ja has escollit aquest dau. Tria un altre");
                                i--;
                            }
                            else{
                                escollits.add(dauEscollit);
                                dausGuardats.add(dauEscollit - 1);
                                dausMostrar.add(daus.get(dauEscollit - 1));
                            }
                        }
                    }


                    System.out.println("Els daus que has guardat són: " + dausMostrar);

                    protocol.take(id, dausGuardats);
                    partida.setEstat(Partida.EstatPartida.ROLL);

                    break;

                case PASS:
                    protocol.pass(id);
                    partida.setEstat(Partida.EstatPartida.PASS);
                    jugat1 = true;

                    if (jugat1 && jugat2){
                        System.out.println("S'ha acabat la partida");
                        guanyarPartida();

                        System.out.println("Vols jugar una altra partida? 0(sí), 1(exit)");
                        int n = sc.nextInt();
                        if(n == 0){
                            jugat1 = false;
                            jugat2 = false;
                            bett();
                        }
                        else if(n == 1){
                            partida.setEstat(Partida.EstatPartida.EXIT);
                            break;
                        }

                    }
                    else if(!jugat2){
                        System.out.println("Ara és el torn del teu contrincant");
                        jugaIA();
                    }
                    break;


                case EXIT:

                    System.out.println("Vols abandonar el joc? (0 (sí) | 1 (no))");
                    if (sc.nextInt() == 0) {
                        partida.setPartida(false);
                        protocol.exit();
                        protocol.desconnexio();
                        protocol.read_play();

                    } else {
                        jugat2 = true;
                        jugat1 = true;
                        partida.setEstat(Partida.EstatPartida.BETT);
                        bett();
                    }

                    break;

                default:
                    System.out.println("S'ha produit un Default");
                    break;

            }

        }

    }


    public void jugarAutomatic(int numPartides) throws IOException {

        for (int i = 0; i < numPartides; i++) {

            while (partida.getPartida()) {

                switch (partida.getEstat()) {
                    case BETT:
                        if (puntuacio == 0) {
                            System.out.println("No tens diners per seguir jugant");
                            guanyarPartida();
                            protocol.desconnexio();
                            protocol.exit();

                        } else {

                            System.out.println("jugar");
                            protocol.bett();

                            System.out.println("El loot és " + protocol.read_loot());

                            puntuacio -= 1;

                            partida.setEstat(Partida.EstatPartida.ROLL);
                        }

                        int comenca = protocol.read_play();

                        while (comenca == -1){

                            comenca  = protocol.read_play();

                        }

                        System.out.println("juga " + comenca);

                        break;


                    case ROLL:
                        if (tirades > 0) {

                            System.out.println("estamos en roll");

                            daus = protocol.read_dice();

                            System.out.println("Els resultats de la tirada són:");
                            for (int j = 1; j < daus.size() + 1; j++) {
                                System.out.println("Dau nº " + j + " és " + daus.get(j-1));
                            }
                            partida.setEstat(Partida.EstatPartida.TAKE);
                        } else {
                            System.out.println("No ens queden més tirades per a fer. Ja hem fet 3.");
                            partida.setEstat(Partida.EstatPartida.PASS);
                        }

                        tirades -= 1;
                        break;

                    case TAKE:
                        int num = 0, suma = 0;
                        boolean podemEscollir = false;

                        ArrayList dausPossibles = daus;
                        Collections.sort(dausPossibles);

                        for (int j = 0; j < dausPossibles.size(); j++) {
                            if (dausGuardats.size() == 0 && j == 6) {
                                dausGuardats.add(dausPossibles.get(j));
                                dausPossibles.set(j, 0);
                            }

                            if (dausGuardats.size() == 1 && j == 5) {
                                dausGuardats.add(dausPossibles.get(j));
                                dausPossibles.set(j, 0);
                            }

                            if (dausGuardats.size() == 2 && j == 4) {
                                dausGuardats.add(dausPossibles.get(j));
                                dausPossibles.set(j, 0);
                            }

                            if (dausGuardats.size() == 3) {
                                for (int k = 0; k < dausPossibles.size(); k++) {
                                    if (k != 0)
                                        dausGuardats.add(dausPossibles.get(k));
                                }

                                int num1 = (int)dausGuardats.get(3);
                                int num2 = (int)dausGuardats.get(4);
                                if(num1 + num2 > 7){
                                    partida.setEstat(Partida.EstatPartida.PASS);
                                    break;
                                }

                            }
                        }


                        protocol.take(id, dausGuardats);
                        partida.setEstat(Partida.EstatPartida.ROLL);

                        break;

                    case PASS:
                        protocol.pass(id);
                        partida.setEstat(Partida.EstatPartida.PASS);
                        break;


                    case EXIT:
                        guanyarPartida();
                        partida.setPartida(false);
                        protocol.desconnexio();
                        protocol.exit();

                        break;

                    default:
                        System.out.println("S'ha produit un Default");
                        break;


                }
            }
        }
    }


    public void jugaIA() throws IOException {
        while (partida.getPartida()) {

            switch (partida.getEstat()) {

                case TAKE:

                    protocol.read_take();
                    break;

                case PASS:
                    protocol.read_pass();
                    jugat2 = true;

                    if (jugat1 && jugat2){
                        System.out.println("S'ha acabat la partida");
                        guanyarPartida();
                    }
                    else if(!jugat1){
                        System.out.println("Ara és el teu torn");
                        if(mode == 1)
                            jugarAutomatic(num);
                        else
                            jugar();
                    }

                    break;


                default:
                    System.out.println("S'ha produit un Default");
                    break;

            }

        }
    }

    public void guanyarPartida() throws IOException {
        if (protocol.read_win() == 0) {
            System.out.println("Has guanyat");
        } else if(protocol.read_win() == 1){
            System.out.println("Has perdut");
        }
        else if(protocol.read_win() == 2) {
            System.out.println("Empat");
        }

        System.out.println("La teva puntuació final és: " + protocol.read_pnts() + "punts");

        while (protocol.read_cash() == -1){

        }
    }


}


