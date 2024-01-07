package View;

import Control.BoardStatus;
import Control.ButtonControl;
import Control.FileHandler;
import Control.SolutionFinder;
import Model.Token;
import Model.Result;
import Model.MovesTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * GameGUI is the main class for the GUI of the game.
 * It sets up the game state and initializes the game frame.
 */
public class GameGUI {
    private static JFrame frame;
    static private JPanel boardPanel;
    private JMenu mainMenu;
    private JMenu editMenu;
    private JMenuBar menuBar;
    private static JPanel buttonPanel;
    private JMenuItem newGameButton;
    private JMenuItem saveGameButton;
    private JMenuItem loadGameButton;
    private JMenuItem findSolutionItem;
    private JMenuItem undoItem;
    private JMenuItem redoItem;
    private JMenuItem endGameButton;
    private JMenuItem startPlayingButton;
    private static JButton[][] boardButtons; // Modified line
    private static JTextArea infoArea;
    public static int movimiento = 1;
    public static int currentScore;

    private static JTextField movesField;
    private static JTextField scoreField;
    private static JTextField tokensField;

    private static GameState gameState = GameState.SETTING_UP;

     /**
     * Enum for the different states of the game.
     */
    public enum GameState {
        MENU,
        SETTING_UP,
        PLAYING,
    }

    /**
     * The main method that starts the game GUI.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GameGUI();
            }
        });
    }

    /**
     * Constructor for the GameGUI class.
     * It sets the initial game state to MENU and initializes the game frame.
     */
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

