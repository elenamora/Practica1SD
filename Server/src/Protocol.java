
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

public class Protocol {

    private Socket socket1, socket2;
    ComUtils utils1, utils2;
    ComUtils utilsLog;
    Random random = new Random();

    File file = new File("test");

    private int singlePlayer;

    int portServidor = 5000;
    ServerSocket serverSocket1, serverSocket2;

    private ComUtils[] utilsL;

    Protocol(String[] args) throws IOException {

        portServidor = Integer.parseInt(args[0]);

        singlePlayer = Integer.parseInt(args[1]);

        file.createNewFile();

        utilsLog = new ComUtils(new FileInputStream(file), new FileOutputStream(file));

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

            utils1 = new ComUtils(socket1);

            utilsL = new ComUtils[]{utils1};

        }

    }

    public void take (int jugadorActual, int id, ArrayList dados){

        if (dados.size() > 0) {

            try {

                utilsL[jugadorActual].write_string("TAKE");
                utilsLog.write_string("TAKE");
                utilsL[jugadorActual].write_blankSpace();
                utilsLog.write_blankSpace();
                utilsL[jugadorActual].write_int32(id);
                utilsLog.write_int32(id);
                utilsL[jugadorActual].write_blankSpace();
                utilsLog.write_blankSpace();
                utilsL[jugadorActual].write_byte((byte) dados.size());
                utilsLog.write_byte((byte) dados.size());

                for (int i = 0; i < dados.size(); i ++) {

                    utilsL[jugadorActual].write_blankSpace();
                    utilsLog.write_blankSpace();
                    int dau = (int) dados.get(i);
                    utilsL[jugadorActual].write_byte((byte) dau);
                    utilsLog.write_byte((byte) dau);

                }

                utilsLog.write_string("\n");

            }

            catch (IOException e) {

                e.printStackTrace();
            }

        }

        else {

            try {

                utilsL[jugadorActual].write_string("TAKE");
                utilsLog.write_string("TAKE");
                utilsL[jugadorActual].write_blankSpace();
                utilsLog.write_blankSpace();
                utilsL[jugadorActual].write_int32(id);
                utilsLog.write_int32(id);
                utilsL[jugadorActual].write_blankSpace();
                utilsLog.write_blankSpace();
                utilsL[jugadorActual].write_byte((byte) dados.size());
                utilsLog.write_byte((byte) dados.size());

                utilsLog.write_string("\n");

            }

            catch (IOException e) {

                e.printStackTrace();
            }

        }

    }

    public void pass (int jugadorActual, int id) {

        try {

            utilsL[jugadorActual].write_string("PASS");
            utilsLog.write_string("PASS");
            utilsL[jugadorActual].write_blankSpace();
            utilsLog.write_blankSpace();
            utilsL[jugadorActual].write_int32(id);
            utilsLog.write_int32(id);

            utilsLog.write_string("\n");

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void cash (int jugadorActual, int dinero){

        try {

            utilsL[jugadorActual].write_string("CASH");
            utilsLog.write_string("CASH");
            utilsL[jugadorActual].write_blankSpace();
            utilsLog.write_blankSpace();
            utilsL[jugadorActual].write_int32(dinero);
            utilsLog.write_int32(dinero);

            utilsLog.write_string("\n");

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void loot (int jugadorActual, int dinero){

        try {

            utilsL[jugadorActual].write_string("LOOT");
            utilsLog.write_string("LOOT");
            utilsL[jugadorActual].write_blankSpace();
            utilsLog.write_blankSpace();
            utilsL[jugadorActual].write_int32(dinero);
            utilsLog.write_int32(dinero);

            utilsLog.write_string("\n");

        }

        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void play (int jugadorActual, int turno){

        try {

            utilsL[jugadorActual].write_string("PLAY");
            utilsLog.write_string("PLAY");
            utilsL[jugadorActual].write_blankSpace();
            utilsLog.write_blankSpace();
            utilsL[jugadorActual].write_char((char)turno);
            utilsLog.write_char((char)turno);

            utilsLog.write_string("\n");

        }

        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void dice (int jugadorActual, int id, int[] dices){

        try {

            utilsL[jugadorActual].write_string("DICE");
            utilsLog.write_string("DICE");
            utilsL[jugadorActual].write_blankSpace();
            utilsLog.write_blankSpace();
            utilsL[jugadorActual].write_int32(id);
            utilsLog.write_int32(id);

            for (int i = 0; i < dices.length; i++) {

                utilsL[jugadorActual].write_blankSpace();
                utilsLog.write_blankSpace();
                utilsL[jugadorActual].write_char((char) dices[i]);
                utilsLog.write_char((char) dices[i]);

            }

            utilsLog.write_string("\n");

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

            utilsLog.write_string("PNTS");
            utilsLog.write_blankSpace();
            utilsLog.write_int32(id);
            utilsLog.write_blankSpace();
            utilsLog.write_int32(score);

        }

        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void wins (int jugadorActual, int win){

        try {

            utilsL[jugadorActual].write_string("WINS");
            utilsL[jugadorActual].write_blankSpace();
            utilsL[jugadorActual].write_char((char) win);

            utilsLog.write_string("WINS");
            utilsLog.write_blankSpace();
            utilsLog.write_char((char) win);

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
        int id, el;

        try {

            utilsL[jugadorActual].read_blankSpace();
            utilsLog.write_blankSpace();
            id = utilsL[jugadorActual].read_int32();
            utilsLog.write_int32(id);
            utilsL[jugadorActual].read_blankSpace();
            utilsLog.write_blankSpace();
            amm = (int)utilsL[jugadorActual].read_byte();
            utilsLog.write_int32(amm);

            if (amm > 0) {

                for (int i = 0; i < amm; i ++) {

                    utilsL[jugadorActual].read_blankSpace();
                    utilsLog.write_blankSpace();
                    el = Integer.valueOf(utilsL[jugadorActual].read_byte());
                    utilsLog.write_int32(el);
                    dicesPos.add(el);

                }

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
            utilsLog.write_string(element);

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

    public void desconnexio(int jugadorActual) {

        if (jugadorActual == 0) {

            try {
                if (socket1 != null) {
                    socket1.close();
                    System.out.println("Hem desconnectat de la partida");
                }
            } catch (IOException e) {
                System.out.println("No s'ha pogut desconnectar del servidor");
            }

        } else {

            try {
                if (socket2 != null) {
                    socket2.close();
                    System.out.println("Hem desconnectat de la partida");
                }
            } catch (IOException e) {
                System.out.println("No s'ha pogut desconnectar del servidor");
            }

        }

    }

}