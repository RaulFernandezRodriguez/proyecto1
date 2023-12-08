
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * BoardTest
 */
public class BoardTest {

    private Board testBoard;

    @Before
    public void before(){
        String[] input = {"RRRVAA", "RAVVVA", "VVVVAA"};
        testBoard = new Board(input,3 ,6);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructors() throws Exception{
        String filledBoard = "VAR\nA V\nRVA\n";
        String[] input = {"VAR", "A V", "RVA"};
        testBoard = new Board(input,3 ,3);
        assertEquals(filledBoard, testBoard.toString());
        testBoard = new Board();
        testBoard.toString();
    }

    @Test
    public void testGetGroups(){
        assertEquals(3, testBoard.getGroups().size());
        for (Grupo grupo : testBoard.getGroups()) {
            switch (grupo.getFichas().size()) {
                case 4:
                    assertEquals(Color.RED, grupo.getFichas().get(0).getColor());
                    break;
                case 8:
                    assertEquals(Color.GREEN, grupo.getFichas().get(0).getColor());
                    break;
                case 5:
                    assertEquals(Color.BLUE, grupo.getFichas().get(0).getColor());
                    break;
                default:
                    assertEquals(true, false);
            }
        }
    }

    @Test
    public void testGetFicha(){
        assertEquals(Color.GREEN, testBoard.getFicha(0, 0).getColor());
        assertEquals(Color.RED, testBoard.getFicha(2, 0).getColor());
        assertEquals(Color.BLUE, testBoard.getFicha(2, 5).getColor());
    }

    @Test
    public void testUpdateBoard(){
        String updatedBoard = "    A \nRR AA \nRARAA \n";
        testBoard.getGroups().get(0).deleteGroup();
        testBoard.updateBoard();
        assertEquals(updatedBoard, testBoard.toString());
    }

    @Test
    public void testCreateDeepCopy(){
        Board copy = testBoard.createDeepCopy();
        for (int i = 0; i < testBoard.getRows(); i++) {
            for (int j = 0; j < testBoard.getCols(); j++) {
                assertEquals(testBoard.getFicha(i, j).getColor(), copy.getFicha(i, j).getColor());
            }
        }
    }

    @Test
    public void testIsEmpty(){
        assertEquals(18, testBoard.isEmpty());
        String[] input = {"   ", "   ", "   "};
        testBoard = new Board(input,3 ,3);
        assertEquals(0, testBoard.isEmpty());
    }

    @Test
    public void testToString(){
        String filledBoard = "RRRVAA\nRAVVVA\nVVVVAA\n";
        assertEquals(filledBoard, testBoard.toString());
    }
}