package Control;

import java.util.Stack;

import Model.Result;
import Model.Token;

// Objeto que almacenara muchas cosas, como estado, fichas restantes y puntuacion
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
        // Before making a move, save the current state to the undo stack
        undoStack.push(new BoardStatus(currentBoard));
        // Clear the redo stack
        //redoStack.clear();

        // Make the move...

        // Update board, score, remainingTokens
    }

    public static Token[][] undoChange() {
        if (!undoStack.isEmpty()) {
            // Save the current state to the redo stack
            redoStack.push(new BoardStatus(board));
            // Restore the state from the undo stack
            BoardStatus previous = undoStack.pop();
            Token[][] newBoard = previous.getBoard();
            return newBoard;
        }
    }

    public static Token[][] redoChange() {
        if (!redoStack.isEmpty()) {
            // Save the current state to the undo stack
            undoStack.push(new BoardStatus(board, score, remainingTokens));
            undoStack.push();
            // Restore the state from the redo stack
            BoardStatus next = redoStack.pop();
            Token[][] newBoard = next.getBoard();
            return newBoard;
        }
    }

    public static void makeMove(Token[][] currentBoard, Result currentScore) {
        // Before making a move, save the current state to the undo stack
        undoStack.push(new BoardStatus(currentBoard, currentScore, currentTokens));
        // Clear the redo stack
        //redoStack.clear();

        // Make the move...

        // Update board, score, remainingTokens
    }

    public static BoardStatus undoMove() {
        if (!undoStack.isEmpty()) {
            // Save the current state to the redo stack
            redoStack.push(new BoardStatus(board, score));
            // Restore the state from the undo stack
            BoardStatus previous = undoStack.pop();
            Token[][] newBoard = previous.getBoard();
            Result newScore = previous.getScore();
            return new BoardStatus(newBoard, newScore);
        }
    }

    public static BoardStatus redoMove() {
        if (!redoStack.isEmpty()) {
            // Save the current state to the undo stack
            undoStack.push(new BoardStatus(board, score, remainingTokens));
            undoStack.push();
            // Restore the state from the redo stack
            BoardStatus next = redoStack.pop();
            Token[][] newBoard = next.getBoard();
            Result newScore = next.getScore();
            return new BoardStatus(newBoard, newScore);
        }
    }

    public void clearStacks() {
        undoStack.clear();
        redoStack.clear();
    }
}