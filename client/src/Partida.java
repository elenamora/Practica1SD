

public class Partida {

    private boolean partida = false;
    private EstatPartida estat;
    public static enum EstatPartida{START, BETT, ROLL, PASS, DEAL, END, EXIT}

    public Partida(){
        this.partida = true;
        estat = EstatPartida.START;
    }

    public void setPartida(boolean partida){
        this.partida = partida;
    }

    public boolean getPartida(){
        return this.partida;
    }

    public void setEstat(EstatPartida estat){
        this.estat = estat;
    }

    public  EstatPartida getEstat(){
        return this.estat;
    }
}
