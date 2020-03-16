
import java.net.*;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class Joc {

    private Partida partida;
    private Protocol protocol;
    private Random r = new Random();
    private Scanner sc;
    private int mode;
    private int puntuacio1, puntuacio2, bote;


    public Joc(Protocol p, int mode){

        this.partida = new Partida();
        this.protocol = p;
        this.mode = mode;
        sc = new Scanner(System.in);

    }



    public void jugar(){

        while (partida.getPartida()){

            String command = null;

            try {
                command = protocol.utils.read_string();

            }catch (IOException ex){
                System.out.println("no s'ha rebut la comanda correctament");
                System.exit(1);
            }

            switch (partida.getEstat()){
                case BETT:
                    if(puntuacio1 == 0){
                        System.out.println("El jugador 1 no té diners per seguir jugant");
                        guanyarPartida();
                        protocol.desconnexio();
                        protocol.exit();

                    }
                    else if(puntuacio2 == 0){
                        System.out.println("El jugador 2 no té diners per seguir jugant");
                        guanyarPartida();
                        protocol.desconnexio();
                        protocol.exit();
                    }
                    else {
                        protocol.bett();
                        puntuacio1 -= 1;
                        puntuacio2 -= 1;
                        bote = 2;
                        partida.setEstat(Partida.EstatPartida.DEAL);
                    }

                    break;

                case ROLL:

                    break;


                case DEAL:
                    break;


                case PASS:
                    break;


                case END:
                    break;


                case EXIT:

                    System.out.println("Vols abandonar el joc? (0 (sí) | 1 (no))");
                    if (sc.nextInt() == 0){
                        guanyarPartida();
                        protocol.desconnexio();
                        protocol.exit();
                    }
                    else{
                        partida.setEstat(Partida.EstatPartida.BETT);
                    }


                    break;

                 default:
                     System.out.println("S'ha produit un Default");
                     break;


            }

        }

    }

    public void guanyarPartida(){
        if(puntuacio2 > puntuacio1){
            System.out.println("Ha guanyat el Jugador 2");

        }
        else if(puntuacio1 > puntuacio2){
            System.out.println("Ha guanyat el Jugador 1");

        }
        else {
            System.out.println("Hi ha hagut empat");

        }

        System.out.println("Puntació Jugador 1" + puntuacio1);
        System.out.print("Puntuació Jugador 2" + puntuacio2);
    }


}
