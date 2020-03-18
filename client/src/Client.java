
import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Client{

    public static void main(String[] args2) throws IOException, TimeoutException {

        String[] args = {"25.71.79.215", "5000", "0"};

        String nomMaquina;
        int numPort, mode = 0, id ;
        Scanner sc = new Scanner(System.in);


        if(args.length != 3){
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

        System.out.print("Posa el teu id: ");
        id = sc.nextInt();

        Protocol protocol = new Protocol();

        protocol.connexio(nomMaquina, numPort);

        protocol.start(id);

        Joc joc = new Joc(protocol, mode, id);

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

