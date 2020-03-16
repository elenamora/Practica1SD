
import java.io.*;
import java.net.*;

public class Server{


    public static void main(String[] args) {

        int portServidor = 1234, mode;
        ServerSocket serverSocket = null;
        Socket socket = null;

        ComUtils utils;


        if(args.length > 2){
            System.out.println("Us: java Client <port> [1|2]");
            System.exit(1);
        }

        if(args.length == 2){
            portServidor = Integer.parseInt(args[0]);
            mode = Integer.parseInt(args[1]);
        }

        try{

            serverSocket = new ServerSocket(portServidor);
            System.out.println("Servidor Socket preparat en el port"+ portServidor);

            while (true){
                System.out.println("Esperant una connexió d'un client");

                socket = serverSocket.accept();
                System.out.println("Connexió acceptada d'un client");

                utils = new ComUtils(socket);

            }

        }catch (IOException ex){
            System.out.println("No s'ha pogut crear el servidor");
        }finally {
            try {
                if(serverSocket != null) serverSocket.close();
            }catch (IOException ex){
                System.out.println("No s'ha pogut establir connexió amb el client");
            }
        }


    }



}
