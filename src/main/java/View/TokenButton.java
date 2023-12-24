package View;
import javax.swing.*;
import javax.swing.undo.UndoManager;

import Model.Token;
import Model.GenerateMoves;
import Model.Result;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class TokenButton extends JButton {
    private Token token;

    public TokenButton(Token token) {
        this.token = token;
        // Set the button color based on the token color.
        this.setBackground(getVisualColor(token.getColor()));
        this.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Handle the button click
                handleButtonClick(getCurrentBoard(), 0, 0);
            }
        });
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
        // Update the button color when the token changes.
        this.setBackground(getVisualColor(token.getColor()));
    }

    public void updateColor() {
        this.setBackground(getVisualColor(token.getColor()));
    }

    public void handleButtonClick(Token[][] board, int row, int col){
        boolean[][] visited = new boolean[board.length][board[0].length];
        for (boolean[] fila : visited)
            Arrays.fill(fila, false);
        LinkedList<Token> group = GenerateMoves.formGroup(board, visited, row, col, board.length - 1, board[0].length - 1);
        Iterator<Token> iterator = group.iterator();
        Token firstToken = group.get(0);
        int groupLength = 0;
        int[] startPosition = new int[2];
        startPosition[0] = firstToken.getRow();
        startPosition[1] = firstToken.getCol();
        while (iterator.hasNext()) {
            Token Token = iterator.next();
            board[Token.getRow()][Token.getCol()].setColor('_');
            groupLength++;
            if(token.getRow() > startPosition[0]){
                startPosition[0] = token.getRow();
            }
            if(token.getCol() < startPosition[1]){
                startPosition[1] = token.getCol();
            }
        }
        int x = board.length - startPosition[0];
        int y = startPosition[1] + 1;
        Result score = new Result((int) Math.pow(groupLength - 2, 2), x, y, firstToken.getColor(), groupLength);
        GenerateMoves.fixBoard(board);
    }

    public Color getVisualColor(char boardColor){
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
}