
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

public class Protocol {

    private Socket socket1, socket2;
    ComUtils utils1, utils2;
    Random random = new Random();

    private int singlePlayer;

    int portServidor = 5000;
    ServerSocket serverSocket1, serverSocket2;

    private ComUtils[] utilsL;

    Protocol(String[] args) throws IOException {

        portServidor = Integer.parseInt(args[0]);

        singlePlayer = Integer.parseInt(args[1]);

        if ( singlePlayer == 2){

            serverSocket1 = new ServerSocket(portServidor);
            serverSocket2 = new ServerSocket(portServidor + 1);

            socket1 = serverSocket1.accept();
            socket2 = serverSocket2.accept();

            utils1 = new ComUtils(socket1);
            utils2 = new ComUtils(socket2);

            utilsL = new ComUtils[]{utils1, utils2};

        }

        else {

            serverSocket1 = new ServerSocket(portServidor);

            socket1 = serverSocket1.accept();

            System.out.println("Hola");

            utils1 = new ComUtils(socket1);

            utilsL = new ComUtils[]{utils1};

        }

    }

    /*
    public void xd() {

        try{

            serverSocket = new ServerSocket(portServidor);
            System.out.println("Servidor Socket preparat en el port"+ portServidor);

            while (true){
                System.out.println("Esperant una connexió d'un client");

                socket = serverSocket.accept();
                System.out.println("Connexió acceptada d'un client");


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

    public void connexio(String nomMaquina, int numPort) {

        try {

            maquinaServidora = InetAddress.getByName(nomMaquina);

            socket = new Socket(maquinaServidora, numPort);

            utils = new ComUtils(socket);

        } catch (IOException e) {

            System.out.println("Error de connexió");
        }
    }

     */

    public void take (int jugadorActual, int cant, ArrayList dados){

        if (cant > 0) {

            try {

                utilsL[jugadorActual].write_string("TAKE");
                utilsL[jugadorActual].write_blankSpace();
                utilsL[jugadorActual].write_byte((byte) cant);

                for (int i = 0; i < cant; i ++) {

                    utilsL[jugadorActual].write_blankSpace();
                    utilsL[jugadorActual].write_byte((byte) dados.get(i));

                }

            } catch (IOException e) {

                e.printStackTrace();
            }


        } else {

            try {

                utilsL[jugadorActual].write_string("TAKE");
                utilsL[jugadorActual].write_blankSpace();
                utilsL[jugadorActual].write_byte((byte) cant);

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

    }

    public void pass (int jugadorActual, int id) {

        try {

            utilsL[jugadorActual].write_string("PASS");
            utilsL[jugadorActual].write_blankSpace();
            utilsL[jugadorActual].write_int32(id);

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void cash (int jugadorActual, int dinero){

        try {

            utilsL[jugadorActual].write_string("CASH");
            utilsL[jugadorActual].write_blankSpace();
            utilsL[jugadorActual].write_int32(dinero);

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void loot (int jugadorActual, int dinero){

        try {

            utilsL[jugadorActual].write_string("LOOT");
            utilsL[jugadorActual].write_blankSpace();
            utilsL[jugadorActual].write_int32(dinero);

        }

        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void play (int jugadorActual, int turno){

        try {

            utilsL[jugadorActual].write_string("PLAY");
            utilsL[jugadorActual].read_blankSpace();
            utilsL[jugadorActual].write_int32(turno);

        }

        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void dice (int jugadorActual, int[] dices){

        try {

            utilsL[jugadorActual].write_string("DICE");

            for (int i = 0; i < dices.length; i++) {

                utilsL[jugadorActual].write_blankSpace();
                utilsL[jugadorActual].write_string(Integer.toString(dices[i]));


            }

        }

        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void pnts (int jugadorActual, int score){

        try {

            utilsL[jugadorActual].write_string("PNTS");
            utilsL[jugadorActual].write_blankSpace();
            utilsL[jugadorActual].write_int32(score);

        }

        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void wins (int jugadorActual, int win){

        try {

            utilsL[jugadorActual].write_string("WINS");
            utilsL[jugadorActual].write_blankSpace();
            utilsL[jugadorActual].write_int32(win);

        }

        catch (IOException e) {

            e.printStackTrace();

        }

    }

    public int turn(){

        int turn = random.nextInt(2);

        return turn;

    }

    public int readStrt(int jugadorActual) {

        String read = "";
        int val = -1;

        try {

            read = utilsL[0].read_string();
            utilsL[0].read_blankSpace();
            val = utilsL[0].read_int32();

            System.out.println(Integer.toString(val));


        }

        catch(IOException e) {

            e.printStackTrace();

        }

        if (read.equals("STRT")) {

            System.out.println("Holax2");

            return val;



        }

        return -1;

    }

    public boolean readBett(int jugadorActual) {

        String read = "";

        try {

            read = utilsL[jugadorActual].read_string();

        }

        catch(IOException e) {

            error(jugadorActual,4, "Error");
            e.printStackTrace();

        }

        if (read.equals("BETT")) {

            return true;

        }

        else {

            return  false;

        }

    }

    public ArrayList<Integer> readTake(int jugadorActual) {

        String read = "";
        String amm;
        ArrayList<Integer> dicesPos = new ArrayList<>();

        try {

            read = utilsL[jugadorActual].read_string();
            utilsL[jugadorActual].read_blankSpace();
            amm = utilsL[jugadorActual].read_string();

            for (int i = 0; i < Integer.valueOf(amm); i ++) {

                utilsL[jugadorActual].read_blankSpace();
                dicesPos.add(Integer.valueOf(utilsL[jugadorActual].read_string()));

            }


        }

        catch(IOException e) {

            error(jugadorActual,4, "Error");

            e.printStackTrace();

        }

        if(dicesPos.size() > 0) {

            return dicesPos;

        }

        else {

            dicesPos.add(-1);
            return dicesPos;

        }

    }

    public boolean readPass(int jugadorActual) {

        String read = "";

        try {

            read = utilsL[jugadorActual].read_string();

        }

        catch(IOException e) {

            error(jugadorActual,4, "Error");
            e.printStackTrace();

        }

        if (read.equals("PASS")) {

            return true;

        }

        else {

            return  false;

        }

    }

    public boolean readExit(int jugadorActual) {

        String read = "";

        try {

            read = utilsL[jugadorActual].read_string();

        }

        catch(IOException e) {

            error(jugadorActual,4, "Error");
            e.printStackTrace();

        }

        if (read.equals("EXIT")) {

            return true;

        }

        else {

            return  false;

        }

    }

    public void error(int jugadorActual, int hd, String txtError) {

        try {

            utilsL[jugadorActual].write_string("ERRO");
            utilsL[jugadorActual].write_blankSpace();
            utilsL[jugadorActual].write_string_variable(hd, txtError);

        }
        catch(IOException e) {

            e.printStackTrace();

        }

    }

}