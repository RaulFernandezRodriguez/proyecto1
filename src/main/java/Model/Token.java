package Model;

/**
 * The Token class represents a game token in the board, with a color, row, and column position.
 */
public class Token {
    private char color;
    private int row;
    private int col;

    /**
     * Constructs a Token object with the specified color, row, and column.
     *
     * @param color the color of the token
     * @param row   the row position of the token
     * @param col   the column position of the token
     */
    public Token(char color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the color of the token.
     *
     * @return the color of the token
     */
    public char getColor() {
        return color;
    }

    /**
     * Sets the color of the token.
     *
     * @param color the color of the token
     */
    public void setColor(char color) {
        this.color = color;
    }

    /**
     * Returns the row position of the token.
     *
     * @return the row position of the token
     */
    public int getRow() {
        return row;
    }

    /**
     * Sets the row position of the token.
     *
     * @param row the row position of the token
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Returns the column position of the token.
     *
     * @return the column position of the token
     */
    public int getCol() {
        return col;
    }

    /**
     * Sets the column position of the token.
     *
     * @param col the column position of the token
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Checks if the token has a valid color, which could be Rojo, Azul or Verde.
     *
     * @return true if the color is valid, false otherwise
     */
    public boolean valid() {
        if (this.getColor() == 'V' || this.getColor() == 'R' || this.getColor() == 'A') {
            return true;
        } else {
            return false;
        }
    }
}

