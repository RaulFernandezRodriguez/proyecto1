package View;

import javax.swing.*;
import java.io.*;
import java.awt.*;

public class GameFileHandler {

    public static void saveGameToFile(Component[] components, JFrame frame) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (Component component : boardPanel.getComponents()) {
                    if (component instanceof JComboBox) {
                        JComboBox<String> cell = (JComboBox<String>) component;
                        String color = (String) cell.getSelectedItem();
                        writer.print(color.charAt(0));
                    }
                }
                writer.println();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error saving game: " + e.getMessage());
            }
        }
    }

    public static void loadGameFromFile(JPanel boardPanel, JFrame frame, String[] colors) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
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
                boardPanel.revalidate();
                boardPanel.repaint();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error loading game: " + e.getMessage());
            }
        }
    }
}