package Control;

import javax.swing.*;
import java.io.*;
import java.awt.*;

public class FileHandler {
    public static void saveGameToFile(JPanel boardPanel, File file) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            Component[] components = boardPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JComboBox) {
                    JComboBox<String> cell = (JComboBox<String>) component;
                    String color = (String) cell.getSelectedItem();
                    writer.print(color.charAt(0));
                }
            }
            writer.println();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving game: " + e.getMessage());
        }
    }
    
    public static void loadGameFromFile(JPanel boardPanel, File file, String[] colors) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            int rows = line.length();
            int cols = line.length();
    
            boardPanel.removeAll();
            boardPanel.setLayout(new GridLayout(rows, cols));
    
            for (char color : line.toCharArray()) {
                JComboBox<String> cell = new JComboBox<>(colors);
                cell.setSelectedItem(String.valueOf(color));
                boardPanel.add(cell);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading game: " + e.getMessage());
        }
    }
}