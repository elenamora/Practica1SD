
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Client{

    public static void main(String[] args2) throws IOException, TimeoutException {

        String[] args = {"25.99.172.37", "5000", "0"};
        //String[] args = {"25.71.79.215", "5001", "0"};


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



        int num = 0;
        if(mode == 1){
            System.out.println("Quantes partides vols jugar?");
            num = sc.nextInt();
        }

        Joc joc = new Joc(protocol, mode, id, num);

    }
}

