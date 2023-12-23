package Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class GenerateMoves {
     /**
     * Searches for all possible moves on the game board, adding them in tree 
     * to store every possible route and move.
     * To make this posible, we keep track of the visited cells on the game board, and we start
     * forming groups of Tokens from the bottom row to the top row, securing that the first Token of a group
     * is always the bottom-most and left-most Token of the group.
     * Finnaly, we remove the groups of Tokens from the game board, and update the moves tree.
     * 
     * @param board    The game board as a 2D array of Token objects.
     * @param treeNode The root node of the moves tree.
     */
    public static void searchMoves(Token[][] board, MovesTree treeNode) {
        ArrayList<LinkedList<Token>> groups = new ArrayList<>();
        boolean[][] visited = new boolean[board.length][board[0].length];
        for (boolean[] fila : visited)
            Arrays.fill(fila, false);
        for (int i = board.length - 1; i >= 0; i--) {
            for (int j = 0; j < board[0].length; j++) {
                if (visited[i][j] == false && board[i][j].valid())
                    groups.add(formGroup(board, visited, i, j, board.length - 1, board[0].length - 1));
            }
        }
        Iterator<LinkedList<Token>> groupIterator = groups.iterator();
        while (groupIterator.hasNext()) {
            LinkedList<Token> currentGroup = groupIterator.next();
            int groupLength = currentGroup.size();
            if (groupLength >= 2) {
                Token[][] copiaTablero = copyBoard(board);
                removeGroup(copiaTablero, currentGroup, treeNode, groupLength);
            }
        }
    }

    /**
     * Forms a group of connected Tokens on the game board using depth-first search.
     *
     * @param board      The game board as a 2D array of Token objects.
     * @param visited    A 2D array representing the visited cells on the game board.
     * @param x          The row index of the current cell.
     * @param y          The column index of the current cell.
     * @param rowLength  The maximum row index of the game board.
     * @param colLength  The maximum column index of the game board.
     * @return A linked list containing the Tokens in one group.
     */
    public static LinkedList<Token> formGroup(Token[][] board, boolean[][] visited, int x, int y, int rowLength, int colLength) {
        LinkedList<Token> thisGroup = new LinkedList<>();
        thisGroup.add(board[x][y]);
        visited[x][y] = true;
        if (board[x][y].valid()) {
            if (y + 1 <= colLength && board[x][y].getColor() == board[x][y + 1].getColor() && visited[x][y + 1] == false) {
                thisGroup.addAll(formGroup(board, visited, x, y + 1, rowLength, colLength));
            }
            if (y - 1 >= 0 && board[x][y].getColor() == board[x][y - 1].getColor() && visited[x][y - 1] == false) {
                thisGroup.addAll(formGroup(board, visited, x, y - 1, rowLength, colLength));
            }
            if (x + 1 <= rowLength && board[x][y].getColor() == board[x + 1][y].getColor() && visited[x + 1][y] == false) {
                thisGroup.addAll(formGroup(board, visited, x + 1, y, rowLength, colLength));
            }
            if (x - 1 >= 0 && board[x][y].getColor() == board[x - 1][y].getColor() && visited[x - 1][y] == false) {
                thisGroup.addAll(formGroup(board, visited, x - 1, y, rowLength, colLength));
            }
        }
        return thisGroup;
    }

    /**
     * Creates a copy of the game board, a deep copy.
     *
     * @param boardOld The original game board as a 2D array of Token objects.
     * @return The copied game board as a 2D array of Token objects.
     */
    public static Token[][] copyBoard(Token[][] boardOld) {
        int rows = boardOld.length;
        int cols = boardOld[0].length;
        Token[][] boardNew = new Token[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Token current = boardOld[i][j];
                Token copied = new Token(current.getColor(), current.getRow(), current.getCol());
                boardNew[i][j] = copied;
            }
        }
        return boardNew;
    }

    /**
     * Removes a group of Tokens from the game board. 
     * With it creates a new node in the moves tree, and calls the searchMoves method to continue searching for moves.
     * The new node stores all data related to the move, like score, color, position and group length.
     * The method also updates the game board by moving Tokens down and shifting columns.
     * 
     * @param board       The game board as a 2D array of Token objects.
     * @param group       The group of Tokens to be removed.
     * @param treeNode    The parent node of the moves tree.
     * @param groupLength The length of the group.
     */
    public static void removeGroup(Token[][] board, LinkedList<Token> group, MovesTree treeNode, int groupLength) {
        Iterator<Token> iterator = group.iterator();
        Token firstToken = group.get(0);
        while (iterator.hasNext()) {
            Token Token = iterator.next();
            board[Token.getRow()][Token.getCol()].setColor('_');
        }
        int x = board.length - firstToken.getRow();
        int y = firstToken.getCol() + 1;
        Result score = new Result((int) Math.pow(groupLength - 2, 2), x, y, firstToken.getColor(), groupLength);
        MovesTree subBoard = new MovesTree(board, score, treeNode);
        treeNode.addChild(subBoard);
        fixBoard(board);
        searchMoves(board, subBoard);
    }

    /**
     * Fixes the actual game board after a move by moving Tokens all the way down and shifting columns if necesarry.
     *
     * @param board The game board as a 2D array of Token objects.
     */
    public static void fixBoard(Token[][] board) {
        for (int i = 0; i < board[0].length; i++) {
            int currentRow = board.length - 1;
            // Recorrer la columna desde abajo hacia arriba
            for (int j = board.length - 1; j >= 0; j--) {
                if (board[j][i].getColor() != '_') {
                    // Si es diferente al carácter objetivo, desplazarlo hacia abajo
                    board[currentRow--][i].setColor(board[j][i].getColor());
                }
            }
            while (currentRow >= 0) {
                board[currentRow--][i].setColor('_');
            }
        }
        for (int i = board[0].length - 1; i >= 0; i--) {
            if (board[board.length - 1][i].getColor() == '_') {
            // Shift all columns to the left
            for (int j = i; j < board[0].length - 1; j++) {
                for (int k = 0; k < board.length; k++) {
                    board[k][j].setColor(board[k][j + 1].getColor());
                }
            }
            // Set the last column to '_'
            for (int k = 0; k < board.length; k++) {
                board[k][board[0].length - 1].setColor('_');
            }
        }
        }
    }
}
