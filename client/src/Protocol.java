import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Protocol{

    InetAddress maquinaServidora;
    Socket socket;
    ComUtils utils;


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
                utils.write_int32(id);
                utils.write_blankSpace();
                utils.write_byte((byte) dados.size());


                for (int i = 0; i < dados.size(); i++) {

                    utils.write_blankSpace();
                    int dau = (int) dados.get(i);
                    utils.write_byte((byte) dau);

                }
            } catch (IOException e) {
                System.out.println("No s'han pogut escollir els daus");
            }

        }else {

            try {

                utils.write_string("TAKE");
                utils.write_blankSpace();
                utils.write_int32(id);
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



    public int read_loot() {

        String loot = "", blnc = "";
        int dinero = 0;

        try {
            loot = utils.read_string();
            blnc = utils.read_blankSpace();
            dinero = utils.read_int32();

        } catch (IOException e) {
            System.out.println("No hem pogut rebre el loot");
        }

        if (loot.equals("LOOT")) {

            return dinero;

        }
        else{
            try {
                error();
            } catch (IOException e){
                System.out.println("Hi ha hagut un error al rebre el loot");
            }
        }
        return -1;

    }

    /*** Funció que retorna un int indicant qui comença primer ***/
    public int read_play() {
        String play = "";
        int turn = -1;

        try {

            play = utils.read_string();
            utils.read_blankSpace();
            turn = (int)utils.read_char();


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
        ArrayList daus = new ArrayList();

        try {

            dice = utils.read_string();
            System.out.println(dice);
            utils.read_blankSpace();
            utils.read_int32();


            for(int i = 0; i < 5; i++) {

                utils.read_blankSpace();

                daus.add((int) utils.read_char());

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

    /*** Funció que retorna el Take del contrincant ***/
    public ArrayList<Integer> read_take() {

        String take = "";
        int amm;
        ArrayList<Integer> dicesPos = new ArrayList<>();

        try {

            utils.read_blankSpace();
            utils.read_int32();
            utils.read_blankSpace();
            amm = (int)utils.read_byte();

            for (int i = 0; i < amm; i ++) {

                utils.read_blankSpace();
                dicesPos.add(Integer.valueOf(utils.read_byte()));
            }
        }
        catch(IOException e) {
            System.out.println("No hem pogut rebre el TAKE del contrincant");
        }

        if(take.equals("TAKE")){
            if(dicesPos.size() > 0) {
                return dicesPos;

            }
            else {
                dicesPos.add(-1);
                return dicesPos;
            }
        }

        else{
            try {
                error();
            } catch (IOException e){
                System.out.println("Hi ha hagut un error al rebre el TAKE");
            }
        }

        return dicesPos;
    }



    /*** Funció que ens indica si el contrincant ha passat ***/
    public boolean read_pass() {

        try {

            utils.read_blankSpace();
            utils.read_int32();
        }
        catch(IOException e) {
            System.out.println("No hem pogut rebre l'acció del contrincant");
        }

        return true;

    }

    /*** Funció que retorna la puntuació obtinguda pel jugador ***/
    public int read_pnts() {

        String pnts = "";
        int score = 0;

        try {
            pnts = utils.read_string();
            utils.read_blankSpace();
            utils.read_int32();
            utils.read_blankSpace();
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
    public int read_win(){
        String win = "";
        int ganador = -1;

        try {
            win = utils.read_string();
            utils.read_blankSpace();
            ganador = (int)utils.read_char();

        }
        catch(IOException e) {
            System.out.println("No hem pogut rebre guanyador del Servidor");
        }

        if (win.equals("WINS")){
            return ganador;
        }
        else{
            try {
                error();
            } catch (IOException e){
                System.out.println("Hi ha hagut un error al rebre el cash");
            }
        }
        return -1;
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

    public String getElement() {

        String el = "";

        try {

            el = utils.read_string();
            System.out.println(el);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return el;

    }

}
