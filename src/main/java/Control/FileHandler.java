package Control;

import Model.Token;
import Model.Main;

import javax.swing.*;
import java.io.*;
import java.util.Scanner;

/**
 * The FileHandler class provides methods for saving and loading game data to/from files.
 * Follows the desing pattern stablished in the first assignment.
 */
public class FileHandler {
    
    /**
     * Saves the game board to a file.
     * 
     * @param board the game board represented by a 2D array of tokens
     * @param file the file to save the game to
     */
    public static void saveGameToFile(Token[][] board, File file) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    writer.print(board[i][j].getColor());
                }
                writer.println();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving game: " + e.getMessage());
        }
    }

    /**
     * Writes the game result to a file.
     * 
     * @param gameResult the result of the game
     */
    public static void writeResultToFile(String gameResult) {
        File file = new File("moves.txt");
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(gameResult);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the game board from a file.
     * 
     * @param file the file to load the game from
     * @return the loaded game board represented by a 2D array of tokens
     */
    public static Token[][] loadGameFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Scanner scanner = new Scanner(reader);
            String gameInput = Main.storeGame(scanner);
            return Main.gameBoard(gameInput);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading game: " + e.getMessage());
            return null;
        }
    }
}