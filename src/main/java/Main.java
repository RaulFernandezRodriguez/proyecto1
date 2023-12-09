
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The Main class represents the entry point of the program. It contains the main method
 * to read the input, store the games, and play each game by searching for the best moves.
 */
public class Main {
    private static int games;
    private static int actualGame;

    /**
     * The main method is the entry point of the program. It reads the number fo games, and starts
     * playing each game one by one by searching for the best moves, after storing the input of each game.
     * 
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        games = Integer.parseInt(scanner.nextLine());
        if (games <= 0) {
            //System.out.println("Error, numero de juegos invalido");
        } else {
            scanner.nextLine(); // Consume the newline character after the number of games.
            actualGame = 1;
            while (actualGame <= games) {
                String gameInput;
                if (actualGame == games) {
                    gameInput = storeLastGame(scanner);
                } else {
                    gameInput = storeGame(scanner);
                }
                Token[][] board = gameBoard(gameInput);
                if (board != null) {
                    play(board);
                } else {
                    //System.out.println("Error en el juego " + actualGame);
                    break;
                }
                actualGame++;
                if (actualGame == 1)
                    scanner.nextLine();

                // Verificar si hay más líneas después del juego actual
                if (!scanner.hasNext()) {
                    break;
                }
            }
        }
        scanner.close();
    }

    /**
     * Reads the input of a game,scores it in a StringBuilder and returns it as a string.
     * The input of a game ends when a blank line is found.
     * 
     * @param scanner The scanner object used to read the input.
     * @return The input of one game as a string.
     */
    private static String storeGame(Scanner scanner) {
        StringBuilder gameInput = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty() || !scanner.hasNextLine()) {
                break; // Fin del juego actual.
            }
            gameInput.append(line).append("\n");
        }
        return gameInput.toString().trim(); // Eliminar el último carácter '\n'.
    }

    /**
     * Reads the input of the last game and returns it as a string.
     *
     * @param scanner The scanner object used to read the input.
     * @return The input of the last game as a string.
     */
    private static String storeLastGame(Scanner scanner) {
        StringBuilder lastGameInput = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                break; // Se encontró un espacio en blanco, final del último juego.
            }
            lastGameInput.append(line).append("\n");
        }
        return lastGameInput.toString().trim();
    }

    /**
     * Converts the game input string into a 2D static array of Token objects representing the game board.
     * The method wont create the game board if it identifies any issue with the input (like different token 
     * colors or row lengths).
     * 
     * @param gameInput The input of the game as a string.
     * @return The game board as a 2D static array of Token objects, or null if the input is invalid in some way.
     */
    private static Token[][] gameBoard(String gameInput) {
        String[] rows = gameInput.split("\n");
        int numRows = rows.length;
        int numCols = rows[0].length();
        if (numCols > 20) {
            return null;
        }
        Token[][] board = new Token[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            String row = rows[i];
            for (int j = 0; j < numCols; j++) {
                Token token = new Token(row.charAt(j), i, j);
                if (token.valid() && row.length() == numCols) {
                    board[i][j] = token;
                } else {
                    return null;
                }
            }
        }
        return board;
    }

    /**
     * Plays the game by searching for the best moves.
     * It creates a tree storing all the posible moves and their scores, to later search for the best moves.
     * 
     * @param board The game board as a 2D array of Token objects.
     */
    public static void play(Token[][] board) {
        MovesTree original = new MovesTree(board);
        searchMoves(board, original);
        searchBestMoves(original);
    }

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
                    //visited[i][j] = true;
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
        while (iterator.hasNext()) {
            Token Token = iterator.next();
            board[Token.getRow()][Token.getCol()].setColor('_');
        }
        Token token = group.get(0);
        int x = board.length - token.getRow();
        int y = token.getCol() + 1;
        Result score = new Result((int) Math.pow(groupLength - 2, 2), x, y, token.getColor(), groupLength);
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
                for (int j = board.length - 1; j >= 0; j--) {
                    if (i + 1 < board[0].length) {
                        board[j][i].setColor(board[j][i + 1].getColor());
                        board[j][i + 1].setColor('_');
                    }
                }
            }
        }
    }

    /**
     * Searches for the best moves in the moves tree and prints the result.
     *
     * @param root The root node of the moves tree.
     */
    public static void searchBestMoves(MovesTree root) {
        int[] maxScore = new int[1]; // Almacena la mejor puntuación encontrada
        ArrayList<ArrayList<MovesTree>> bestPathsList = new ArrayList<>(); // Almacena la ruta para la mejor puntuación
        depthSearch(root, new ArrayList<>(), maxScore, bestPathsList);
        ArrayList<MovesTree> bestPath = orderPaths(bestPathsList);
        printResult(bestPath);
    }

    /**
     * Performs a depth-first search on the moves tree to store all the best paths.
     *
     * @param node           The current node being visited.
     * @param currentPath    The current path being traversed.
     * @param maxScore       The maximum score found so far.
     * @param bestPathsList  The list of best paths.
     */
    public static void depthSearch(MovesTree node, ArrayList<MovesTree> currentPath, int[] maxScore, ArrayList<ArrayList<MovesTree>> bestPathsList) {
        if (node == null) {
            return;
        }
        int currentScore = 0;
        if (node.getData() != null) { // We want to evade the root node, without data
            currentScore = node.getTotalScore(); // Use the stored score of the node
        }
        currentPath.add(node);

        // Check if it's a new best path
        if (currentScore > maxScore[0]) {
            maxScore[0] = currentScore;
            bestPathsList.clear();
            bestPathsList.add(new ArrayList<>(currentPath));
        } else if (currentScore == maxScore[0]) {
            bestPathsList.add(new ArrayList<>(currentPath));
        }

        if (node.getChilds() != null) {
            for (MovesTree child : node.getChilds()) {
                depthSearch(child, new ArrayList<>(currentPath), maxScore, bestPathsList);
            }
        }
        currentPath.remove(currentPath.size() - 1);
    }

    /**
     * Orders the best paths lexicographically and returns the lexicographically first path.
     *
     * @param bestPathsList The list of best paths.
     * @return The lexicographically first path.
     */
    public static ArrayList<MovesTree> orderPaths(ArrayList<ArrayList<MovesTree>> bestPathsList) {
        if (bestPathsList.isEmpty()) {
            return null;
        }
        ArrayList<MovesTree> lexicographicallyFirst = bestPathsList.get(0);
        for (int i = 1; i < bestPathsList.size(); i++) {
            ArrayList<MovesTree> currentPath = bestPathsList.get(i);
            if (comparePaths(currentPath, lexicographicallyFirst)) {
                lexicographicallyFirst = currentPath;
            }
        }
        return lexicographicallyFirst;
    }

    /**
     * Compares two paths lexicographically.
     *
     * @param path1 The first path.
     * @param path2 The second path.
     * @return True if path1 is lexicographically smaller than path2, false otherwise.
     */
    public static boolean comparePaths(ArrayList<MovesTree> path1, ArrayList<MovesTree> path2) {
        for (int i = 1; i < Math.min(path1.size(), path2.size()); i++) {
            int compare = compareMoves(path1.get(i), path2.get(i));
            if (compare != 0) {
                return compare < 0;
            }
        }
        return path1.size() < path2.size();
    }

    /**
     * Compares two moves lexicographically.
     *
     * @param move1 The first move.
     * @param move2 The second move.
     * @return -1 if move1 is lexicographically smaller than move2, 1 if move1 is lexicographically greater than move2, 0 if they are equal.
     */
    public static int compareMoves(MovesTree move1, MovesTree move2) {
        Result data1 = move1.getData();
        Result data2 = move2.getData();
        int compareX = Integer.compare(data1.getXPosition(), data2.getXPosition());
        if (compareX != 0) {
            return compareX;
        }
        int compareY = Integer.compare(data1.getYPosition(), data2.getYPosition());
        if (compareY != 0) {
            return compareY;
        }
        return Integer.compare(data1.getPoints(), data2.getPoints());
    }


    /**
     * Prints the result of the game as the format specified in the problem statement.
     *
     * @param path The path of the best moves.
     */
    public static void printResult(ArrayList<MovesTree> path) {
        System.out.println("Juego "+actualGame+":");        
        int finalScore = 0;
        Iterator<MovesTree> pathIterator = path.iterator();
        pathIterator.next();
        int movimiento = 1;
        while(pathIterator.hasNext()){
            Result data = pathIterator.next().getData();
            if(data.getPoints() == 1){
                System.out.println("Movimiento "+(movimiento)+" en ("+data.getXPosition()+", "+data.getYPosition()+"): eliminó "+data.getGroupLength()+" fichas de color "+data.getGroupColor()+" y obtuvo "+data.getPoints()+" punto.");  
            }else{
                System.out.println("Movimiento "+(movimiento)+" en ("+data.getXPosition()+", "+data.getYPosition()+"): eliminó "+data.getGroupLength()+" fichas de color "+data.getGroupColor()+" y obtuvo "+data.getPoints()+" puntos.");  
            }
            finalScore += data.getPoints();
            movimiento++;
        }
        MovesTree bestResult = null;
        for (MovesTree nodo : path) {
            bestResult = nodo; // Actualiza el tablero en cada iteración
        }
        int remainingTokens = bestResult.getRemainingTokens(bestResult.getBoard());
        if(remainingTokens == 0){
            finalScore = finalScore + 1000;
        }
        if(remainingTokens == 1){
            System.out.println("Puntuación final: "+finalScore+", quedando "+remainingTokens+" ficha.");
        }else{
            System.out.println("Puntuación final: "+finalScore+", quedando "+remainingTokens+" fichas.");            
        }
        if(actualGame != games)
            System.out.print("\n");
    }
}   
