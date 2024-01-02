package Control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import Model.GenerateMoves;
import Model.MovesTree;
import Model.Result;
import Model.Token;
import View.GameGUI;

/**
 * This class handles the click of a button in the panel.
 */
public class ButtonControl {
    /**
     * Handles the button click event by performing the necessary actions specified in the first assigment.
     * 
     * @param board The game board represented as a 2D array of tokens.
     * @param row The row index of the clicked button.
     * @param col The column index of the clicked button.
     */
    public static void handleButtonClick(Token[][] board, int row, int col){
        boolean[][] visited = new boolean[board.length][board[0].length];
        for (boolean[] fila : visited)
            Arrays.fill(fila, false);
        LinkedList<Token> group = GenerateMoves.formGroup(board, visited, row, col, board.length - 1, board[0].length - 1);
        if(group.size() == 1){
            JOptionPane.showMessageDialog(null, "No se puede eliminar: la ficha seleccionada no forma un grupo.", "Error", JOptionPane.ERROR_MESSAGE);
            return ; // Ignore the click has it does not form a group
        }
        GameGUI.storeMove();
        Iterator<Token> iterator = group.iterator();
        Token firstToken = group.get(0);
        char groupColor = firstToken.getColor();
        int groupLength = 0;
        int[] startPosition = new int[2];
        startPosition[0] = firstToken.getRow();
        startPosition[1] = firstToken.getCol();
        while (iterator.hasNext()) {
            Token Token = iterator.next();
            board[Token.getRow()][Token.getCol()].setColor('_');
            groupLength++;
            if(Token.getRow() > startPosition[0]){
                startPosition[0] = Token.getRow();
            }
            if(Token.getCol() < startPosition[1]){
                startPosition[1] = Token.getCol();
            }
        }
        int x = board.length - startPosition[0];
        int y = startPosition[1] + 1;
        Result score = new Result((int) Math.pow(groupLength - 2, 2), x, y, groupColor, groupLength);
        GameGUI.showResult(score);       
        GenerateMoves.fixBoard(board);
        GameGUI.updateBoard(board);
        GameGUI.updateScoreField(score.getPoints());
        GameGUI.updateTokensField(MovesTree.getRemainingTokens(board));
        GameGUI.updateMovesField();
        checkEnd(board);
    }

    public static void checkEnd(Token[][] board){
        ArrayList<LinkedList<Token>> groups = new ArrayList<>();
        boolean[][] visited = new boolean[board.length][board[0].length];
        for (boolean[] fila : visited)
            Arrays.fill(fila, false);
        for (int i = board.length - 1; i >= 0; i--) {
            for (int j = 0; j < board[0].length; j++) {
                if (visited[i][j] == false && board[i][j].valid())
                    groups.add(GenerateMoves.formGroup(board, visited, i, j, board.length - 1, board[0].length - 1));
            }
        }
        for (LinkedList<Token> group : groups) {
            if (group.size() >= 2) {
                return ; // Game is not ended, there are still possible moves
            }
        }
        GameGUI.endGame(); // Game is ended, there are no more possible moves
    }

}
