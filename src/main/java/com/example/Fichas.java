package com.example;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Fichas {
    char[][] tablero;
    int x, y;
    int puntuacion;
    int movimientos;

    public Fichas (char[][] tablero){
        this.tablero = tablero;
        this.x = tablero.length;
        this.y = tablero[0].length;
        this.movimientos = 0;
        this.puntuacion = 0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numGames = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character after the number of games.

        for (int gameNumber = 1; gameNumber <= numGames; gameNumber++) {
            String gameInput = storeGame(scanner);
            char[][] gameLines = gameBoard(gameInput);

            // Determinar el tamaño del tablero de este juego.
            int rows = gameLines.length;
            int cols = gameLines[0].length;

            char[][] board = new char[rows][cols];

            play(board, 0, gameNumber+1);
            //List<String> moves = new ArrayList<>();
            //while (true) {
            //checkBoard();
            // Find and remove the largest group
            //String[] group = findLargestGroup();
                    //if (group == null) {
                     //break;
                    //}
                    //remove(group);
                    //movimientos++;

            // Ahora tienes el tablero almacenado en 'board' para procesar este juego.
            //}
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

    private static char[][] gameBoard(String gameInput) {
        String[] rows = gameInput.split("\n");
        int numRows = rows.length;
        int numCols = rows[0].length();
    
        char[][] board = new char[numRows][numCols];
    
        for (int i = 0; i < numRows; i++) {
            String row = rows[i];
            for (int j = 0; j < numCols; j++) {
                board[i][j] = row.charAt(j);
            }
        }
    
        return board;
    }
        
    public static void play(char[][] board, int game){
        char[][] result = new char[20][5];
        searchBigGroups(board, 0, 0, result);
        
        printResult(board, result, game);
    } 

    public static int searchBigGroups(char[][] board, int x, int y, char[][] result){
        int[][] size = new int[board.length][board.length];
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                boolean[][] visited = new boolean[board.length][board.length];
                size[i][j] = group(board, visited,  i, j);
            }
        }
        int maxGroup = 0;
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                if(board[i][j] > maxGroup){
                    maxGroup = board[i][j];
                }
            }
        }
        removeLargestGroup(board, maxGroup, result);
    }

    public static int group(char[][] board, boolean[][] visited, int x, int y){
        visited[x][y] = true;
        int groupSize = 1;
        if(board[x][y] == board[x][y+1] && visited[x][y+1] == false){
            groupSize += group(board, visited, x, y+1);
        }
        if(board[x][y] == board[x][y-1] && visited[x][y-1] == false){
            groupSize += group(board, visited, x, y-1);
        }
        if(board[x][y] == board[x+1][y] && visited[x+1][y] == false){
            groupSize += group(board, visited, x+1, y);
        }
        if(board[x][y] == board[x-1][y] && visited[x-1][y] == false){
            groupSize +=group(board, visited, x-1, y);
        }
        return groupSize;
    }

    public static void removeLargestGroup(char[][] board, int group, char[][] result){
        int inicioX = 0, inicioY = 0;
        for(int i = board.length -1; i > 0; i--){
            for(int j = board.length -1; j > 0; j--){
                if(board[i][j] == group){
                    if(i > inicioX){
                        inicioX = i;
                    }
                    if(j > inicioY){
                        inicioY = j;
                    }
                }
            }
        }
        result[movimiento][0] = inicioX;
        result[movimiento][1] = inicioY;
        result[movimiento][2] = size[inicioX][inicioY];
        result[movimiento][3] = board[inicioX][inicioY];
        result[movimiento][5] = (result[movimiento][2] -2)*(result[movimiento][2] -2);
        removeGroup(board, inicioX, inicioY);
        fixBoard(board);
    }

    public static void removeGroup(char[][] board, int x, int y){
        board[x][y] = '_';
        if(board[x][y] == board[x][y+1]){
            removeGroup(board, x, y+1);
        }
        if(board[x][y] == board[x][y-1]){
            removeGroup(board, x, y-1);
        }
        if(board[x][y] == board[x+1][y]){
            removeGroup(board, x+1, y);
        }
        if(board[x][y] == board[x-1][y]){
            removeGroup(board, x-1, y);
        }
    }

    public static void fixBoard(char[][] board){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                if(board[i+1][j] == '_'){
                    board[i+1][j] = board[i][j];
                    board[i][j] = '_';
                }
            }
        }
        boolean emptyCol;
        for(int i = 0; i < board.length; i++){
            emptyCol = true;
            for(int j = 0; j < board.length; j++){
                if(board[j][i] != '_'){
                    emptyCol = false;
                }
            }
            if(emptyCol == true){
                for(int j = 0; j < board.length; j++){
                    board[j][i] = board[j][i+1];
                    board[j][i+1] = '_';
                }
            }
        }
    }

    public static void printResult(char[][] board, char[][] result, int game){
        System.out.println("Juego "+game+":");
        for(int i = 0; i < result[].length; i++){
            System.out.println("Movimiento "+(i+1)+" en ("+result[i][0]+","+result[i][0]+"): eliminó "+result[i][2]+" fichas de color "+result[i][3]+" y obtuvo "+result[i][4]+" puntos.");
        }
        int remaining = 0;
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                if(board[i][j] != '_'){
                    remaining++;
                }
            }
        }
        int finalScore = 0;
        for(int i = 0; i < result[].length; i++){
            finalScore = finalScore + result[i][4];
        }
        if(remaining == 0){
            finalScore = finalScore + 1000;
        }
        System.out.println("Puntuación final: "+finalScore+", quedando "+remaining+" fichas.");
    }
}
