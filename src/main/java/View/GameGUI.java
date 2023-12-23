package View;
import javax.swing.*;
import javax.swing.undo.UndoManager;

import java.awt.*;
import java.awt.event.*;

public class GameGUI {
    private JFrame frame;
    private JPanel boardPanel;
    private JMenu mainMenu;
    private JMenuBar menuBar;
    private JMenuItem newGameItem;
    private String[] colors = {"Red", "Green", "Blue"};
    private JMenuItem saveGameItem;
    private JMenuItem loadGameItem;
    private JMenuItem findSolutionItem;
    private JMenu editMenu;
    private JMenuItem undoItem;
    private JMenuItem redoItem;
    private UndoManager undoManager;
    //private Game game;

    public GameGUI() {
        frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        boardPanel = new JPanel();

        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        mainMenu = new JMenu("Main Menu");
        menuBar.add(mainMenu);

        newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createNewGame();
            }
        });
        mainMenu.add(newGameItem);
       
       // menuBar.add(gameMenu);

        frame.setJMenuBar(menuBar);
        frame.add(boardPanel);
        frame.setVisible(true);

        saveGameItem = new JMenuItem("Save Game");
        saveGameItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GameFileHandler.saveGameToFile(boardPanel.getComponents(), frame);
            }
        });
        mainMenu.add(saveGameItem);

        loadGameItem = new JMenuItem("Load Game");
        loadGameItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GameFileHandler.loadGameFromFile(boardPanel, frame, colors);
            }
        });
        mainMenu.add(loadGameItem);
    }

    private void createNewGame() {
        String rowsInput = JOptionPane.showInputDialog(frame, "Enter number of rows:");
        String colsInput = JOptionPane.showInputDialog(frame, "Enter number of columns:");
        int rows = Integer.parseInt(rowsInput);
        int cols = Integer.parseInt(colsInput);

        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(rows, cols));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JComboBox<String> cell = new JComboBox<>(colors);
                boardPanel.add(cell);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();

        findSolutionItem = new JMenuItem("Find Solution");
        findSolutionItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findSolution();
            }
        });
        menuBar.add(findSolutionItem);

        undoManager = new UndoManager();

        editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        undoItem = new JMenuItem("Undo");
        undoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        });
        editMenu.add(undoItem);

        redoItem = new JMenuItem("Redo");
        redoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        });
        editMenu.add(redoItem);
    }

    private void findSolution() {
        SolutionFinder solutionFinder = new SolutionFinder(game);
        solutionFinder.execute();
        try {
            String solution = solutionFinder.get();
            JOptionPane.showMessageDialog(frame, "Optimal solution: " + solution);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error finding solution: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GameGUI();
            }
        });
    }

    //TO DO
    public void play() {
        List<Move> moves = MoveFinder.findMoves(this);
        Route bestRoute = RouteSearcher.findBestRoute(this);
        // ... existing code ...
    }
}