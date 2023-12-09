import java.util.ArrayList;

/**
 * Represents a tree structure for storing game moves and their associated data.
 */
public class MovesTree {
    private MovesTree padre;
    private Token[][] estado;
    private ArrayList<MovesTree> hijos;
    private Result puntuacion;
    private int TokensRestantes;

    /**
     * Constructs a MovesTree object with the given state, score, and parent.
     *
     * @param estado     The state of the game board.
     * @param puntuacion The score associated with the game state.
     * @param padre      The parent MovesTree object.
     */
    public MovesTree(Token[][] estado, Result puntuacion, MovesTree padre) {
        this.estado = estado;
        this.hijos = new ArrayList<>();
        this.puntuacion = puntuacion;
        this.padre = padre;
        this.TokensRestantes = getRemainingTokens(estado);
    }

    /**
     * Constructs the root of the tree.
     *
     * @param estado The state of the game board.
     */
    public MovesTree(Token[][] estado) {
        this.estado = estado;
        this.hijos = new ArrayList<>();
        this.puntuacion = null;
        this.padre = null;
    }

    /**
     * Returns all the score data associated with the game state.
     *
     * @return The score associated with the game state.
     */
    public Result getData() {
        return puntuacion;
    }

    /**
     * Returns this nodes parent MovesTree node.
     *
     * @return The parent MovesTree object.
     */
    public MovesTree getFather() {
        return padre;
    }

    /**
     * Adds a child MovesTree object to the current MovesTree node.
     *
     * @param hijo The child MovesTree object to be added.
     */
    public void addChild(MovesTree hijo) {
        if (hijos == null) {
            hijos = new ArrayList<>();
        }
        hijos.add(hijo);
    }

    /**
     * Returns the list of childs MovesTree objects that this node has.
     *
     * @return The list of child MovesTree objects.
     */
    public ArrayList<MovesTree> getChilds() {
        return hijos;
    }

    /**
     * Returns the state of the game board.
     *
     * @return The state of the game board.
     */
    public Token[][] getBoard() {
        return estado;
    }

    /**
     * Calculates and returns the number of remaining tokens on the game board.
     *
     * @param board The game board.
     * @return The number of remaining tokens on the game board.
     */
    public int getRemainingTokens(Token[][] board) {
        int remaining = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j].getColor() != '_') {
                    remaining++;
                }
            }
        }
        return remaining;
    }

    /**
     * Calculates and returns the total score of the game up to this point in the route.
     *
     * @return The total score of the game.
     */
    public int getTotalScore() {
        int totalScore = this.getData().getPoints();
        MovesTree father = this.getFather();
        while (father.getData() != null) {
            totalScore += father.getData().getPoints();
            father = father.getFather();
        }
        if (this.getRemainingTokens(this.getBoard()) == 0) {
            totalScore += 1000;
        }
        return totalScore;
    }
}
