package Model;

import java.util.ArrayList;

public class SearchBestRoute {
        /**
     * Searches for the best moves in the moves tree and prints the result.
     *
     * @param root The root node of the moves tree.
     */
    public static ArrayList<MovesTree> searchBestMoves(MovesTree root) {
        int[] maxScore = new int[1]; // Almacena la mejor puntuación encontrada
        int[] minTokens = new int[] {Integer.MAX_VALUE}; // Almacena el mínimo número de fichas encontradas
        ArrayList<ArrayList<MovesTree>> bestPathsList = new ArrayList<>(); // Almacena la ruta para la mejor puntuación
        depthSearch(root, new ArrayList<>(), maxScore, minTokens, bestPathsList);
        ArrayList<MovesTree> bestPath = orderPaths(bestPathsList);
        return bestPath;
    }

    /**
     * Performs a depth-first search on the moves tree to store all the best paths.
     *
     * @param node           The current node being visited.
     * @param currentPath    The current path being traversed.
     * @param maxScore       The maximum score found so far.
     * @param bestPathsList  The list of best paths.
     */
    public static void depthSearch(MovesTree node, ArrayList<MovesTree> currentPath, int[] maxScore, int[] minTokens, ArrayList<ArrayList<MovesTree>> bestPathsList) {
        if (node == null) {
            return;
        }
        int currentScore = 0;
        int currentTokens = node.getRemainingTokens(node.getBoard());
        if (node.getData() != null) { // We want to evade the root node, without data
            currentScore = node.getTotalScore(); // Use the stored score of the node
        }
        currentPath.add(node);

        // Check if it's a new best path
        if (currentScore > maxScore[0] || currentTokens < minTokens[0]) {
            if (currentScore > maxScore[0]) {
                maxScore[0] = currentScore;
            }
            if (currentTokens < minTokens[0]) {
                minTokens[0] = currentTokens;
            }
            bestPathsList.clear();
            bestPathsList.add(new ArrayList<>(currentPath));
        } else if (currentScore == maxScore[0]) {
            bestPathsList.add(new ArrayList<>(currentPath));
        }

        if (node.getChilds() != null) {
            for (MovesTree child : node.getChilds()) {
                depthSearch(child, new ArrayList<>(currentPath), maxScore, minTokens, bestPathsList);
            }
        }
        currentPath.remove(currentPath.size() - 1);
    }

    /**
     * Orders the best paths lexicographically and returns the lexicographically first path.
     *
     * @param bestPathsList The list of best paths.
     * @return The lexicographically first path.
     */
    public static ArrayList<MovesTree> orderPaths(ArrayList<ArrayList<MovesTree>> bestPathsList) {
        if (bestPathsList.isEmpty()) {
            return null;
        }
        ArrayList<MovesTree> lexicographicallyFirst = bestPathsList.get(0);
        for (int i = 1; i < bestPathsList.size(); i++) {
            ArrayList<MovesTree> currentPath = bestPathsList.get(i);
            if (comparePaths(currentPath, lexicographicallyFirst)) {
                lexicographicallyFirst = currentPath;
            }
        }
        return lexicographicallyFirst;
    }

    /**
     * Compares two paths lexicographically.
     *
     * @param path1 The first path.
     * @param path2 The second path.
     * @return True if path1 is lexicographically smaller than path2, false otherwise.
     */
    public static boolean comparePaths(ArrayList<MovesTree> path1, ArrayList<MovesTree> path2) {
        for (int i = 1; i < Math.min(path1.size(), path2.size()); i++) {
            int compare = compareMoves(path1.get(i), path2.get(i));
            if (compare != 0) {
                return compare < 0;
            }
        }
        return path1.size() < path2.size();
    }

    /**
     * Compares two moves lexicographically.
     *
     * @param move1 The first move.
     * @param move2 The second move.
     * @return -1 if move1 is lexicographically smaller than move2, 1 if move1 is lexicographically greater than move2, 0 if they are equal.
     */
    public static int compareMoves(MovesTree move1, MovesTree move2) {
        Result data1 = move1.getData();
        Result data2 = move2.getData();    

        // Compare based on row (inverted)
        int compareX = Integer.compare(data1.getXPosition(), data2.getXPosition());
        if (compareX != 0) {
            return compareX;
        }   

        // Compare based on column
        int compareY = Integer.compare(data1.getYPosition(), data2.getYPosition());
        if (compareY != 0) {
            return compareY;
        }

        // Compare based on remaining tokensi
        int compareTokens = Integer.compare(move1.getRemainingTokens(move1.getBoard()), move2.getRemainingTokens(move2.getBoard()));
        if (compareTokens != 0) {
            return compareTokens;
        }

        return Integer.compare(data1.getPoints(), data2.getPoints());
    }
}
