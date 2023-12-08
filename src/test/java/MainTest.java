
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Unit test for simple App.
 */

public class MainTest {
    private static InputStream in;
    private static PrintStream out;


    @Before
    public void before() throws Exception {
        in = System.in;
        out = System.out;
    }

    @After
    public void after() throws Exception {
        System.setIn(in);
        System.setOut(out);
    }

    @Test
    public void testConstructor(){
        Main main = new Main();
        Assert.assertNotNull(main);
    }

    @Test
    public void agoraInput(){
        String inputTest = "2\n\nAVR\nAAR\nARR\nVVR\n\nVRA\nRAR\nAAR\nRVV\n";
        String expectedOutput = "Juego 1:\n" + //
                "Movimiento 1 en (1, 3): eliminó 5 fichas de color R y obtuvo 9 puntos.\n" + //
                "Movimiento 2 en (2, 1): eliminó 4 fichas de color A y obtuvo 4 puntos.\n" + //
                "Movimiento 3 en (1, 1): eliminó 3 fichas de color V y obtuvo 1 punto.\n" + //
                "Puntuación final: 1014, quedando 0 fichas.\n" + //
                "\n" + //
                "Juego 2:\n" + //
                "Movimiento 1 en (2, 3): eliminó 2 fichas de color R y obtuvo 0 puntos.\n" + //
                "Movimiento 2 en (2, 1): eliminó 4 fichas de color A y obtuvo 4 puntos.\n" + //
                "Movimiento 3 en (1, 1): eliminó 3 fichas de color R y obtuvo 1 punto.\n" + //
                "Movimiento 4 en (1, 1): eliminó 3 fichas de color V y obtuvo 1 punto.\n" + //
                "Puntuación final: 1006, quedando 0 fichas.\n"; 

        InputStream new_in = new ByteArrayInputStream(inputTest.getBytes());
        System.setIn(new_in);

        ByteArrayOutputStream outputTest = new ByteArrayOutputStream();
        PrintStream new_out = new PrintStream(outputTest);
        System.setOut(new_out);

        Main.main(null);

        assertEquals(expectedOutput, outputTest.toString());
    }

    @Test
    public void noSolution(){
        String inputTest = "1\n\nVAR\nRVA\nARV\nVAR\n";
        String expectedOutput = "Juego 1:\n" + //
                "Puntuación final: 0, quedando 12 fichas.\n"; 

        InputStream new_in = new ByteArrayInputStream(inputTest.getBytes());
        System.setIn(new_in);

        ByteArrayOutputStream outputTest = new ByteArrayOutputStream();
        PrintStream new_out = new PrintStream(outputTest);
        System.setOut(new_out);

        Main.main(null);

        assertEquals(expectedOutput, outputTest.toString());
    }

    @Test
    public void readThreeOneCorrect(){
        String inputTest = "3\n\nAVR\nAAR\nARR\nVVR\n\nVRA\nR R\nAAR\nRVV\n\nVV\nRR\n";
        String expectedOutput = "Juego 1:\n" + //
                "Movimiento 1 en (1, 3): eliminó 5 fichas de color R y obtuvo 9 puntos.\n" + //
                "Movimiento 2 en (2, 1): eliminó 4 fichas de color A y obtuvo 4 puntos.\n" + //
                "Movimiento 3 en (1, 1): eliminó 3 fichas de color V y obtuvo 1 punto.\n" + //
                "Puntuación final: 1014, quedando 0 fichas.\n";  

        InputStream new_in = new ByteArrayInputStream(inputTest.getBytes());
        System.setIn(new_in);

        ByteArrayOutputStream outputTest = new ByteArrayOutputStream();
        PrintStream new_out = new PrintStream(outputTest);
        System.setOut(new_out);

        Main.main(null);

        assertEquals(expectedOutput, outputTest.toString());
    }

    @Test
    public void badSpacingBetweenBoards(){
        String inputTest = "2\n\nAVR\nAAR\nARR\nVVR\n\n\nVRA\nRAR\nAAR\nRVV\n";
        String expectedOutput = "Juego 1:\n" + //
                "Movimiento 1 en (1, 3): eliminó 5 fichas de color R y obtuvo 9 puntos.\n" + //
                "Movimiento 2 en (2, 1): eliminó 4 fichas de color A y obtuvo 4 puntos.\n" + //
                "Movimiento 3 en (1, 1): eliminó 3 fichas de color V y obtuvo 1 punto.\n" + //
                "Puntuación final: 1014, quedando 0 fichas.\n";  

        InputStream new_in = new ByteArrayInputStream(inputTest.getBytes());
        System.setIn(new_in);

        ByteArrayOutputStream outputTest = new ByteArrayOutputStream();
        PrintStream new_out = new PrintStream(outputTest);
        System.setOut(new_out);

        Main.main(null);

        assertEquals(expectedOutput, outputTest.toString());
    }

    @Test
    public void badSpacingBetweenNumberInput(){
        String inputTest = "2\nAVR\nAAR\nARR\nVVR\n\nVRA\nRAR\nAAR\nRVV\n";
        String expectedOutput = "";  

        InputStream new_in = new ByteArrayInputStream(inputTest.getBytes());
        System.setIn(new_in);

        ByteArrayOutputStream outputTest = new ByteArrayOutputStream();
        PrintStream new_out = new PrintStream(outputTest);
        System.setOut(new_out);

        Main.main(null);

        assertEquals(expectedOutput, outputTest.toString());
    }

    @Test
    public void wrongNumber(){
        String inputTest = "owo\n\nAVR\nAAR\nARR\nVVR\n\nVRA\nRAR\nAAR\nRVV\n";
        String expectedOutput = "";  

        InputStream new_in = new ByteArrayInputStream(inputTest.getBytes());
        System.setIn(new_in);

        ByteArrayOutputStream outputTest = new ByteArrayOutputStream();
        PrintStream new_out = new PrintStream(outputTest);
        System.setOut(new_out);

        Main.main(null);

        assertEquals(expectedOutput, outputTest.toString());
    }

    @Test
    public void noInput(){
        String inputTest = "";
        String expectedOutput = "";  

        InputStream new_in = new ByteArrayInputStream(inputTest.getBytes());
        System.setIn(new_in);

        ByteArrayOutputStream outputTest = new ByteArrayOutputStream();
        PrintStream new_out = new PrintStream(outputTest);
        System.setOut(new_out);

        Main.main(null);

        assertEquals(expectedOutput, outputTest.toString());
    }

    @Test
    public void biggestInput(){
        String inputTest = "1\n\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //;
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA" + //
                "\nAAAAAAAAAAAAAAAAAAAA\n";
        String expectedOutput = "Juego 1:\n" + //
                "Movimiento 1 en (1, 1): eliminó 400 fichas de color A y obtuvo 158404 puntos.\n" + //
                "Puntuación final: 159404, quedando 0 fichas.\n";  

        InputStream new_in = new ByteArrayInputStream(inputTest.getBytes());
        System.setIn(new_in);

        ByteArrayOutputStream outputTest = new ByteArrayOutputStream();
        PrintStream new_out = new PrintStream(outputTest);
        System.setOut(new_out);

        Main.main(null);

        assertEquals(expectedOutput, outputTest.toString());
    }
}
