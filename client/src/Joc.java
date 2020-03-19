
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
    private int mode, id;
    private int puntuacio = -1;
    private ArrayList daus = new ArrayList();
    public ArrayList dausGuardats = new ArrayList();
    public int tirades = 3;

    static private String[] menu = {"Tirar Daus", "Guardar daus", "Passar torn", "Sortir de l'aplicació"};

    public Joc(Protocol p, int mode, int id) throws IOException, TimeoutException {

        this.id = id;
        this.partida = new Partida();
        this.protocol = p;
        this.mode = mode;
        sc = new Scanner(System.in);

        puntuacio = protocol.read_cash();

        while (puntuacio == -1){

            puntuacio = protocol.read_cash();

        }

        System.out.println(Integer.toString(puntuacio));

        partida.setEstat(Partida.EstatPartida.BETT);

    }

    public void jugar() throws IOException {

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

                    System.out.println("juga" + comenca);


                    break;

                case ROLL:
                    if (tirades > 0) {

                        System.out.println("estamos en roll");

                        daus = protocol.read_dice();

                        System.out.println("Els resultats de la tirada són:");
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
                    System.out.println("Quins daus et vols quedar? (0(Cap) | 1 | 2 | 3 | 4 | 5 )");
                    int num = 0;
                    int dauEscollit;


                    while (sc.hasNext()) {
                        if (sc.hasNextInt()){
                            dauEscollit = sc.nextInt();
                            if (dauEscollit == 0){
                                System.out.println("Què vols fer? 0(ROLL), 1(PASS), 2(EXIT)");
                                int opcio = sc.nextInt();
                                if(opcio == 0)
                                    partida.setEstat(Partida.EstatPartida.ROLL);
                                else if(opcio == 1){
                                    partida.setEstat(Partida.EstatPartida.PASS);
                                    break;
                                }
                                else if(opcio == 2){
                                    partida.setEstat(Partida.EstatPartida.EXIT);
                                    break;
                                }
                            }
                            else
                                dausGuardats.add(daus.get(dauEscollit - 1));
                        }

                        else
                            sc.next();
                    }

                    protocol.take(num, dausGuardats);
                    partida.setEstat(Partida.EstatPartida.ROLL);

                    break;

                case PASS:
                    protocol.pass(id);
                    partida.setEstat(Partida.EstatPartida.PASS);
                    break;


                case EXIT:

                    System.out.println("Vols abandonar el joc? (0 (sí) | 1 (no))");
                    if (sc.nextInt() == 0) {
                        partida.setPartida(false);
                        protocol.exit();
                        //protocol.desconnexio();
                        protocol.read_play();

                    } else {
                        partida.setEstat(Partida.EstatPartida.BETT);
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


}


