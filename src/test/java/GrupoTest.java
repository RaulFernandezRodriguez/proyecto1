
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;
/**
 * GrupoTest
 */
public class GrupoTest {

    private static Grupo basicGrupo;

    @Before
    public void before(){
        basicGrupo = new Grupo();
        for(int i = 0; i < 3; i++){
            Ficha newFicha = new Ficha(Color.RED);
            newFicha.setGroup(basicGrupo);
        }
    }

    @Test
    public void testDeleteFicha(){
        Ficha toRemove = basicGrupo.getFichas().get(0);
        basicGrupo.deleteFicha(toRemove);
        assertEquals(2, basicGrupo.getFichas().size());
        assertEquals(basicGrupo, toRemove.getGroup());
        toRemove.setGroup(null);
        assertEquals(null, toRemove.getGroup());
    }

    @Test
    public void testSetMovement(){
        basicGrupo.setMovement(1, 2);
        assertEquals("(1, 2)", basicGrupo.getMovement());
    }

    @Test
    public void testDeleteGroup(){
        assertEquals(3, basicGrupo.deleteGroup());
        assertEquals(0, basicGrupo.getFichas().size());
    }
}