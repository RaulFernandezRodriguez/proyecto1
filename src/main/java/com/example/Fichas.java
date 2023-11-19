package com.example;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Fichas {
    char[][] tablero;
    int x, y;
    int puntuacion;
    int movimientos;
    static int games;
    static int actualGame;

    public Fichas (char[][] tablero){
        this.tablero = tablero;
        this.x = tablero.length;
        this.y = tablero[0].length;
        this.movimientos = 0;
        this.puntuacion = 0;
    }
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
                char[][] board = gameBoard(gameInput);
                if(checkBoard(board))
                    play(board);
                actualGame++;
            }
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
        
    public static boolean checkBoard(char[][] board){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board.length; j++){
                if(board[i][j] != 'R' && board[i][j] != 'V' && board[i][j] != 'A'){
                    return false;
                }                
            }
        }
        return true;
    }

    public static void play(char[][] board){
        ArrayList<char[]> result = new ArrayList<char[]>();
        //char[][] result = new char[20][5];
        searchBigGroups(board, 0, 0, result);
        printResult(board, result);
    } 

    public static void searchBigGroups(char[][] board, int x, int y, ArrayList<char[]> result){
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
        // los grupos recuerda que no pueden ser menores de 2
        if(maxGroup > 2){
            removeLargestGroup(board, maxGroup, result);
            searchBigGroups(board, maxGroup, y, result);
        }
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

    public static void removeLargestGroup(char[][] board, int group, ArrayList<char[]> result){
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
        result.add(new char[] {(char) inicioX, (char) inicioY, (char) group,  board[inicioX][inicioY], (char) ((int) Math.pow(result.get(games)[2] -2, 2))}); // * (result.get) (result[games][2] -2)*(result[games][2] -2))});
        // result[games][0] = ;
        // result[games][1] = ;
        // result[games][2] = 
        // result[games][3] = ;
        // result[games][5] = ;
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
        for(int i = board.length-1; i >= 0; i--){
            for(int j = board.length-1; j >= 0; j--){
                if(board[i][j] == '_'){
                    board[i][j] = board[i-1][j];
                    board[i-1][j] = '_';
                }
            }
        }
        boolean emptyCol;
        for(int i = board.length-1; i >= 0; i--){
            emptyCol = true;
            for(int j = board.length-1; j >= 0; j--){
                if(board[j][i] != '_'){
                    emptyCol = false;
                }
            }
            if(emptyCol == true){
                for(int j = board.length-1; j >= 0; j--){
                    if(i+1 < board.length){
                        board[j][i] = board[j][i+1];
                        board[j][i+1] = '_';
                    }
                }
            }
        }
    }

    public static void printResult(char[][] board, ArrayList<char[]> result){
        System.out.println("Juego "+actualGame+":");        
        int finalScore = 0;
        // for(int i = 0; i < result[0].length; i++){
        //     if(result[i][4] == 1){
        //         System.out.println("Movimiento "+(i+1)+" en ("+result[i][0]+","+result[i][1]+"): eliminó "+result[i][2]+" fichas de color "+result[i][3]+" y obtuvo "+result[i][4]+" punto.");
        //     }else{
        //         System.out.println("Movimiento "+(i+1)+" en ("+result[i][0]+","+result[i][1]+"): eliminó "+result[i][2]+" fichas de color "+result[i][3]+" y obtuvo "+result[i][4]+" puntos.");
        //     }
        //     finalScore = finalScore + result[i][4];
        // }
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
            for(int j = 0; j < board.length; j++){
                if(board[i][j] != '_'){
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
