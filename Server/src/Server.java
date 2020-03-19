import java.io.*;

public class Server{


    public static void main(String[] args) throws IOException {

        String[] args2 = {"5000", "1"};

        if(args2.length != 2){

            System.out.println("Us: java Client <port> [1|2]");
            System.exit(1);

        }


        if(args2.length == 2){

            Joc joc = new Joc(args2);
            joc.newGame();

        }

    }

}