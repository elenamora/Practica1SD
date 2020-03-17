
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client{

    public static void main(String[] args) throws IOException {

        String nomMaquina;
        int numPort, mode = 0;

        Protocol protocol = new Protocol();
        Joc joc = new Joc(protocol, mode);
        Scanner sc = new Scanner(System.in);


        if(args.length != 2){
            System.out.println("Us: java Client <maquina_servidora> <port> [0|1|2]");
            System.exit(1);
        }

        nomMaquina = args[0];
        numPort = Integer.parseInt(args[1]);

        if(args.length == 3) {

            mode = Integer.parseInt(args[2]);

            if (mode != 0 || mode != 1 || mode != 2) {
                System.out.println("Mode no vàlid. Automàticament es jugarà en mode manual.");
                mode = 0;
            }
        }

        protocol.connexio(nomMaquina, numPort);
        protocol.start();

        if (mode == 0 || mode == 2){
            joc.jugar();
        }
        else if(mode == 1){
            System.out.println("Quantes partides vols jugar?");
            int num = sc.nextInt();
            joc.jugarAutomatic(num);
        }

    }
}

