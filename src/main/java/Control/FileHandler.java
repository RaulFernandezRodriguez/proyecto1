package Control;

import Model.Token;
import Model.Main;

import javax.swing.*;
import java.io.*;
import java.util.Scanner;

public class FileHandler {
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

    public static void writeResultToFile(String gameResult) {
        File file = new File("moves.txt");
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print(gameResult);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

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

    // public static void loadGameFromFile(JPanel boardPanel, File file, String[] colors) {
    //     try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
    //         String line = reader.readLine();
    //         int rows = line.length();
    //         int cols = line.length();
    
    //         boardPanel.removeAll();
    //         boardPanel.setLayout(new GridLayout(rows, cols));
    
    //         for (char color : line.toCharArray()) {
    //             JComboBox<String> cell = new JComboBox<>(colors);
    //             cell.setSelectedItem(String.valueOf(color));
    //             boardPanel.add(cell);
    //         }
    //     } catch (IOException e) {
    //         JOptionPane.showMessageDialog(null, "Error loading game: " + e.getMessage());
    //     }
    // }
}