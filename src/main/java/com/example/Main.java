package com.example;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
        movesTree original = new movesTree(board);
        searchMove(board, original);
        searchBestMoves(original);
    } 

//Hacer este metodo recursivo para que vaya creando sub tableros hasta el final

    public static void searchMove(Ficha[][] board, movesTree result){
        ArrayList<LinkedList<Ficha>> groups = new ArrayList<>();
        boolean[][] visited = new boolean[board.length][board.length];
        Arrays.fill(visited, false);
        for(int i = board.length; i > 0; i--){
            for(int j = 0; j < board[0].length; j++){
                if(visited[i][j] == false)
                    groups.add(formGroup(board, visited, i, j));
            }
        }
        Iterator<LinkedList<Ficha>> groupIterator = groups.iterator();
        int i = 0;
        while(groupIterator.hasNext()){
            int groupLength = 0;
            Iterator<Ficha> iterator = groups.get(i).iterator();
            while(iterator.hasNext()){
                groupLength++;
            }
            if(groupLength >= 2 && groups.get(i) != null){
                Ficha[][] copiaTablero = copyBoard(board);
                removeGroup(copiaTablero, groups.get(i), result, groupLength);
            } 
            i++;
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

    public static Ficha[][] copyBoard (Ficha[][] boardOld){
        int rows = boardOld.length;
        int cols = boardOld[0].length;
        Ficha[][] boardNew = new Ficha[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Ficha current = boardOld[i][j];
                Ficha copied = new Ficha(current.getColor(), current.getRow(), current.getCol());
                boardNew[i][j] = copied;
            }
        }
        return boardNew;
    }

    public static void removeGroup(Ficha[][] board, LinkedList<Ficha> group, movesTree result, int length){
        Iterator<Ficha> iterator = group.iterator();
        while(iterator.hasNext()){
            Ficha ficha = iterator.next();
            board[ficha.getRow()][ficha.getCol()].setColor('_');
        }
        Ficha token = group.get(0);
        int x = board.length - token.getRow();
        int y = board[0].length - token.getCol();
        Result score = new Result((int) Math.pow(length-2, 2), x, y, token.getColor(), length);
        movesTree subBoard = new movesTree(board, score, result);
        result.addChild(subBoard);
        //result.add(new char[] {(char) x, (char) y, (char) length,  board[x][y].getColor(), (char) ((int) Math.pow(result.get(games)[2] -2, 2))});
        fixBoard(board);
        searchMove(board, subBoard);
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

    public static void searchBestMoves(movesTree root){
        int[] maxScore = new int[1]; // Almacena la mejor puntuación encontrada
        ArrayList<movesTree> bestPath = new ArrayList<>(); // Almacena la ruta para la mejor puntuación
        depthSearch(root, 0, new ArrayList<>(), maxScore, bestPath);
        printResult(bestPath);
    }
    
    private static void depthSearch(movesTree node, int currentScore, ArrayList<movesTree> currentPath, int[] maxScore, ArrayList<movesTree> bestPath) {
        if (node == null) {
            return;
        }
        currentScore += node.getData().getPoints();
        currentPath.add(node);
        if (node.getChilds() == null || node.getChilds().isEmpty()) {
            // Es un nodo hoja, evaluamos la puntuación actual
            if (currentScore > maxScore[0]) {
                // Actualizamos la mejor puntuación y su ruta asociada
                maxScore[0] = currentScore;
                bestPath.clear();
                bestPath.addAll(currentPath);
            }
        } else {
            // Seguimos explorando los hijos
            for (movesTree child : node.getChilds()) {
                depthSearch(child, currentScore, currentPath, maxScore, bestPath);
            }
        }
        // Retrocedemos a un nivel superior en el árbol
        currentPath.remove(currentPath.size() - 1);
    }

    public static void printResult(ArrayList<movesTree> path){
        System.out.println("Juego "+actualGame+":");        
        int finalScore = 0;
        Iterator<movesTree> pathIterator = path.iterator();
        int movimiento = 0;
        while(pathIterator.hasNext()){
            Result data = path.get(movimiento).getData();
            if(data.getPoints() == 1){
                System.out.println("Movimiento "+(movimiento+1)+" en ("+data.getXPosition()+","+data.getYPosition()+"): eliminó "+data.getGroupLength()+" fichas de color "+data.getGroupColor()+" y obtuvo "+data.getPoints()+" punto.");  
            }else{
                System.out.println("Movimiento "+(movimiento+1)+" en ("+data.getXPosition()+","+data.getYPosition()+"): eliminó "+data.getGroupLength()+" fichas de color "+data.getGroupColor()+" y obtuvo "+data.getPoints()+" puntos.");  
            }
            finalScore += data.getPoints();
            movimiento++;
        }
        int remaining = 0;
        Ficha[][] finalBoard = null;
        for (movesTree nodo : path) {
            finalBoard = nodo.getBoard(); // Actualiza el tablero en cada iteración
        }
        for(int i = 0; i < finalBoard.length; i++){
            for(int j = 0; j < finalBoard[0].length; j++){
                if(finalBoard[i][j].getColor() != '_'){
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
