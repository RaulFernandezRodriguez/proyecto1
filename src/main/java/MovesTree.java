import java.util.ArrayList;

public class MovesTree {
    private MovesTree padre;
    private Ficha[][] estado;
    private ArrayList<MovesTree> hijos;
    private Result puntuacion;
    private int fichasRestantes;

    public MovesTree(Ficha[][] estado, Result puntuacion, MovesTree padre){
        this.estado = estado;
        this.hijos = new ArrayList<>();
        this.puntuacion = puntuacion;
        this.padre = padre;
        this.fichasRestantes = getRemainingTokens(estado);
    }

    public MovesTree(Ficha[][] estado){
        this.estado = estado;
        this.hijos = new ArrayList<>();
        this.puntuacion = null;
        this.padre = null;
    }

    public Result getData(){
        return puntuacion;
    }

    public MovesTree getFather(){
        return padre;
    }

    public void addChild(MovesTree hijo) {
        if (hijos == null) {
            hijos = new ArrayList<>();
        }
        hijos.add(hijo);
    }

    public ArrayList<MovesTree> getChilds() {
        return hijos;
    }

    public Ficha[][] getBoard(){
        return estado;
    }

    public int getRemainingTokens(Ficha[][] board){
        int remaining = 0;
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(board[i][j].getColor() != '_'){
                    remaining++;
                }
            }
        }
        return remaining;
    }
}
