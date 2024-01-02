package Control;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

import Model.GenerateMoves;
import Model.MovesTree;
import Model.Result;
import Model.SearchBestRoute;
import Model.Token;

/**
 * This class represents a SolutionFinder, which is responsible for finding the best solution for a given game board.
 * It extends the SwingWorker class and implements the doInBackground method for background processing.
 */
public class SolutionFinder extends SwingWorker<Void, Void> {
    private final Token[][] board;

    public SolutionFinder(Token[][] board) {
        this.board = board;
    }

      /**
     * Finds the best solution for the given game board.
     * It generates all possible moves, searches for the best route,
     * and displays the results to the user.
     *
     * @param board The game board represented as a 2D array of tokens.
     */
    public static void findSolution(Token[][] board) {
        MovesTree original = new MovesTree(board);
        GenerateMoves.searchMoves(board, original);
        ArrayList<MovesTree> bestPath= SearchBestRoute.searchBestMoves(original);
        int finalScore = 0;
        Iterator<MovesTree> pathIterator = bestPath.iterator();
        pathIterator.next();
        StringBuilder message = new StringBuilder();
        int movimiento = 1;
        while(pathIterator.hasNext()){
            Result data = pathIterator.next().getData();
            if(data.getPoints() == 1){
                message.append("Movimiento "+(movimiento)+" en ("+data.getXPosition()+", "+data.getYPosition()+"): eliminó "+data.getGroupLength()+" fichas de color "+data.getGroupColor()+" y obtuvo "+data.getPoints()+" punto.\n");  
            }else{
                message.append("Movimiento "+(movimiento)+" en ("+data.getXPosition()+", "+data.getYPosition()+"): eliminó "+data.getGroupLength()+" fichas de color "+data.getGroupColor()+" y obtuvo "+data.getPoints()+" puntos.\n");  
            }
            finalScore += data.getPoints();
            movimiento++;
        }
        MovesTree bestResult = null;
        for (MovesTree nodo : bestPath) {
            bestResult = nodo; // Actualiza el tablero en cada iteración
        }
        int remainingTokens = bestResult.getRemainingTokens(bestResult.getBoard());
        if(remainingTokens == 0){
            finalScore = finalScore + 1000;
        }
        if(remainingTokens == 1){
            message.append("Puntuación final: "+finalScore+", quedando "+remainingTokens+" ficha.\n");
        }else{
            message.append("Puntuación final: "+finalScore+", quedando "+remainingTokens+" fichas.\n");            
        }
        JOptionPane.showMessageDialog(null, message.toString());
    }

    @Override
    protected Void doInBackground() throws Exception {
        findSolution(board);
        return null;
    }

}