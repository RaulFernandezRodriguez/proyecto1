/**
 * The Result class represents the result of a removing a group in the board, a move.
 * It contains information such as points, position, color, and group length.
 */
public class Result {
    private int points;
    private int x, y;
    private char color;
    private int groupLength;

    /**
     * Constructs a new Result object with the specified parameters.
     *
     * @param points      the points obtained
     * @param x           the x position
     * @param y           the y position
     * @param color       the color of the group
     * @param groupLength the length of the group
     */
    public Result(int points, int x, int y, char color, int groupLength) {
        this.points = points;
        this.x = x;
        this.y = y;
        this.color = color;
        this.groupLength = groupLength;
    }

    /**
     * Returns the points obtained.
     *
     * @return the points obtained
     */
    public int getPoints() {
        return points;
    }

    /**
     * Sets the points obtained.
     *
     * @param points the points obtained
     */
    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Returns the x position.
     *
     * @return the x position
     */
    public int getXPosition() {
        return x;
    }

    /**
     * Returns the y position.
     *
     * @return the y position
     */
    public int getYPosition() {
        return y;
    }

    /**
     * Returns the color of the group.
     *
     * @return the color of the group
     */
    public char getGroupColor() {
        return color;
    }

    /**
     * Returns the length of the group.
     *
     * @return the length of the group
     */
    public int getGroupLength() {
        return groupLength;
    }
}
