
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

        if (singlePlayer == 2) {

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

    public void take (int jugadorActual, int id, ArrayList dados){

        if (dados.size() > 0) {

            try {

                utilsL[jugadorActual].write_string("TAKE");
                utilsL[jugadorActual].write_blankSpace();
                utilsL[jugadorActual].write_int32(id);
                utilsL[jugadorActual].write_blankSpace();
                utilsL[jugadorActual].write_byte((byte) dados.size());

                System.out.println(dados.size());

                for (int i = 0; i < dados.size(); i ++) {

                    utilsL[jugadorActual].write_blankSpace();
                    int dau = (int) dados.get(i);
                    utilsL[jugadorActual].write_byte((byte) dau);

                }

            } catch (IOException e) {

                e.printStackTrace();
            }


        } else {

            try {

                utilsL[jugadorActual].write_string("TAKE");
                utilsL[jugadorActual].write_blankSpace();
                utilsL[jugadorActual].write_byte((byte) dados.size());

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

    }

    public void pass (int jugadorActual, int id) {

        System.out.println("HOLA");

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
            utilsL[jugadorActual].write_blankSpace();
            utilsL[jugadorActual].write_char((char)turno);

        }

        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void dice (int jugadorActual, int id, int[] dices){

        try {

            utilsL[jugadorActual].write_string("DICE");
            utilsL[jugadorActual].write_blankSpace();
            utilsL[jugadorActual].write_int32(id);

            for (int i = 0; i < dices.length; i++) {

                utilsL[jugadorActual].write_blankSpace();

                utilsL[jugadorActual].write_char((char) dices[i]);

            }

        }

        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void pnts (int jugadorActual, int id, int score){

        try {

            utilsL[jugadorActual].write_string("PNTS");
            utilsL[jugadorActual].write_blankSpace();
            utilsL[jugadorActual].write_int32(id);
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

        int val = -1;

        try {

            utilsL[jugadorActual].read_string();
            utilsL[jugadorActual].read_blankSpace();
            val = utilsL[jugadorActual].read_int32();

        }

        catch(IOException e) {

            e.printStackTrace();

        }

        return val;

    }

    public ArrayList<Integer> readTake(int jugadorActual) {

        int amm = 0;
        ArrayList<Integer> dicesPos = new ArrayList<>();

        try {

            utilsL[jugadorActual].read_blankSpace();
            utilsL[jugadorActual].read_int32();
            utilsL[jugadorActual].read_blankSpace();
            amm = (int)utilsL[jugadorActual].read_byte();

            for (int i = 0; i < amm; i ++) {

                utilsL[jugadorActual].read_blankSpace();

                dicesPos.add(Integer.valueOf(utilsL[jugadorActual].read_byte()));

            }


        }

        catch(IOException e) {

            error(jugadorActual,4, "Error");

            e.printStackTrace();

        }

        if(amm > 0) {

            return dicesPos;

        }

        else {

            dicesPos.add(-1);
            return dicesPos;

        }

    }

    public String readElement(int jugadorActual) {

        String element = "";

        try {

            element  = utilsL[jugadorActual].read_string();

        }

        catch (IOException e) {

            e.printStackTrace();

        }

        return element;

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