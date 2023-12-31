package View;
import javax.swing.*;
import javax.swing.undo.UndoManager;

import Control.BoardStatus;
import Control.ButtonControl;
import Control.FileHandler;
import Control.SolutionFinder;
import Model.Token;
import Model.Result;
import Model.GenerateMoves;
import Model.MovesTree;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class GameGUI {
    private JFrame frame;
    static private JPanel boardPanel;
    private JMenu mainMenu;
    private JMenuBar menuBar;
    private JPanel buttonPanel;
    private JButton newGameButton;
    private String[] colors = {"Red", "Green", "Blue"};
    private JButton saveGameButton;
    private JButton loadGameButton;
    private JButton findSolutionItem;
    private JMenu editMenu;
    private JButton undoItem;
    private JButton redoItem;
    private static JButton[][] boardButtons; // Modified line
    private static JTextArea infoArea;
    public static int movimiento = 1;
    private static boolean isFirstGame = true;
    public static int currentScore;

    private static JTextField movesField;
    private static JTextField scoreField;
    private static JTextField tokensField;

    private GameState gameState = GameState.SETTING_UP;


    public enum GameState {
        MENU,
        SETTING_UP,
        PLAYING,
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GameGUI();
            }
        });
    }

    public GameGUI() {
        gameState = GameState.MENU;
        frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);

        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        mainMenu = new JMenu("Main Menu");
        menuBar.add(mainMenu);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        frame.add(buttonPanel, BorderLayout.NORTH);

        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                play();
            }
        });
        buttonPanel.add(newGameButton);

        frame.setJMenuBar(menuBar);
        frame.setVisible(true);

        saveGameButton = new JButton("Save Game");
        saveGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION && checkBoard(getCurrentBoard())) {
                    File file = new File("game.txt");
                    FileHandler.saveGameToFile(getCurrentBoard(), file);
                }
            }
        });
        buttonPanel.add(saveGameButton);

        loadGameButton = new JButton("Load Game");
        loadGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    Token[][] board = FileHandler.loadGameFromFile(file);
                    updateBoard(board);
                }   
            }
        });
        buttonPanel.add(loadGameButton);
        
        findSolutionItem = new JButton("Find Solution");
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
        buttonPanel.add(findSolutionItem);

        editMenu = new JMenu("Edit");
        menuBar.add(editMenu);
    
        undoItem = new JButton("Undo");
        undoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(gameState == GameState.SETTING_UP){
                    Token[][] newStatus = BoardStatus.undoChange();
                    updateBoard(newStatus);
                } else if(gameState == GameState.PLAYING){
                    BoardStatus newStatus = BoardStatus.undoMove();
                    updateBoard(newStatus.getBoard());
                    Result newData = newStatus.getScore();
                    tokensField.setText(String.valueOf(MovesTree.getRemainingTokens(newStatus.getBoard())));
                    scoreField.setText(String.valueOf(newData.getPoints()));
                    movimiento--;
                    tokensField.repaint();
                    scoreField.repaint();
                    movesField.repaint();
                } 
            }
        });
        buttonPanel.add(undoItem);

        redoItem = new JButton("Redo");
        redoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(gameState == GameState.SETTING_UP){
                    Token[][] newStatus = BoardStatus.redoChange();
                    updateBoard(newStatus);
                } else if(gameState == GameState.PLAYING){
                    BoardStatus newStatus = BoardStatus.redoMove();
                    updateBoard(newStatus.getBoard());
                    Result newData = newStatus.getScore();
                    tokensField.setText(String.valueOf(MovesTree.getRemainingTokens(newStatus.getBoard())));
                    scoreField.setText(String.valueOf(newData.getPoints()));
                    movimiento++;
                    tokensField.repaint();
                    scoreField.repaint();
                    movesField.repaint();
                }
            }
        });
        buttonPanel.add(redoItem);

        if(isFirstGame){
            JButton startPlayingButton = new JButton("Start Playing");
            startPlayingButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Check if the board is fully complete
                    if(checkBoard(getCurrentBoard())){
                        gameState = GameState.PLAYING;
                        saveGameButton.setEnabled(false);
                        BoardStatus.clearStacks();
                        // If the board is fully complete, change the game state to PLAYING
                        findSolutionItem.setEnabled(true);
                        undoItem.setEnabled(true);
                        redoItem.setEnabled(true);
                    }   
                }
            });
            // Add the startPlayingButton to the boardPanel
            buttonPanel.add(startPlayingButton);
        }

        findSolutionItem.setEnabled(false);
        undoItem.setEnabled(false);
        redoItem.setEnabled(false);

        frame.add(buttonPanel, BorderLayout.NORTH);
    }

    private void play() {
        gameState = GameState.SETTING_UP;
        saveGameButton.setEnabled(true);
        
        String rowsInput = JOptionPane.showInputDialog(frame, "Enter number of rows:");
        String colsInput = JOptionPane.showInputDialog(frame, "Enter number of columns:");
        int rows = Integer.parseInt(rowsInput);
        int cols = Integer.parseInt(colsInput);
        

        if (!isFirstGame) {
            boardPanel.removeAll();
            boardPanel.revalidate();
            boardPanel.repaint();
        }

        boardPanel = new JPanel();
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
                            Color chosenColor = colorChooser(frame);
                            boardButtons[row][col].setBackground(chosenColor);
                            BoardStatus.makeChange(getCurrentBoard());
                        } else if (gameState == GameState.PLAYING) {
                            // Handle the button click.
                            ButtonControl.handleButtonClick(getCurrentBoard(), row, col);
                        }
                    }
                });
                boardPanel.add(boardButtons[i][j]);
            }
        }
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.validate();
        buttonPanel.revalidate();
        buttonPanel.repaint();

        // Create the panel for displaying the game stats
        JPanel gameStatsPanel = new JPanel(new GridLayout(3, 2));

        gameStatsPanel.add(new JLabel("Number of moves:"));
        movesField = new JTextField();
        movesField.setText(String.valueOf(movimiento));
        movesField.setEditable(false);
        gameStatsPanel.add(movesField);

        gameStatsPanel.add(new JLabel("Current total score:"));
        scoreField = new JTextField();
        scoreField.setEditable(false);
        gameStatsPanel.add(scoreField);

        gameStatsPanel.add(new JLabel("Remaining tokens on board:"));
        tokensField = new JTextField();
        tokensField.setEditable(false);
        gameStatsPanel.add(tokensField);

        infoArea = new JTextArea();
        infoArea.setEditable(false); 

        // Create a split pane and add the panels to it
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, infoArea, gameStatsPanel);
        splitPane.setDividerLocation(150);
        frame.add(splitPane, BorderLayout.SOUTH);
        currentScore = 0;
        scoreField.setText(String.valueOf(currentScore));
        movimiento = 1;
        movesField.setText(String.valueOf(movimiento));
        tokensField.setText(String.valueOf(rows * cols));
        splitPane.validate();
        splitPane.repaint();

        frame.validate();
        frame.repaint();

        isFirstGame = false;
    }

    public static Token[][] getCurrentBoard() {
        int rows = boardButtons.length;
        int cols = boardButtons[0].length;
        Token[][] currentBoard = new Token[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Get the color of the button
                Color buttonColor = boardButtons[i][j].getBackground();
                // Create a new Token with the color of the button
                currentBoard[i][j] = new Token(getCharColor(buttonColor), i, j);
            }
        }
        return currentBoard;
    }

    public static void updateBoard(Token[][] fixedBoard) {
        int rows = fixedBoard.length;
        int cols = fixedBoard[0].length;
        if (isFirstGame) {
            boardPanel = new JPanel();
            boardPanel.setLayout(new GridLayout(rows, cols));
            boardButtons = new JButton[rows][cols]; // Added line
        } else{
            boardPanel.removeAll();
            boardPanel.revalidate();
            boardPanel.repaint();
        }
        // Remove all existing buttons from the boardPanel
        //boardPanel.removeAll();
        // Create new buttons that reflect the current state of the fixedBoard
        for (int i = 0; i < fixedBoard.length; i++) {
            for (int j = 0; j < fixedBoard[i].length; j++) {
                JButton button = new JButton();
                button.setBackground(getVisualColor(fixedBoard[i][j].getColor()));
                final int row = i;
                final int col = j;
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // Handle the button click.
                        ButtonControl.handleButtonClick(fixedBoard, row, col);
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

    public void playboard(Token[][] board){
        // Set the layout of the boardPanel to match the size of the board
        boardPanel.setLayout(new GridLayout(board.length, board[0].length));

        // Remove all existing buttons from the boardPanel
        boardPanel.removeAll();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                JButton button = new JButton();
                button.setBackground(getVisualColor(board[i][j].getColor()));
                final int row = i;
                final int col = j;
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e){
                        if (gameState == GameState.SETTING_UP) {
                            // Ask for the color and set it
                            Color chosenColor = colorChooser(frame);
                            boardButtons[row][col].setBackground(chosenColor);
                            BoardStatus.makeChange(getCurrentBoard());
                        } else if (gameState == GameState.PLAYING) {
                            // Handle the button click.
                            ButtonControl.handleButtonClick(getCurrentBoard(), row, col);
                        }
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

    public boolean checkBoard(Token[][] board){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j].getColor() == '_') {
                    JOptionPane.showMessageDialog(frame, "The board is not fully complete.");
                    return false;
                }
            }
        }
        return true;
    }

    public static void showResult(Result data){
        if(data.getPoints() == 1){
            infoArea.append("Movimiento "+(movimiento)+" en ("+data.getXPosition()+", "+data.getYPosition()+"): eliminó "+data.getGroupLength()+" fichas de color "+data.getGroupColor()+" y obtuvo "+data.getPoints()+" punto.\n");  
        }else{
            infoArea.append("Movimiento "+(movimiento)+" en ("+data.getXPosition()+", "+data.getYPosition()+"): eliminó "+data.getGroupLength()+" fichas de color "+data.getGroupColor()+" y obtuvo "+data.getPoints()+" puntos.\n");  
        }
        if(checkEnd()){
            int remainingTokens = Integer.parseInt(tokensField.getText());
            int finalScore = Integer.parseInt(scoreField.getText());
            if(remainingTokens == 1){
                infoArea.append("Puntuación final: "+finalScore+", quedando "+remainingTokens+" ficha.");
            }else{
                infoArea.append("Puntuación final: "+finalScore+", quedando "+remainingTokens+" fichas.");            
            }
            int option = JOptionPane.showConfirmDialog(null, "¿Quieres guardar el resultado del juego en un archivo de texto?", "Guardar resultado", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                // El jugador quiere guardar el resultado del juego.
                FileHandler.writeMovesToFile(infoArea.getText());
            } 
        }
    }

    public static boolean checkEnd(){
        Token[][] board = getCurrentBoard();
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
        Iterator<LinkedList<Token>> groupIterator = groups.iterator();
        while (groupIterator.hasNext()) {
            LinkedList<Token> currentGroup = groupIterator.next();
            int groupLength = currentGroup.size();
            if (groupLength >= 2) {
                return false;
            }
        }
        return true;
    }

    public static Color getVisualColor(char boardColor){
        if(boardColor == 'R'){
            return Color.RED;
        }else if(boardColor == 'V'){
            return Color.GREEN;
        }else if(boardColor == 'A'){
            return Color.BLUE;
        }else {
            return Color.WHITE;

        }
    }

    public static char getCharColor(Color boardColor){
        if(boardColor == Color.RED){
            return 'R';
        }else if(boardColor == Color.GREEN){
            return 'V';
        }else if(boardColor == Color.BLUE){
            return 'A';
        }else {
            return '_';
        }
    }

    public static Color colorChooser(JFrame frame){
        Object[] options = {"Blue", "Red", "Green", "Blank"};
        int n = JOptionPane.showOptionDialog(frame,
            "Choose a color for this button:",
            "Choose Color",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

        switch (n) {
            case 0: return Color.BLUE;
            case 1: return Color.RED;
            case 2: return Color.GREEN;
            case 3: return Color.WHITE;
            default: return null; // This will be returned if the user closes the dialog without choosing an option
        }
    }
    
    public static void updateScoreField(int score) {
        currentScore += score; // Suma la puntuación del movimiento a `currentScore`
        scoreField.setText(String.valueOf(currentScore)); // Actualiza `scoreField` con la puntuación total
    }
    
    public static void updateTokensField(int remainingTokens) {
        tokensField.setText(String.valueOf(remainingTokens));
    }

    public static void updateMovesField() {
        movimiento++;
        movesField.setText(String.valueOf(movimiento));
    }
}
