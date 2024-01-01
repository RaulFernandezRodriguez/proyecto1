package Control;

import java.util.Stack;

import Model.Result;
import Model.Token;


public class BoardStatus {
    
    private Token[][] board;
    private Result score;

    private static Stack<BoardStatus> undoStack = new Stack<>();
    private static Stack<BoardStatus> redoStack = new Stack<>();

    public BoardStatus(Token[][] board, Result score) {
        this.board = board;
        this.score = score;
    }

    public BoardStatus(Token[][] board) {
        this.board = board;
        this.score = null;
    }

    public Token[][] getBoard() {
        return board;
    }

    public Result getScore() {
        return score;
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

    public static void makeMove(Token[][] currentBoard, Result currentScore) {
        // After making a move, save the current state to the undo stack
        undoStack.push(new BoardStatus(currentBoard, currentScore));
    }

    public static BoardStatus undoMove() {
        if (!undoStack.isEmpty()) {
            BoardStatus previous = undoStack.pop();
            Token[][] newBoard = previous.getBoard();
            Result newScore = previous.getScore();           
             // Save the current state to the redo stack
            redoStack.push(new BoardStatus(newBoard, newScore));
            return new BoardStatus(newBoard, newScore);
        }
        return null;
    }

    public static BoardStatus redoMove() {
        if (!redoStack.isEmpty()) {
            // Save the current state to the undo stack 
            BoardStatus next = redoStack.pop();
            Token[][] newBoard = next.getBoard();
            Result newScore = next.getScore();
            return new BoardStatus(newBoard, newScore);
        }
        return null;
    }

    public static void clearStacks() {
        undoStack.clear();
        redoStack.clear();
    }
}