        newGameButton = new JMenuItem("New Game");
        newGameButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                play();
            }
        });
        mainMenu.add(newGameButton);

        frame.setJMenuBar(menuBar);
        frame.setVisible(true);

        saveGameButton = new JMenuItem("Save Game");
        saveGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION && checkBoard(getCurrentBoard())) {
                    File file = fileChooser.getSelectedFile();
                    FileHandler.saveGameToFile(getCurrentBoard(), file);
                }
            }
        });
        mainMenu.add(saveGameButton);
        saveGameButton.setEnabled(false);

        loadGameButton = new JMenuItem("Load Game");
        loadGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    Token[][] board = FileHandler.loadGameFromFile(file);
                    playboard(board);
                }   
            }
        });
        mainMenu.add(loadGameButton);
        
        findSolutionItem = new JMenuItem("Find Solution");
        findSolutionItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SolutionFinder worker = new SolutionFinder(getCurrentBoard());
                worker.execute();
            }
        });        
        mainMenu.add(findSolutionItem);
        findSolutionItem.setEnabled(false);

        editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        undoItem = new JMenuItem("Undo");
        undoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(gameState == GameState.SETTING_UP){
                    Token[][] newStatus = BoardStatus.undoChange(getCurrentBoard());
                    if(newStatus != null)
                        updateBoard(newStatus);
                } else if(gameState == GameState.PLAYING){
                    BoardStatus currentStatus = new BoardStatus(getCurrentBoard(), infoArea.getText(), Integer.parseInt(scoreField.getText()), Integer.parseInt(movesField.getText()));
                    BoardStatus newStatus = BoardStatus.undoMove(currentStatus);
                    if(newStatus.getBoard() != null)
                        updateBoard(newStatus.getBoard());
                        boardPanel.repaint();
                        frame.repaint();
                    if(newStatus.getDataTrack() != null){
                        infoArea.setText(newStatus.getDataTrack());
                        infoArea.append("\n");
                        tokensField.setText(String.valueOf(MovesTree.getRemainingTokens(newStatus.getBoard())));
                        scoreField.setText(String.valueOf(newStatus.getScore()));
                        movimiento = newStatus.getMoves();
                        movesField.setText(String.valueOf(--movimiento));
                        tokensField.repaint();
                        scoreField.repaint();
                        movesField.repaint();
                    }
                } 
                boardPanel.repaint();
                boardPanel.revalidate();
                frame.repaint();
                frame.revalidate();
            }
        });
        editMenu.add(undoItem);        
        undoItem.setEnabled(false);

        redoItem = new JMenuItem("Redo");
        redoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(gameState == GameState.SETTING_UP){
                    Token[][] newStatus = BoardStatus.redoChange();
                    if(newStatus != null)
                        updateBoard(newStatus);
                } else if(gameState == GameState.PLAYING){
                    BoardStatus newStatus = BoardStatus.redoMove();
                    if(newStatus != null){
                        if(newStatus.getBoard() != null)
                            updateBoard(newStatus.getBoard());
                            boardPanel.repaint();
                            frame.repaint();
                        if(newStatus.getDataTrack() != null){
                            infoArea.setText(newStatus.getDataTrack());
                            tokensField.setText(String.valueOf(MovesTree.getRemainingTokens(newStatus.getBoard())));
                            scoreField.setText(String.valueOf(newStatus.getScore()));
                            movimiento = newStatus.getMoves();
                            movesField.setText(String.valueOf(movimiento)); 
                            movesField.repaint();
                            tokensField.repaint();
                            scoreField.repaint();
                        }
                    }
                }
                boardPanel.repaint();
                boardPanel.revalidate();
                frame.repaint();
                frame.revalidate();
            }
        });
        editMenu.add(redoItem);
        redoItem.setEnabled(false);

        endGameButton = new JMenuItem("End Game");
        endGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                endGame();
            }
        });
        mainMenu.add(endGameButton);
        endGameButton.setEnabled(false);

        startPlayingButton = new JMenuItem("Start Playing");
        startPlayingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Check if the board is fully complete
                if(checkBoard(getCurrentBoard())){
                    // Check if the board is even playable
                    ButtonControl.checkEnd(getCurrentBoard());
                    gameState = GameState.PLAYING;
                    saveGameButton.setEnabled(false);
                    BoardStatus.clearStacks();
                    storeMove();
                    // If the board is fully complete, change the game state to PLAYING
                    findSolutionItem.setEnabled(true);
                    endGameButton.setEnabled(true);
                    startPlayingButton.setEnabled(false);
                }   
            }
        });
        // Add the startPlayingButton to the boardPanel
        mainMenu.add(startPlayingButton);
        startPlayingButton.setEnabled(false);

        frame.add(buttonPanel, BorderLayout.NORTH);

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
        splitPane.setDividerLocation(550);
        frame.add(splitPane, BorderLayout.SOUTH);
        currentScore = 0;
        scoreField.setText(String.valueOf(currentScore));
        movimiento = 0;
        movesField.setText(String.valueOf(movimiento));
        splitPane.validate();
        splitPane.repaint();

        boardPanel = new JPanel();
        frame.add(boardPanel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();
    }

    /**
     * This method is used to start the game.
     * It sets the game state to SETTING_UP, enables the game control buttons, 
     * clears the undo/redo stacks, and prompts the user to enter the number of rows.
     */
    private void play() {
        gameState = GameState.SETTING_UP;
        saveGameButton.setEnabled(true);
        startPlayingButton.setEnabled(true);
        findSolutionItem.setEnabled(false);
        endGameButton.setEnabled(false);
        BoardStatus.clearStacks();

        undoItem.setEnabled(true);
        redoItem.setEnabled(true);
        
        String rowsInput = JOptionPane.showInputDialog(frame, "Enter number of rows:");
        String colsInput = JOptionPane.showInputDialog(frame, "Enter number of columns:");
        int rows = Integer.parseInt(rowsInput);
        int cols = Integer.parseInt(colsInput);

        currentScore = 0;
        scoreField.setText(String.valueOf(currentScore));
        movimiento = 1;
        movesField.setText(String.valueOf(movimiento));
        tokensField.setText(String.valueOf(rows * cols));
        infoArea.setText("");
        

        boardPanel.removeAll();
        //boardPanel = new JPanel();
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
                            BoardStatus.makeChange(getCurrentBoard());
                            Color chosenColor = colorChooser(frame);
                            boardButtons[row][col].setBackground(chosenColor);
                        } else if (gameState == GameState.PLAYING) {
                            // Handle the button click.
                            ButtonControl.handleButtonClick(getCurrentBoard(), row, col);
                            boardPanel.repaint();
                        }
                    }
                });
                boardPanel.add(boardButtons[i][j]);
            }
        }
        frame.add(boardPanel, BorderLayout.CENTER);
        buttonPanel.revalidate();
        buttonPanel.repaint();
        boardPanel.revalidate();
        boardPanel.repaint();

        frame.revalidate();
        frame.repaint();
        
    }

    /**
     * Play a game using a given board. Used for the load game functionality.
     * @param board
     */
    public void playboard(Token[][] board){
        gameState = GameState.SETTING_UP;
        saveGameButton.setEnabled(true);
        startPlayingButton.setEnabled(true);
        findSolutionItem.setEnabled(false);
        endGameButton.setEnabled(false);
        BoardStatus.clearStacks();

        undoItem.setEnabled(true);
        redoItem.setEnabled(true);

        int rows = board.length;
        int cols = board[0].length;

        currentScore = 0;
        scoreField.setText(String.valueOf(currentScore));
        movimiento = 1;
        movesField.setText(String.valueOf(movimiento));
        tokensField.setText(String.valueOf(rows * cols));
        infoArea.setText("");

        boardPanel.removeAll();
        //boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(rows, cols));

        boardButtons = new JButton[rows][cols]; 
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boardButtons[i][j] = new JButton();
                boardButtons[i][j].setBackground(getVisualColor(board[i][j].getColor())); // Added line
                final int row = i;
                final int col = j;
                boardButtons[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (gameState == GameState.SETTING_UP) {
                            BoardStatus.makeChange(getCurrentBoard());
                            // Ask for the color and set it
                            Color chosenColor = colorChooser(frame);
                            boardButtons[row][col].setBackground(chosenColor);
                        } else if (gameState == GameState.PLAYING) {
                            ButtonControl.handleButtonClick(getCurrentBoard(), row, col);
                            boardPanel.repaint();
                        }
                    }
                });
                boardPanel.add(boardButtons[i][j]);
            }
        }
        frame.add(boardPanel, BorderLayout.CENTER);
        buttonPanel.revalidate();
        buttonPanel.repaint();
        boardPanel.revalidate();
        boardPanel.repaint();

        frame.revalidate();
        frame.repaint();

    }

    /**
     * Returns the current game board.
     */
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

    /**
     * Updates the game board.
     * @param fixedBoard the new game board
     */
    public static void updateBoard(Token[][] fixedBoard) {
        int rows = fixedBoard.length;
        int cols = fixedBoard[0].length;
        boardPanel.removeAll();

        //boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(rows, cols));

        boardButtons = new JButton[rows][cols]; // Added line

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boardButtons[i][j] = new JButton(); // Added line
                final int row = i;
                final int col = j;
                boardButtons[i][j].setBackground(getVisualColor(fixedBoard[i][j].getColor()));
                boardButtons[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (gameState == GameState.SETTING_UP) {
                            BoardStatus.makeChange(getCurrentBoard());
                            // Ask for the color and set it
                            Color chosenColor = colorChooser(frame);
                            boardButtons[row][col].setBackground(chosenColor);
                        } else if (gameState == GameState.PLAYING) {
                            ButtonControl.handleButtonClick(getCurrentBoard(), row, col);
                            boardPanel.repaint();
                        }
                    }
                });
                boardPanel.add(boardButtons[i][j]);
            }
        }
        frame.add(boardPanel, BorderLayout.CENTER);

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    /**
     * Checks if the board is fully complete.
     * @param board the game board
     * @return true if the board is fully complete, false otherwise
     */
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

    /**
     * Displays the result of a move.
     * @param data the result of the move
     */
    public static void showResult(Result data){
        if(data.getPoints() == 1){
            infoArea.append("Movimiento "+(movimiento)+" en ("+data.getXPosition()+", "+data.getYPosition()+"): eliminó "+data.getGroupLength()+" fichas de color "+data.getGroupColor()+" y obtuvo "+data.getPoints()+" punto.\n");  
        }else{
            infoArea.append("Movimiento "+(movimiento)+" en ("+data.getXPosition()+", "+data.getYPosition()+"): eliminó "+data.getGroupLength()+" fichas de color "+data.getGroupColor()+" y obtuvo "+data.getPoints()+" puntos.\n");  
        }  
        infoArea.revalidate();
        infoArea.repaint();
    }

    /**
     * Checks if the game has ended.
     * @param board the game board
     */
    public static void endGame(){
        int remainingTokens = Integer.parseInt(tokensField.getText());
        int finalScore = Integer.parseInt(scoreField.getText());
        if(remainingTokens == 0){
            finalScore += 1000;
        }
        if(remainingTokens == 1){
            infoArea.append("Puntuación final: "+finalScore+", quedando "+remainingTokens+" ficha.\n");
        }else{
            infoArea.append("Puntuación final: "+finalScore+", quedando "+remainingTokens+" fichas.\n");            
        }
        infoArea.revalidate();
        infoArea.repaint();
        int option = JOptionPane.showConfirmDialog(null, "¿Quieres guardar el resultado del juego en un archivo de texto?", "Guardar resultado", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            // El jugador quiere guardar el resultado del juego.
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                FileHandler.writeResultToFile(infoArea.getText(), fileToSave);
            }
        }
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

    /**
     * Displays a color chooser dialog.
     * @param frame the frame to display the dialog in
     * @return the color chosen by the user
     */
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
        scoreField.setText(String.valueOf(Integer.parseInt(scoreField.getText()) + score)); // Actualiza `scoreField` con la puntuación total
        scoreField.repaint();
    }
    
    public static void updateTokensField(int remainingTokens) {
        tokensField.setText(String.valueOf(remainingTokens));
        tokensField.repaint();
    }

    public static void updateMovesField() {
        movimiento++;
        movesField.setText(String.valueOf(movimiento));
        movesField.repaint();
    }

    public static String getLastLine() {
        String text = infoArea.getText();
        String[] lines = text.split("\n");
        if(lines.length == 1){
            return " ";
        }
        return lines[lines.length - 1];
    }

    public static void storeMove(){
        BoardStatus previousBoard = new BoardStatus(getCurrentBoard(), getLastLine(), Integer.parseInt(scoreField.getText()), Integer.parseInt(movesField.getText()));
        BoardStatus.makeMove(previousBoard);
    }
}
