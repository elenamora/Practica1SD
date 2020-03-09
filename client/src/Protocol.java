
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Protocol{

    InetAddress maquinaServidora;
    Socket socket = null;
    ComUtils utils;

    public void connexio(String nomMaquina, int numPort){

        try{

            maquinaServidora = InetAddress.getByName(nomMaquina);

            socket = new Socket(maquinaServidora, numPort);

            utils = new ComUtils(socket);

        }catch (IOException e){

            System.out.println("Error de connexió");
        }
    }

    public void start(int players) {

        try {

            utils.write_string("STRT");
            utils.write_blankSpace();
            utils.write_int32(players);

        } catch (IOException e) {


        }
    }


        public void bett(){

            try {

                utils.write_string("BETT");

            } catch (IOException e) {

                e.printStackTrace();
            }

        };

        public void take(int cant, ArrayList dados) {

            if (cant > 0) {

                // mandas take + posicion de todos lo dados (posiciones de la array)

            }

            else {

                // mandas take + 0x00

            }

            try {

                utils.write_string("TAKE");

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

        public void pass() {

            try {

                utils.write_string("PASS");

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

        public void exit() {

            try {

                utils.write_string("EXIT");

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

        public void cash(int dinero) {

            try {

                utils.write_string("CASH");
                utils.write_int32(dinero);

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

        public void loot(int dinero) {

            try {

                utils.write_string("LOOT");
                utils.write_int32(dinero);

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

        public void play(int turno) {

            try {

                utils.write_string("PLAY");
                utils.write_int32(turno);

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

        public void dice(ArrayList dados) {

            // for los elementos de la array mandar DICE + 0xXX

            try {

                utils.write_string("DICE");

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

        public void pnts(int score) {

            try {

                utils.write_string("PNTS");
                utils.write_int32(score);

            } catch (IOException e) {

                e.printStackTrace();
            }

        }



}
