import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Protocol{

    InetAddress maquinaServidora;
    Socket socket;
    ComUtils utils;
    Scanner sc;


    /*** Funció que ens permet conncetar-nos a un socket ***/
    public void connexio(String nomMaquina, int numPort){

        try{

            maquinaServidora = InetAddress.getByName(nomMaquina);

            socket = new Socket(maquinaServidora, numPort);

            utils = new ComUtils(socket);

        }catch (IOException e){

            System.out.println("Error de connexió");
        }
    }

    /*** Funció que ens permet desconnectar-nos del socket ***/
    public void desconnexio(){
        try {
            if(socket != null){
                socket.close();
                System.out.println("Hem desconnectat de la partida");
            }
        }catch (IOException e){
            System.out.println("No s'ha pogut desconnectar del servidor");
        }
    }


    /*** Funció que ens permet iniciar la partida on hem d'indicar el nostre id com a jugadors ***/
    public void start(int id) {

        System.out.println("Hola");

        try {

            utils.write_string("STRT");
            utils.write_blankSpace();
            utils.write_int32(id);

        } catch (IOException e) {

            System.out.println("No s'ha pogut iniciar partida");
        }
    }


    public void bett(){

        try {
            utils.write_string("BETT");

        } catch (IOException e) {

            System.out.println("No s'ha pogut fer l'aposta inicial");
        }

    }

    public void take(int id, ArrayList dados) throws IOException {

        if (dados.size() > 0) {

            try {
                utils.write_string("TAKE");
                utils.write_blankSpace();
                utils.write_byte((byte) dados.size());

                for (int i = 0; i < dados.size(); i++) {

                    utils.write_blankSpace();
                    utils.write_byte((byte) dados.get(i));

                }
            } catch (IOException e) {
                System.out.println("No s'han pogut escollir els daus");
            }

        }else {

            try {

                utils.write_string("TAKE");
                utils.write_blankSpace();
                utils.write_byte((byte) dados.size());

            } catch (IOException e) {

                System.out.println("No s'han pogut escollir els daus");
            }

        }

    }

    /*** Funció que permet al jugador passar el seu torn ***/
    public void pass(int id) {

        try {
            utils.write_string("PASS");
            utils.write_blankSpace();
            utils.write_int32(id);

        } catch (IOException e) {
            System.out.println("No s'ha pogut passar");
        }
    }

    /*** Funció que permet al jugador abandonar el joc ***/
    public void exit() {

        try {
            utils.write_string("EXIT");

        } catch (IOException e) {
            System.out.println("No s'ha pogut sortir");
        }
    }


    /*** Funció que rep el cash que ens envia el Servidor ***/
    public int read_cash() throws IOException{

        System.out.println("Ahora estoy en cash!");

        String cash = "";
        int val = -1;

        cash = utils.read_string();
        utils.read_blankSpace();
        val = utils.read_int32();

        if (cash.equals("CASH")) {
            return val;
        }

        else{
            try {
                error();
            } catch (IOException e){
                System.out.println("Hi ha hagut un error al rebre el cash");
            }
        }

        return val;
    }



    public boolean read_loot() {

        String loot = "", blnc = "";
        int dinero = 0;

        try {
            loot = utils.read_string();
            blnc = utils.read_blankSpace();
            dinero = utils.read_int32();

        } catch (IOException e) {
            System.out.println("No hem pogut rebre el loot");
        }

        if (loot.equals("LOOT")){
            return true;
        }
        else{
            try {
                error();
            } catch (IOException e){
                System.out.println("Hi ha hagut un error al rebre el loot");
            }
        }
        return false;

    }

    /*** Funció que retorna un int indicant qui comença primer ***/
    public int read_play() {
        String play = "", blnc = "";
        int turn = 0;

        try {
            play = utils.read_string();
            blnc = utils.read_blankSpace();
            turn = utils.read_int32();
        }
        catch(IOException e) {
            System.out.println("No hem pogut rebre l'ordre de tirada dels jugadors");
        }

        if (play.equals("PLAY")){
            return turn;
        }
        else{
            try {
                error();
            } catch (IOException e){
                System.out.println("Hi ha hagut un error al rebre l'ordre de tirada");
            }
        }
        return turn;

    }

    /*** Funció que retorna els resultats dels daus que s'han tirat ***/
    public ArrayList read_dice() throws IOException {

        String dice = "";
        String blnc;
        ArrayList daus = new ArrayList();

        try {
            dice = utils.read_string();
            blnc = utils.read_blankSpace();

            for(int i = 0; i < 5; i++) {
                blnc = utils.read_blankSpace();
                daus.set(i, utils.read_int32());
            }

        }
        catch(IOException e) {
            System.out.println("No hem pogut tirar els daus");
        }

        if (dice.equals("DICE")) {
            return daus;
        }

        else{
            try {
                error();
            } catch (IOException e){
                System.out.println("Hi ha hagut un error al rebre els daus");
            }
        }

        return daus;
    }


    /*** Funció que retorna la puntuació obtinguda pel jugador ***/
    public int read_pnts() {

        String pnts = "";
        int score = 0;

        try {
            pnts = utils.read_string();
            score = utils.read_int32();
        }
        catch(IOException e) {
            System.out.println("No hem pogut rebre la teva puntuació des del servidor");
        }

        if (pnts.equals("PNTS")){
            return score;
        }
        else{
            try {
                error();
            } catch (IOException e){
                System.out.println("Hi ha hagut un error al rebre la puntuació");
            }
        }
        return score;

    }

    /*** Funció que ens retorna si hem guanyat o no la partida ***/
    public boolean read_win(){
        String win = "";

        try {
            win = utils.read_string();
        }
        catch(IOException e) {
            System.out.println("No hem pogut rebre guanyador del Servidor");
        }

        if (win.equals("WINS")){
            return true;
        }
        else{
            try {
                error();
            } catch (IOException e){
                System.out.println("Hi ha hagut un error al rebre el cash");
            }
        }
        return false;
    }

    /*** Funció que ens indica que s'ha produit un error ***/
    public void error() throws IOException{
        try {
            utils.write_string("ERRO");
            utils.write_blankSpace();
        }catch (IOException e){
            System.out.println("Hi ha hagut un error");
        }
    }



}
