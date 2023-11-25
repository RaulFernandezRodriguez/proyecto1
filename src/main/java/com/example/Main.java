package com.example;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

public class Main {
    static int games;
    static int actualGame;
// La asignacion de posiciones esta inversa, cuidado, la ficha mas abajo e izquierda es (1,1)
//crear el juego una vez comprobada la entrada, hacer metodo de comprobar entrada
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        games = scanner.nextInt();
        if(games < 0){
            System.out.println("Error, numero de juegos invalido");
        }else{
            actualGame = 1;
            // Consume the newline character after the number of games.
            while(actualGame <= games){            
                scanner.nextLine(); 
                String gameInput = storeGame(scanner);
                Ficha[][] board = gameBoard(gameInput);
                if(board != null)
                    play(board);
                actualGame++;
            }
        }
        scanner.close();
    }

    private static boolean valid(Ficha ficha){
        if(ficha.getColor() == 'V' || ficha.getColor() == 'R' || ficha.getColor() == 'A'){
            return true;
        }else{
            return false;
        }
    }
    
    // Función para leer el input de un juego y devolverlo como una cadena.
    private static String storeGame(Scanner scanner) {
        StringBuilder gameInput = new StringBuilder();
        while (scanner.hasNext()) {
            String line = scanner.next().trim();
            if (line.isEmpty()) {
                break; // Fin del juego actual.
            }
            gameInput.append(line).append("\n");
        }
        return gameInput.toString().trim(); // Eliminar el último carácter '\n'.
    }

    private static Ficha[][] gameBoard(String gameInput) {
        String[] rows = gameInput.split("\n");
        int numRows = rows.length;
        int numCols = rows[0].length();
        if(numCols > 20){
            return null;
        }
        Ficha[][] board = new Ficha[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            String row = rows[i];
            for (int j = 0; j < numCols; j++) {
                Ficha token = new Ficha(row.charAt(j), i, j);
                if(valid(token) && row.length() == numCols){
                    board[i][j] = token;
                }else{
                    return null;
                }
            }
        }
        return board;
    }
        
    public static void play(Ficha[][] board){
        ArrayList<char[]> result = new ArrayList<char[]>();
        searchMove(board, 0, 0, result);
        printResult(board, result);
    } 

//Hacer este metodo recursivo para que vaya creando sub tableros hasta el final

    public static void searchMove(Ficha[][] board, int x, int y, ArrayList<char[]> result){
        ArrayList<LinkedList<Ficha>> groups = new ArrayList<>();
        boolean[][] visited = new boolean[board.length][board.length];
        for(int i = board.length; i > 0; i--){
            for(int j = board[0].length; j > 0; j--){
                groups.add(formGroup(board, visited, i, j));
            }
        }
        for(int i = 0; i < board.length; i++){
            int groupLength = 0;
            Iterator<Ficha> iterator = groups.get(i).iterator();
            while(iterator.hasNext()){
                groupLength++;
            }
            if(groupLength >= 2 && groups.get(i) != null){
                Ficha[][] copiaTablero = board;
                ArrayList<char[]> parcialResult = new ArrayList<char[]>();
                removeGroup(copiaTablero, groups.get(i), parcialResult, groupLength);
                //searchMove(copiaTablero, x, y, result);
            } 
        }
    }

    public static LinkedList<Ficha> formGroup(Ficha[][] board, boolean[][] visited, int x, int y){
        visited[x][y] = true;
        LinkedList<Ficha> thisGroup = new LinkedList<>();
        thisGroup.add(board[x][y]);
        if(valid(board[x][y])){
            if(board[x][y].getColor() == board[x][y+1].getColor() && visited[x][y+1] == false){
                thisGroup.addAll(formGroup(board, visited, x, y+1));
            }
            if(board[x][y].getColor() == board[x][y-1].getColor() && visited[x][y-1] == false){
                thisGroup.addAll(formGroup(board, visited, x, y-1));
            }
            if(board[x][y].getColor() == board[x+1][y].getColor() && visited[x+1][y] == false){
                thisGroup.addAll(formGroup(board, visited, x+1, y));
            }
            if(board[x][y].getColor() == board[x-1][y].getColor() && visited[x-1][y] == false){
                thisGroup.addAll(formGroup(board, visited, x-1, y));
            }
            return thisGroup;
        }
        return null;
    }

    public static void removeGroup(Ficha[][] board, LinkedList<Ficha> group, ArrayList<char[]> result, int length){
        Iterator<Ficha> iterator = group.iterator();
        while(iterator.hasNext()){
            Ficha ficha = iterator.next();
            board[ficha.getRow()][ficha.getCol()].setColor('_');
        }
        Ficha token = group.get(0);
        int x = board.length - token.getRow();
        int y = board[0].length - token.getCol();
        result.add(new char[] {(char) x, (char) y, (char) length,  board[x][y].getColor(), (char) ((int) Math.pow(result.get(games)[2] -2, 2))});
        fixBoard(board);
        searchMove(board, , , result);
    }
        // problema con crear el resultado, al ser recursivo, tengo que ver como solo imprimir el correcto result

    public static void fixBoard(Ficha[][] board){
        for(int i = board.length-1; i >= 0; i--){
            for(int j = board.length-1; j >= 0; j--){
                if(board[i][j].getColor() == '_'){
                    board[i][j] = board[i-1][j];
                    board[i-1][j].setColor('_');
                }
            }
        }
        boolean emptyCol;
        for(int i = board.length-1; i >= 0; i--){
            emptyCol = true;
            for(int j = board.length-1; j >= 0; j--){
                if(board[j][i].getColor() != '_'){
                    emptyCol = false;
                }
            }
            if(emptyCol == true){
                for(int j = board.length-1; j >= 0; j--){
                    if(i+1 < board.length){
                        board[j][i] = board[j][i+1];
                        board[j][i+1].setColor('_');
                    }
                }
            }
        }
    }

    public static void printResult(Ficha[][] board, ArrayList<char[]> result){
        System.out.println("Juego "+actualGame+":");        
        int finalScore = 0;
        for(int i = 0; i < 5; i++){
            if(result.get(i)[4] == 1){
                System.out.println("Movimiento "+(i+1)+" en ("+result.get(i)[0]+","+result.get(i)[1]+"): eliminó "+result.get(i)[2]+" fichas de color "+result.get(i)[3]+" y obtuvo "+result.get(i)[4]+" punto.");
            }else{
                System.out.println("Movimiento "+(i+1)+" en ("+result.get(i)[0]+","+result.get(i)[1]+"): eliminó "+result.get(i)[2]+" fichas de color "+result.get(i)[3]+" y obtuvo "+result.get(i)[4]+" puntos.");
            }
            finalScore = finalScore + result.get(i)[4];
        }
        int remaining = 0;
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(board[i][j].getColor() != '_'){
                    remaining++;
                }
            }
        }
        if(remaining == 0){
            finalScore = finalScore + 1000;
        }
        if(remaining == 1){
            System.out.println("Puntuación final: "+finalScore+", quedando "+remaining+" ficha.");
        }else{
            System.out.println("Puntuación final: "+finalScore+", quedando "+remaining+" fichas.");            
        }
    }
}
