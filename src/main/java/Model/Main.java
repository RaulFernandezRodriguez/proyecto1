package Model;

import java.util.Scanner;

import View.GameGUI;

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
        // Ejecuta la interfaz gráfica en el hilo de despacho de eventos
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GameGUI();  // Reemplaza GameGUI con el nombre de tu clase de interfaz gráfica
            }
        });
        Scanner scanner = new Scanner(System.in);
        games = Integer.parseInt(scanner.nextLine());
        if (games <= 0) {
            //System.out.println("Error, numero de juegos invalido");
        } else {
            scanner.nextLine(); // Consume the newline character after the number of games.
            actualGame = 1;
            while (actualGame <= games) {
                String gameInput = storeGame(scanner);;
                if(gameInput.isBlank())
                    break;
                Token[][] board = gameBoard(gameInput);
                if (board != null) {
                    play(board);
                } else {
                    //System.out.println("Error en el juego " + actualGame);
                    break;
                }
                actualGame++;
                // Verificar si hay más líneas después del juego actual
                if (!scanner.hasNext()) {
                    break;
                }
            }
        }
        scanner.close();
    }


    //Cambie estos 2 metodos de private a public
    /**
     * Reads the input of a game,scores it in a StringBuilder and returns it as a string.
     * The input of a game ends when a blank line is found.
     * 
     * @param scanner The scanner object used to read the input.
     * @return The input of one game as a string.
     */
    public static String storeGame(Scanner scanner) {
        StringBuilder gameInput = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                break; // Fin del juego actual.
            }
            gameInput.append(line).append("\n");
        }
        return gameInput.toString().trim(); // Eliminar el último carácter '\n'.
    }

    /**
     * Converts the game input string into a 2D static array of Token objects representing the game board.
     * The method wont create the game board if it identifies any issue with the input (like different token 
     * colors or row lengths).
     * 
     * @param gameInput The input of the game as a string.
     * @return The game board as a 2D static array of Token objects, or null if the input is invalid in some way.
     */
    public static Token[][] gameBoard(String gameInput) {
        String[] rows = gameInput.split("\n");
        int numRows = rows.length;
        int numCols = rows[0].length();
        if (numCols > 20 || numRows > 20) {
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
        GenerateMoves.searchMoves(board, original);
        ArrayList<MovesTree> bestPath= SearchBestRoute.searchBestMoves(original);
        printResult(bestPath);
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
