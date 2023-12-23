package View;
import javax.swing.*;
import javax.swing.undo.UndoManager;

import Model.Token;

import java.awt.*;
import java.awt.event.*;

public class TokenButton extends JButton {
    private Token token;

    public TokenButton(Token token) {
        this.token = token;
        // Set the button color based on the token color.
        this.setBackground(token.getColor());
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
        // Update the button color when the token changes.
        this.setBackground(token.getColor());
    }

    public void updateColor() {
        this.setBackground(token.getColor());
    }
}