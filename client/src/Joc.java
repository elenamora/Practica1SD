
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
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

        while (protocol.read_cash()==-1){
        }

        puntuacio = p.read_cash();
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
                        protocol.bett();
                        puntuacio -= 1;
                        partida.setEstat(Partida.EstatPartida.ROLL);
                    }

                    while (!protocol.read_loot() && protocol.read_play() == -1){

                    }


                    break;

                case ROLL:
                    if (tirades > 0) {
                        daus = protocol.read_dice();
                        System.out.println("Els resultats de la tirada són:");
                        for (int i = 0; i < daus.size(); i++) {
                            System.out.println("Dau nº" + i + 1 + "és" + daus.get(i));
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
                    boolean podemEscollir = false;
                    int dauEscollit = sc.nextInt();

                    ArrayList dausPossibles = daus;

                    podemEscollir = escollir(podemEscollir);

                    // Podem escollir tots els daus que volguem feta una sola tirada sempre i quan compleixin les normes
                    while (podemEscollir && dauEscollit != 0) {
                        if (dausGuardats == null && dausPossibles.get(dauEscollit - 1).equals(6)) {
                            dausGuardats.add(6);
                            dausPossibles.set(dauEscollit - 1, 0);
                            System.out.println("Has escollit el dau" + dauEscollit + "amb valor 6");
                            System.out.println("De moment tens els daus:" + dausGuardats);
                        } else {
                            System.out.println("El valor del dau que pots escollir ha de ser igual a 6");
                        }
                        if (dausGuardats.size() == 1 && dausPossibles.get(dauEscollit - 1).equals(5)) {
                            dausGuardats.add(5);
                            dausPossibles.set(dauEscollit - 1, 0);
                            System.out.println("Has escollit els dau " + dauEscollit + "amb valor 5");
                            System.out.println("De moment tens els daus:" + dausGuardats);
                        } else {
                            System.out.println("El valor del dau que pots escollir ha de ser igual a 5");
                        }
                        if (dausGuardats.size() == 2 && dausPossibles.get(dauEscollit - 1).equals(4)) {
                            dausGuardats.add(4);
                            dausPossibles.set(dauEscollit - 1, 0);
                            System.out.println("Has escollit els dau " + dauEscollit + "amb valor 4");
                            System.out.println("De moment tens els daus:" + dausGuardats);
                        } else {
                            System.out.println("El valor del dau que pots escollir ha de ser igual a 4");
                        }

                        if (dausGuardats.size() == 3) {
                            for (int i = 0; i < dausPossibles.size(); i++) {
                                if (i != 0)
                                    dausGuardats.add(dausPossibles.get(i));
                            }

                            System.out.println("Has aconseguit 6-5-4! \n La teva puntuació és " + dausGuardats.get(3) + dausGuardats.get(4));
                        }

                        podemEscollir = escollir(false);
                        if (podemEscollir)
                            dauEscollit = sc.nextInt();
                    }

                    // Sempre i quan poguem tornar a tirar preguntem si volem tirar una altra vegada
                    if (tirades > 0) {
                        System.out.println("Vols tornar a tirar? (0 (sí), 1 (no), 2(sortir Joc)");
                        int var2 = sc.nextInt();
                        if (var2 == 0) {
                            partida.setEstat(Partida.EstatPartida.ROLL);
                        } else if (var2 == 1) {
                            partida.setEstat(Partida.EstatPartida.PASS);
                        } else if (var2 == 2) {
                            partida.setEstat(Partida.EstatPartida.EXIT);
                        } else {
                            System.out.println("Variable incorrecta");
                        }
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
                        guanyarPartida();
                        partida.setPartida(false);
                        protocol.desconnexio();
                        protocol.exit();
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
        if (protocol.read_win()) {
            System.out.println("Has guanyat");
        } else {
            System.out.println("Has perdut");
        }

        System.out.println("La teva puntuació final és: " + protocol.read_pnts() + "punts");

        while (protocol.read_cash() == -1){

        }
    }


    public boolean escollir(boolean escollir) {

        for (int i = 0; i < daus.size(); i++) {
            if (dausGuardats == null && i == 6) {
                escollir = true;
            }
            if (dausGuardats.size() == 1 && i == 5) {
                escollir = true;
            }
            if (dausGuardats.size() == 2 && i == 4) {
                escollir = true;
            }
            if (dausGuardats.size() == 3) {
                escollir = true;
            }
        }


        return escollir;
    }

    public void jugarAutomatic(int numPartides) throws IOException {

        for (int i = 0; i < numPartides; i++) {

            while (partida.getPartida()) {

                switch (partida.getEstat()) {
                    case BETT:
                        if (puntuacio == 0) {
                            System.out.println("No tens diners per seguir jugant");
                            partida.setEstat(Partida.EstatPartida.EXIT);
                            guanyarPartida();
                            protocol.desconnexio();
                            protocol.exit();
                        } else {
                            protocol.bett();
                            puntuacio -= 1;
                            partida.setEstat(Partida.EstatPartida.ROLL);
                        }

                        break;

                    case ROLL:
                        if (tirades > 0) {
                            daus = protocol.read_dice();
                            System.out.println("Els resultats de la tirada són:");
                            for (int j = 0; j < daus.size(); j++) {
                                System.out.println("Dau nº" + j + 1 + "és" + daus.get(j));
                            }
                            partida.setEstat(Partida.EstatPartida.TAKE);
                        } else {
                            System.out.println("No ens queden més tirades per a fer. Ja hem fet 3.");
                            partida.setEstat(Partida.EstatPartida.PASS);
                            break;
                        }

                        tirades -= 1;
                        break;

                    case TAKE:
                        int num = 0, suma = 0;
                        boolean podemEscollir = false;

                        ArrayList dausPossibles = daus;

                        podemEscollir = escollir(podemEscollir);

                        while (podemEscollir) {

                            for (int j = 0; j < dausPossibles.size(); j++) {
                                if (dausGuardats.size() == 0 && j == 6) {
                                    dausGuardats.add(6);
                                    dausPossibles.set(j, 0);
                                    System.out.println("Has escollit el dau  amb valor 6");
                                    System.out.println("De moment tens els daus:" + dausGuardats);
                                } else if (dausGuardats.size() == 1 && j == 5) {
                                    dausGuardats.add(5);
                                    dausPossibles.set(j, 0);
                                    System.out.println("Has escollit els dau amb valor 5");
                                    System.out.println("De moment tens els daus:" + dausGuardats);
                                } else if (dausGuardats.size() == 4 && j == 4) {
                                    dausGuardats.add(4);
                                    dausPossibles.set(j, 0);
                                    System.out.println("Has escollit els dau amb valor 4");
                                    System.out.println("De moment tens els daus:" + dausGuardats);
                                } else if (dausGuardats.size() >= 3 && j != 0) {
                                    dausGuardats.set(dausGuardats.size(), dausPossibles.get(j));
                                    suma = (int) dausGuardats.get(3) + (int) dausGuardats.get(4);
                                    if (suma > 7) {
                                        partida.setEstat(Partida.EstatPartida.PASS);
                                        protocol.take(id, dausGuardats);
                                        break;
                                    } else {
                                        partida.setEstat(Partida.EstatPartida.ROLL);
                                        break;
                                    }
                                }
                            }

                            podemEscollir = escollir(false);
                            if (podemEscollir)
                                partida.setEstat(Partida.EstatPartida.TAKE);

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


