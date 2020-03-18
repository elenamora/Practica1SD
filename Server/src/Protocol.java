
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

public class Protocol {

    InetAddress maquinaServidora;
    Socket socket = null;
    ComUtils utils;
    Random random = new Random();

    int portServidor = 1215;
    ServerSocket serverSocket = null;

    Protocol(String[] args){

        portServidor = Integer.parseInt(args[0]);

    }

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

    public void take ( int cant, ArrayList dados){

        if (cant > 0) {

            // mandas take + posicion de todos lo dados (posiciones de la array)

        } else {

            // mandas take + 0x00

        }

        try {

            utils.write_string("TAKE");

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void pass () {

        try {

            utils.write_string("PASS");

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void cash (int jugador, int dinero){

        try {

            utils.write_string("CASH");
            utils.write_int32(dinero);

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void loot (int jugador, int dinero){

        try {

            utils.write_string("LOOT");
            utils.write_int32(dinero);

        }

        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void play (int jugador, int turno){

        try {

            utils.write_string("PLAY");
            utils.write_int32(turno);

        }

        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void dice (int[] dices){

        try {

            utils.write_string("DICE");

            for (int i = 0; i < dices.length; i++) {

                utils.write_blankSpace();
                utils.write_string(Integer.toString(dices[i]));


            }

        }

        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void pnts (int score){

        try {

            utils.write_string("PNTS");
            utils.write_int32(score);

        }

        catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void wins (int win){

        try {

            utils.write_string("WINS");
            utils.write_int32(win);

        }

        catch (IOException e) {

            e.printStackTrace();

        }

    }

    public int turn(){

        int turn = random.nextInt(2);

        return turn;

    }

    public int readStrt(int jugador) {

        String read = "";
        int val = -1;

        try {

            read = utils.read_string();
            //utils.read_blankSpace();
            val = utils.read_int32();

        }

        catch(IOException e) {

            error(4, "Error");

            e.printStackTrace();

        }

        if (read.equals("STRT")) {

            if (val <= 2 && val >= 0) {

                return val;

            }

            else  {

                error(4, "Error: valor de jugador incorrecte");

            }

        }

        return -1;

    }

    public boolean readBett(int jugador) {

        String read = "";

        try {

            read = utils.read_string();

        }

        catch(IOException e) {

            error(4, "Error");
            e.printStackTrace();

        }

        if (read.equals("BETT")) {

            return true;

        }

        else {

            return  false;

        }

    }

    public ArrayList<Integer> readTake() {

        String read = "";
        String amm;
        ArrayList<Integer> dicesPos = new ArrayList<>();

        try {

            read = utils.read_string();
            //utils.read_blankSpace();
            amm = utils.read_string();

            for (int i = 0; i < Integer.valueOf(amm); i ++) {

                dicesPos.add(Integer.valueOf(utils.read_string()));

            }


        }

        catch(IOException e) {

            error(4, "Error");

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

    public boolean readPass() {

        String read = "";

        try {

            read = utils.read_string();

        }

        catch(IOException e) {

            error(4, "Error");
            e.printStackTrace();

        }

        if (read.equals("PASS")) {

            return true;

        }

        else {

            return  false;

        }

    }

    public boolean readExit() {

        String read = "";

        try {

            read = utils.read_string();

        }

        catch(IOException e) {

            error(4, "Error");
            e.printStackTrace();

        }

        if (read.equals("EXIT")) {

            return true;

        }

        else {

            return  false;

        }

    }

    public void error(int hd, String txtError) {

        try {

            utils.write_string("ERRO");
            utils.write_blankSpace();
            utils.write_string_variable(hd, txtError);

        }
        catch(IOException e) {

            e.printStackTrace();

        }

    }

}