package Control;

import java.util.Stack;

import Model.Token;


/**
 * The BoardStatus class represents the status of a game board, for the redo and undo actions.
 * It stores the current state of the board, the data track, and the total score.
 * It also provides methods to make changes, undo changes, redo changes, and clear the stacks.
 * Data track refers to me message show by doing a move, with all its information.
 */
public class BoardStatus {
    
    private Token[][] board;
    private String dataTrack;
    private int totalScore;
    private static int moves;

    private static Stack<BoardStatus> undoStack = new Stack<>();
    private static Stack<BoardStatus> redoStack = new Stack<>();

    /**
     * Constructs a BoardStatus object with the specified board, data track, and total score.
     * Represents boards in the Playing phase.
     * 
     * @param board The game board.
     * @param dataTrack The data track.
     * @param totalScore The total score.
     */
    public BoardStatus(Token[][] board, String dataTrack, int totalScore, int moves) {
        this.board = board;
        this.dataTrack = dataTrack;
        this.totalScore = totalScore;
        BoardStatus.moves = moves;
    }

    /**
     * Constructs a BoardStatus object with the specified board.
     * The data track is set to an empty string and the total score is set to 0.
     * This represent boards in the Set up phase.
     * 
     * @param board The game board.
     */
    public BoardStatus(Token[][] board) {
        this.board = board;
        this.totalScore= 0;
        this.dataTrack = "";
    }

    /**
     * Returns the game board.
     * 
     * @return The game board.
     */
    public Token[][] getBoard() {
        return board;
    }

    /**
     * Returns the total score.
     * 
     * @return The total score.
     */
    public int getScore() {
        return totalScore;
    }

    /**
     * Returns the data track.
     * 
     * @return The data track.
     */
    public String getDataTrack() {
        return dataTrack;
    }

    public int getMoves() {
        return moves;
    }

    /**
     * Makes a change to the current board and saves the current state to the undo stack.
     * Seting up phase.
     * 
     * @param currentBoard The current game board.
     */
    public static void makeChange(Token[][] currentBoard) {
        undoStack.push(new BoardStatus(currentBoard));
    }

    /**
     * Undoes the last change made to the board and saves the current state to the redo stack.
     * 
     * @return The previous game board.
     */
    public static Token[][] undoChange() {
        if (!undoStack.isEmpty()) {
            BoardStatus previous = undoStack.pop();
            Token[][] newBoard = previous.getBoard();         
            redoStack.push(new BoardStatus(newBoard));
            return newBoard;
        }
        return null;
    }

    /**
     * Redoes the last change that was undone and saves the current state to the undo stack.
     * 
     * @return The next game board.
     */
    public static Token[][] redoChange() {
        if (!redoStack.isEmpty()) {
            BoardStatus next = redoStack.pop();
            Token[][] newBoard = next.getBoard();
            undoStack.push(new BoardStatus(newBoard));
            return newBoard;
        }
        return null;
    }

    /**
     * Makes a move using the previous board status and saves the current state to the undo stack.
     * Playing phase.
     * 
     * @param previousBoard The previous board status.
     */
    public static void makeMove(BoardStatus previousBoard) {
        undoStack.push(previousBoard);
    }

    /**
     * Undoes the last move made and saves the current state to the redo stack.
     * 
     * @return The previous board status.
     */
    public static BoardStatus undoMove() {
        if (!undoStack.isEmpty()) {
            BoardStatus previous = undoStack.pop();
            Token[][] newBoard = previous.getBoard();
            int newScore = previous.getScore();      
            String newDataTrack = previous.getDataTrack();     
            redoStack.push(new BoardStatus(newBoard, newDataTrack, newScore, moves));
            return new BoardStatus(newBoard, newDataTrack, newScore, moves);
        }
        return null;
    }

    /**
     * Redoes the last move that was undone and saves the current state to the undo stack.
     * 
     * @return The next board status.
     */
    public static BoardStatus redoMove() {
        if (!redoStack.isEmpty()) {
            BoardStatus next = redoStack.pop();
            Token[][] newBoard = next.getBoard();
            int newScore = next.getScore();      
            String newDataTrack = next.getDataTrack();     
            undoStack.push(new BoardStatus(newBoard, newDataTrack, newScore, moves));
            return new BoardStatus(newBoard, newDataTrack, newScore, moves);
        }
        return null;
    }

    /**
     * Clears the undo and redo stacks.
     */
    public static void clearStacks() {
        undoStack.clear();
        redoStack.clear();
    }
}
