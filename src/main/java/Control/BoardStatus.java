package Control;

import java.util.Stack;

import Model.Result;
import Model.Token;


public class BoardStatus {
    
    private Token[][] board;
    private String dataTrack;
    private int totalScore;

    private static Stack<BoardStatus> undoStack = new Stack<>();
    private static Stack<BoardStatus> redoStack = new Stack<>();

    public BoardStatus(Token[][] board, String dataTrack, int totalScore) {
        this.board = board;
        this.dataTrack = dataTrack;
        this.totalScore = totalScore;
    }

    public BoardStatus(Token[][] board) {
        this.board = board;
        this.totalScore= 0;
        this.dataTrack = "";
    }

    public Token[][] getBoard() {
        return board;
    }

    public int getScore() {
        return totalScore;
    }

    public String getDataTrack() {
        return dataTrack;
    }

    public static void makeChange(Token[][] currentBoard) {
        // After making a move, save the current state to the undo stack
        undoStack.push(new BoardStatus(currentBoard));
    }

    public static Token[][] undoChange() {
        if (!undoStack.isEmpty()) {
            BoardStatus previous = undoStack.pop();
            Token[][] newBoard = previous.getBoard();         
             // Save the current state to the redo stack
            redoStack.push(new BoardStatus(newBoard));
            return newBoard;
        }
        return null;
    }

    public static Token[][] redoChange() {
        if (!redoStack.isEmpty()) {
            BoardStatus next = redoStack.pop();
            Token[][] newBoard = next.getBoard();
            undoStack.push(new BoardStatus(newBoard));
            return newBoard;
        }
        return null;
    }

    public static void makeMove(BoardStatus previousBoard) {
        // After making a move, save the current state to the undo stack
        undoStack.push(previousBoard);
    }

    public static BoardStatus undoMove() {
        if (!undoStack.isEmpty()) {
            BoardStatus previous = undoStack.pop();
            Token[][] newBoard = previous.getBoard();
            int newScore = previous.getScore();      
            String newDataTrack = previous.getDataTrack();     
             // Save the current state to the redo stack
            redoStack.push(new BoardStatus(newBoard, newDataTrack, newScore));
            return new BoardStatus(newBoard, newDataTrack, newScore);
        }
        return null;
    }

    public static BoardStatus redoMove() {
        if (!redoStack.isEmpty()) {
            // Save the current state to the undo stack 
            BoardStatus next = redoStack.pop();
            Token[][] newBoard = next.getBoard();
            int newScore = next.getScore();      
            String newDataTrack = next.getDataTrack();     
             // Save the current state to the redo stack
            undoStack.push(new BoardStatus(newBoard, newDataTrack, newScore));
            return new BoardStatus(newBoard, newDataTrack, newScore);
        }
        return null;
    }

    public static void clearStacks() {
        undoStack.clear();
        redoStack.clear();
    }
}
