

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * FichaTest
 */
public class FichaTest {

    @Test
    public void testConstructors(){
        Ficha testFicha = new Ficha();
        assertEquals(Color.NONE, testFicha.getColor());
        assertEquals(null, testFicha.getGroup());
        testFicha = new Ficha(Color.RED);
        assertEquals(Color.RED, testFicha.getColor());
        assertEquals(null, testFicha.getGroup());
        Ficha testFicha2 = new Ficha(testFicha);
        assertEquals(Color.RED, testFicha2.getColor());
        assertEquals(null, testFicha2.getGroup());
    }

    @Test
    public void testGetters(){
        Ficha testFicha = new Ficha(Color.RED);
        assertEquals('R', testFicha.getColorRepresentation());
        testFicha = new Ficha(Color.GREEN);
        assertEquals('V', testFicha.getColorRepresentation());
        testFicha = new Ficha(Color.BLUE);
        assertEquals('A', testFicha.getColorRepresentation());
        testFicha = new Ficha(Color.NONE);
        assertEquals(' ', testFicha.getColorRepresentation());
    }

    @Test
    public void testSetColor(){
        Ficha testFicha = new Ficha();
        testFicha.setColor(Color.RED);
        assertEquals(Color.RED, testFicha.getColor());
        testFicha.setColor(Color.GREEN);
        assertEquals(Color.GREEN, testFicha.getColor());
        testFicha.setColor(Color.BLUE);
        assertEquals(Color.BLUE, testFicha.getColor());
        testFicha.setColor(Color.NONE);
        assertEquals(Color.NONE, testFicha.getColor());
    }

    @Test
    public void testSetGroup(){
        Ficha testFicha = new Ficha(Color.RED);
        Grupo testGroup = new Grupo();
        testFicha.setGroup(testGroup);
        assertEquals(testGroup, testFicha.getGroup());
        assertEquals(1, testGroup.getFichas().size());
        testFicha.getGroup().deleteFicha(testFicha);
        testFicha.setGroup(null);
        assertEquals(null, testFicha.getGroup());
        assertEquals(0, testGroup.getFichas().size());
    }
}