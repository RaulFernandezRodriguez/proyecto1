package View;
import javax.swing.*;
import javax.swing.undo.UndoManager;

import Model.GenerateMoves;
import Model.MovesTree;
import Model.SearchBestRoute;
import Model.Token;
import Model.Result;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;

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
    private JButton[][] boardButtons; // Modified line
    private static JTextArea infoArea;
    public static int movimiento = 1;

    private GameState gameState = GameState.SETTING_UP;


    public enum GameState {
        SETTING_UP,
        PLAYING
    }

    public GameGUI() {
        frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        //boardPanel = new JPanel();
        //boardPanel.setLayout(new GridLayout(rows, cols)); // Modified line

        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        mainMenu = new JMenu("Main Menu");
        menuBar.add(mainMenu);

        newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                play();
            }
        });
        mainMenu.add(newGameItem);

        // // Initialize the boardButtons array.
        // boardButtons = new JButton[rows][cols];

        // // Create a button for each cell in the grid.
        // for (int i = 0; i < rows; i++) {
        //     for (int j = 0; j < cols; j++) {
        //         boardButtons[i][j] = new JButton();
        //         boardButtons[i][j].addActionListener(new ActionListener() {
        //             public void actionPerformed(ActionEvent e) {
        //                 // Handle the button click.
        //                 TokenButton.handleButtonClick(getCurrentBoard(), rows, cols);
        //             }
        //         });
        //         boardPanel.add(boardButtons[i][j]);
        //     }
        // }
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

        infoArea = new JTextArea();
        infoArea.setEditable(false); 
        frame.add(new JScrollPane(infoArea), BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GameGUI();
            }
        });
    }

    private void play() {
        String rowsInput = JOptionPane.showInputDialog(frame, "Enter number of rows:");
        String colsInput = JOptionPane.showInputDialog(frame, "Enter number of columns:");
        int rows = Integer.parseInt(rowsInput);
        int cols = Integer.parseInt(colsInput);

        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(rows, cols));

        boardButtons = new JButton[rows][cols]; // Added line

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boardButtons[i][j] = new JButton(); // Added line
                final int row = i;
                final int col = j;
                boardButtons[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (gameState == GameState.SETTING_UP) {
                            // Ask for the color and set it
                            Color chosenColor = TokenButton.colorChooser(frame);
                            boardButtons[row][col].setBackground(chosenColor);
                        } else if (gameState == GameState.PLAYING) {
                            // Handle the button click.
                            Token[][] newBoard = TokenButton.handleButtonClick(getCurrentBoard(), row, col);
                            if(newBoard != null)
                                updateBoard(newBoard);
                        }
                    }
                });
                boardPanel.add(boardButtons[i][j]);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();

        findSolutionItem = new JMenuItem("Find Solution");
        findSolutionItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        // Llama al método findSolution en un hilo de trabajador en segundo plano
                        SolutionFinder.findSolution(getCurrentBoard());
                        return null;
                    }
                };
        
                worker.execute();
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

        findSolutionItem.setEnabled(false);
        undoItem.setEnabled(false);
        redoItem.setEnabled(false);

        JButton startPlayingButton = new JButton("Start Playing");
        startPlayingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Check if the board is fully complete
                if(checkBoard(rows, cols)){
                    gameState = GameState.PLAYING;
                    // If the board is fully complete, change the game state to PLAYING
                    findSolutionItem.setEnabled(true);
                    undoItem.setEnabled(true);
                    redoItem.setEnabled(true);
                }   
            }
        });
        // Add the startPlayingButton to the boardPanel
        boardPanel.add(startPlayingButton);
    }

    public Token[][] getCurrentBoard() {
        int rows = boardButtons.length;
        int cols = boardButtons[0].length;
        Token[][] currentBoard = new Token[rows][cols];
    
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Get the color of the button
                Color buttonColor = boardButtons[i][j].getBackground();
                // Create a new Token with the color of the button
                currentBoard[i][j] = new Token(TokenButton.getCharColor(buttonColor), i, j);
            }
        }
        return currentBoard;
    }

    // public void actionPerformed(ActionEvent e) {
    //     JButton clickedButton = (JButton) e.getSource();
    //     // Handle the button click.
    // }

    public void updateBoard(Token[][] fixedBoard) {
        // Remove all existing buttons from the boardPanel
        boardPanel.removeAll();
        // Create new buttons that reflect the current state of the fixedBoard
        for (int i = 0; i < fixedBoard.length; i++) {
            for (int j = 0; j < fixedBoard[i].length; j++) {
                JButton button = new JButton();
                button.setBackground(TokenButton.getVisualColor(fixedBoard[i][j].getColor()));
                final int row = i;
                final int col = j;
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // Handle the button click.
                        Token[][] newBoard = TokenButton.handleButtonClick(fixedBoard, row, col);
                        if(newBoard != null)
                            updateBoard(newBoard);
                    }
                });
                // Add the button to the boardPanel
                boardPanel.add(button);
            }
        }
        // Revalidate and repaint the boardPanel to reflect the new buttons
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    public boolean checkBoard(int rows, int cols){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (boardButtons[i][j].getBackground() == null) {
                    JOptionPane.showMessageDialog(frame, "The board is not fully complete.");
                    return false;
                }
            }
        }
        return true;
    }

    public static void showResult(Result data){
        if(data.getPoints() == 1){
            infoArea.append("Movimiento "+(movimiento)+" en ("+data.getXPosition()+", "+data.getYPosition()+"): eliminó "+data.getGroupLength()+" fichas de color "+data.getGroupColor()+" y obtuvo "+data.getPoints()+" punto.");  
        }else{
            infoArea.append("Movimiento "+(movimiento)+" en ("+data.getXPosition()+", "+data.getYPosition()+"): eliminó "+data.getGroupLength()+" fichas de color "+data.getGroupColor()+" y obtuvo "+data.getPoints()+" puntos.");  
        }
        movimiento++;
    }
}
