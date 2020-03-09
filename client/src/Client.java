
import java.net.*;

public class Client{


    public static void main(String[] args){

        String nomMaquina;
        int numPort, mode = 0;


        InetAddress maquinaServidora;
        Socket socket = null;
        ComUtils utils;

        Protocol protocol = new Protocol();


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




    }

}

