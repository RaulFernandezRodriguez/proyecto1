import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class Main {
    private static int games;
    private static int actualGame;
// La asignacion de posiciones esta inversa, cuidado, la ficha mas abajo e izquierda es (1,1)
//crear el juego una vez comprobada la entrada, hacer metodo de comprobar entrada
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        games = Integer.parseInt(scanner.nextLine());
        scanner.nextLine();// Consume the newline character after the number of games.
        if(games < 0){
            System.out.println("Error, numero de juegos invalido");
        }else{
            actualGame = 1;
            while(actualGame <= games){
                String gameInput;
                if(actualGame == games){
                    gameInput = storeLastGame(scanner);
                }else{
                    gameInput = storeGame(scanner);
                }   
                Ficha[][] board = gameBoard(gameInput);
                if(board != null){
                    play(board);
                }else{
                    System.out.println("Error en el juego " + actualGame);
                    break;
                } 
                actualGame++;
                if(actualGame == 1)
                    scanner.nextLine();   

                // Verificar si hay más líneas después del juego actual
                if (!scanner.hasNext()) {
                    break;
                }
            }
        }
        scanner.close();
    }


    
    // Función para leer el input de un juego y devolverlo como una cadena.
    private static String storeGame(Scanner scanner) {
        StringBuilder gameInput = new StringBuilder();
         while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty() || !scanner.hasNextLine()) {
                break; // Fin del juego actual.
            }
            gameInput.append(line).append("\n"); 
        }
        return gameInput.toString().trim(); // Eliminar el último carácter '\n'.
    }

    private static String storeLastGame(Scanner scanner) {
        StringBuilder lastGameInput = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                break; // Se encontró un espacio en blanco, final del último juego.
            }
            lastGameInput.append(line).append("\n");
        }
        return lastGameInput.toString().trim();
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
                if(token.valid() && row.length() == numCols){
                    board[i][j] = token;
                }else{
                    return null;
                }
            }
        }
        return board;
    }
        
    public static void play(Ficha[][] board){
        MovesTree original = new MovesTree(board);
        searchMoves(board, original);
        searchBestMoves(original);
    } 

    public static void searchMoves(Ficha[][] board, MovesTree treeNode){
        ArrayList<LinkedList<Ficha>> groups = new ArrayList<>();
        boolean[][] visited = new boolean[board.length][board[0].length];
        for (boolean[] fila : visited)
            Arrays.fill(fila, false);
        for(int i = board.length -1; i >= 0; i--){
            for(int j = 0; j < board[0].length; j++){
                if(visited[i][j] == false && board[i][j].valid())      
                    //visited[i][j] = true;
                    groups.add(formGroup(board, visited, i, j, board.length -1, board[0].length -1));
            }
        }
        Iterator<LinkedList<Ficha>> groupIterator = groups.iterator();
        while(groupIterator.hasNext()){
            LinkedList<Ficha> currentGroup = groupIterator.next();
            int groupLength = currentGroup.size();
            if(groupLength >= 2){
                Ficha[][] copiaTablero = copyBoard(board);
                removeGroup(copiaTablero, currentGroup, treeNode, groupLength);
            } 
        }
    }

    public static LinkedList<Ficha> formGroup(Ficha[][] board, boolean[][] visited, int x, int y, int rowLength, int colLength){
        LinkedList<Ficha> thisGroup = new LinkedList<>();
        thisGroup.add(board[x][y]);
        visited[x][y] = true;
        if(board[x][y].valid()){
            if(y+1 <= colLength && board[x][y].getColor() == board[x][y+1].getColor() && visited[x][y+1] == false){
                thisGroup.addAll(formGroup(board, visited, x, y+1, rowLength, colLength));
            }
            if(y-1 >= 0 && board[x][y].getColor() == board[x][y-1].getColor() && visited[x][y-1] == false){
                thisGroup.addAll(formGroup(board, visited, x, y-1, rowLength, colLength));
            }
            if(x+1 <= rowLength && board[x][y].getColor() == board[x+1][y].getColor() && visited[x+1][y] == false){
                thisGroup.addAll(formGroup(board, visited, x+1, y, rowLength, colLength));
            }
            if(x-1 >= 0 && board[x][y].getColor() == board[x-1][y].getColor() && visited[x-1][y] == false){
                thisGroup.addAll(formGroup(board, visited, x-1, y, rowLength, colLength));
            }
        }
        return thisGroup;
        //}
        //return null;
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

    public static void removeGroup(Ficha[][] board, LinkedList<Ficha> group, MovesTree treeNode, int groupLength){
        Iterator<Ficha> iterator = group.iterator();
        while(iterator.hasNext()){
            Ficha ficha = iterator.next();
            board[ficha.getRow()][ficha.getCol()].setColor('_');
        }
        Ficha token = group.get(0);
        int x = board.length - token.getRow();
        int y = token.getCol()+1;
        Result score = new Result((int) Math.pow(groupLength-2, 2), x, y, token.getColor(), groupLength);
        MovesTree subBoard = new MovesTree(board, score, treeNode);
        treeNode.addChild(subBoard);
        fixBoard(board);
        searchMoves(board, subBoard);
    }

    public static void fixBoard(Ficha[][] board){
        for (int i = 0; i < board[0].length; i++) {
            int currentRow = board.length - 1;
            // Recorrer la columna desde abajo hacia arriba
            for (int j = board.length - 1; j >= 0; j--) {
                if (board[j][i].getColor() != '_') {
                    // Si es diferente al carácter objetivo, desplazarlo hacia abajo
                    board[currentRow--][i].setColor(board[j][i].getColor());
                }
            }
            while (currentRow >= 0) {
                board[currentRow--][i].setColor('_');
            }
        }
        for(int i = board[0].length -1; i >= 0; i--){
            if(board[board.length -1][i].getColor() == '_'){
                for(int j = board.length-1; j >= 0; j--){
                    if(i+1 < board[0].length){
                        board[j][i].setColor(board[j][i+1].getColor());
                        board[j][i+1].setColor('_');
                    }
                }
            }
        }
    }

    public static void searchBestMoves(MovesTree root){
        int[] maxScore = new int[1]; // Almacena la mejor puntuación encontrada
        int[] maxRemainingTokens = new int[1]; // Almacena la menor cantidad de fichas restantes
        ArrayList<ArrayList<MovesTree>> bestPathsList = new ArrayList<>(); // Almacena la ruta para la mejor puntuación
        depthSearch(root, 0, 0, new ArrayList<>(), maxScore, maxRemainingTokens, bestPathsList);
        ArrayList<MovesTree> bestPath = orderPaths(bestPathsList);
        printResult(bestPath);
    }

    public static void depthSearch(MovesTree node, int currentScore, int remainingTokens, ArrayList<MovesTree> currentPath, int[] maxScore, int[] maxRemainingTokens, ArrayList<ArrayList<MovesTree>> bestPathsList) {
        if (node == null) {
            return;
        }
        if (node.getData() != null) {
            currentScore += node.getData().getPoints();
        }
        remainingTokens = node.getRemainingTokens(node.getBoard());
        currentPath.add(node);
        if (node.getChilds() == null || node.getChilds().isEmpty()) {
            // Es un nodo hoja, evaluamos la puntuación actual y la cantidad de fichas restantes
            boolean isNewBestPath = false;
            if (currentScore > maxScore[0] || (currentScore == maxScore[0] && remainingTokens < maxRemainingTokens[0])) {
                isNewBestPath = true;
            } else if (remainingTokens == 0) {
                // Si quedan 0 fichas, sumar el bono extra de puntos
                isNewBestPath = true;
                currentScore += 1000;
            }
            if (isNewBestPath) {
                if (bestPathsList.isEmpty() || currentScore > maxScore[0]) {
                    maxScore[0] = currentScore;
                    maxRemainingTokens[0] = remainingTokens;
                    bestPathsList.clear();
                    bestPathsList.add(new ArrayList<>(currentPath));
                } else if (currentScore == maxScore[0]) {
                    bestPathsList.add(new ArrayList<>(currentPath));
                }
            }
        } else {
            // Seguimos explorando los hijos
            for (MovesTree child : node.getChilds()) {
                depthSearch(child, currentScore, remainingTokens, new ArrayList<>(currentPath), maxScore, maxRemainingTokens, bestPathsList);
            }
        }
        currentPath.remove(currentPath.size() - 1);
    }

    public static ArrayList<MovesTree> orderPaths(ArrayList<ArrayList<MovesTree>> bestPathsList){
        if (bestPathsList.isEmpty()) {
            return null;
        }
        ArrayList<MovesTree> lexicographicallyFirst = bestPathsList.get(0);
        for (int i = 1; i < bestPathsList.size(); i++) {
            ArrayList<MovesTree> currentPath = bestPathsList.get(i);
            if (orderPaths(currentPath, lexicographicallyFirst)) {
                lexicographicallyFirst = currentPath;
            }
        }
        return lexicographicallyFirst;
    }

    public static boolean orderPaths(ArrayList<MovesTree> path1, ArrayList<MovesTree> path2) {
        for (int i = 0; i < Math.min(path1.size(), path2.size()); i++) {
            int compare = compareMoves(path1.get(i), path2.get(i));
            if (compare != 0) {
                return compare < 0;
            }
        }
        return path1.size() < path2.size();
    }

    public static int compareMoves(MovesTree move1, MovesTree move2) {
        Result data1 = move1.getData();
        Result data2 = move2.getData();
        int compareX = Integer.compare(data1.getXPosition(), data2.getXPosition());
        if (compareX != 0) {
            return compareX;
        }
        int compareY = Integer.compare(data1.getYPosition(), data2.getYPosition());
        if (compareY != 0) {
            return compareY;
        }
        return Integer.compare(data1.getPoints(), data2.getPoints());
    }

    public static void printResult(ArrayList<MovesTree> path){
        System.out.println("Juego "+actualGame+":");        
        int finalScore = 0;
        Iterator<MovesTree> pathIterator = path.iterator();
        pathIterator.next();
        int movimiento = 1;
        while(pathIterator.hasNext()){
            Result data = pathIterator.next().getData();
            if(data.getPoints() == 1){
                System.out.println("Movimiento "+(movimiento)+" en ("+data.getXPosition()+", "+data.getYPosition()+"): eliminó "+data.getGroupLength()+" fichas de color "+data.getGroupColor()+" y obtuvo "+data.getPoints()+" punto.");  
            }else{
                System.out.println("Movimiento "+(movimiento)+" en ("+data.getXPosition()+", "+data.getYPosition()+"): eliminó "+data.getGroupLength()+" fichas de color "+data.getGroupColor()+" y obtuvo "+data.getPoints()+" puntos.");  
            }
            finalScore += data.getPoints();
            movimiento++;
        }
        MovesTree bestResult = null;
        for (MovesTree nodo : path) {
            bestResult = nodo; // Actualiza el tablero en cada iteración
        }
        int remainingTokens = bestResult.getRemainingTokens(bestResult.getBoard());
        if(remainingTokens == 0){
            finalScore = finalScore + 1000;
        }
        if(remainingTokens == 1){
            System.out.println("Puntuación final: "+finalScore+", quedando "+remainingTokens+" ficha.");
        }else{
            System.out.println("Puntuación final: "+finalScore+", quedando "+remainingTokens+" fichas.");            
        }
        if(actualGame != games)
            System.out.print("\n");
    }
}