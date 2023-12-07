public class Ficha {
    private char color;
    private int row;
    private int col;

    public Ficha(char color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
    }

    public char getColor() {
        return color;
    }

    public void setColor(char color) {
        this.color = color;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean valid(){
        if(this.getColor() == 'V' || this.getColor() == 'R' || this.getColor() == 'A'){
            return true;
        }else{
            return false;
        }
    }
}

