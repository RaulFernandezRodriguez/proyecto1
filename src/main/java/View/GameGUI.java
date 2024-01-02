package View;

import Control.BoardStatus;
import Control.ButtonControl;
import Control.FileHandler;
import Control.SolutionFinder;
import Model.Token;
import Model.Result;
import Model.GenerateMoves;
import Model.MovesTree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

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
    private static boolean isFirstGame = true;
    public static int currentScore;

    private static JTextField movesField;
    private static JTextField scoreField;
    private static JTextField tokensField;

    private static GameState gameState = GameState.SETTING_UP;

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
                    File file = new File("game.txt");
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
                    Token[][] newStatus = BoardStatus.undoChange();
                    if(newStatus != null)
                        updateBoard(newStatus);
                } else if(gameState == GameState.PLAYING){
                    BoardStatus newStatus = BoardStatus.undoMove();
                    if(newStatus.getBoard() != null)
                        updateBoard(newStatus.getBoard());
                        boardPanel.repaint();
                    if(newStatus.getDataTrack() != null){
                        String currentText = infoArea.getText();
                        String[] lines = currentText.split("\n");
                        StringBuilder newText = new StringBuilder();
                        if(lines.length == 1){
                            newText.append("");
                        } else{
                            for (int i = 0; i < lines.length - 1; i++) {
                                newText.append(lines[i]).append("\n");
                            }
                        }
                        infoArea.setText(newText.toString());
                        tokensField.setText(String.valueOf(MovesTree.getRemainingTokens(newStatus.getBoard())));
                        scoreField.setText(String.valueOf(newStatus.getScore()));
                        movimiento = newStatus.getMoves();
                        movesField.setText(String.valueOf(movimiento));
                        tokensField.repaint();
                        scoreField.repaint();
                        movesField.repaint();
                    }
                } 
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
                    if(newStatus.getBoard() != null)
                        updateBoard(newStatus.getBoard());
                    if(newStatus.getDataTrack() != null){
                        infoArea.append(newStatus.getDataTrack());
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
                    gameState = GameState.PLAYING;
                    saveGameButton.setEnabled(false);
                    BoardStatus.clearStacks();
                    BoardStatus previousBoard = new BoardStatus(getCurrentBoard(), "", Integer.parseInt(scoreField.getText()), Integer.parseInt(movesField.getText()));
                    BoardStatus.makeMove(previousBoard);
                    // If the board is fully complete, change the game state to PLAYING
                    findSolutionItem.setEnabled(true);
                    endGameButton.setEnabled(true);
                }   
            }
        });
        // Add the startPlayingButton to the boardPanel
        mainMenu.add(startPlayingButton);

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

        frame.revalidate();
        frame.repaint();
    }

    private void play() {
        gameState = GameState.SETTING_UP;
        saveGameButton.setEnabled(true);


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
                            BoardStatus.makeChange(getCurrentBoard());
                            Color chosenColor = colorChooser(frame);
                            boardButtons[row][col].setBackground(chosenColor);
                        } else if (gameState == GameState.PLAYING) {
                            // Handle the button click.
                            BoardStatus previousBoard = new BoardStatus(getCurrentBoard(), getLastLine(), Integer.parseInt(scoreField.getText()), Integer.parseInt(movesField.getText()));
                            BoardStatus.makeMove(previousBoard);
                            ButtonControl.handleButtonClick(getCurrentBoard(), row, col);
                            boardPanel.repaint();
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
        boardPanel.revalidate();
        boardPanel.repaint();

        frame.validate();
        frame.repaint();
        
        isFirstGame = false;
    }

    public void playboard(Token[][] board){
        gameState = GameState.SETTING_UP;
        saveGameButton.setEnabled(true);
        

        undoItem.setEnabled(true);
        redoItem.setEnabled(true);

        int rows = board.length;
        int cols = board[0].length;

        // Remove all existing buttons from the boardPanel
        if(!isFirstGame){
            boardPanel.removeAll();
            boardPanel.revalidate();
            boardPanel.repaint();
        }

        currentScore = 0;
        scoreField.setText(String.valueOf(currentScore));
        movimiento = 1;
        movesField.setText(String.valueOf(movimiento));
        tokensField.setText(String.valueOf(rows * cols));
        infoArea.setText("");

        boardPanel = new JPanel();
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
                            BoardStatus previousBoard = new BoardStatus(getCurrentBoard(), getLastLine(), Integer.parseInt(scoreField.getText()), Integer.parseInt(movesField.getText()));
                            BoardStatus.makeMove(previousBoard);
                            ButtonControl.handleButtonClick(getCurrentBoard(), row, col);
                            boardPanel.repaint();
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
        boardPanel.revalidate();
        boardPanel.repaint();

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
        boardPanel.removeAll();

        boardPanel = new JPanel();
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
                            BoardStatus previousBoard = new BoardStatus(getCurrentBoard(), getLastLine(), Integer.parseInt(scoreField.getText()), Integer.parseInt(movesField.getText()));
                            BoardStatus.makeMove(previousBoard);
                            ButtonControl.handleButtonClick(getCurrentBoard(), row, col);
                            boardPanel.repaint();
                        }
                    }
                });
                boardPanel.add(boardButtons[i][j]);
            }
        }
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.validate();
        boardPanel.revalidate();
        boardPanel.repaint();
        buttonPanel.revalidate();
        buttonPanel.repaint();
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
        infoArea.revalidate();
        infoArea.repaint();
        int remainingTokens = Integer.parseInt(tokensField.getText());
        if(checkEnd() == true || remainingTokens == 0 || remainingTokens == 1){
            endGame();
        }
    }

    public static void endGame(){
        int remainingTokens = Integer.parseInt(tokensField.getText());
        int finalScore = Integer.parseInt(scoreField.getText());
        if(remainingTokens == 0){
            finalScore += 1000;
        }
        if(remainingTokens == 1){
            infoArea.append("Puntuación final: "+finalScore+", quedando "+remainingTokens+" ficha.");
        }else{
            infoArea.append("Puntuación final: "+finalScore+", quedando "+remainingTokens+" fichas.");            
        }
        infoArea.revalidate();
        infoArea.repaint();
        int option = JOptionPane.showConfirmDialog(null, "¿Quieres guardar el resultado del juego en un archivo de texto?", "Guardar resultado", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            // El jugador quiere guardar el resultado del juego.
            FileHandler.writeResultToFile(infoArea.getText());
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
        for (LinkedList<Token> group : groups) {
            if (group.size() >= 2) {
                return false; // Game is not ended, there are still possible moves
            }
        }
        return true; // Game is ended, there are no more possible moves
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
        return lines[lines.length - 1];
    }
}
