

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * FichasIgualesTest
 */
public class FichasIgualesTest {

    @Test (expected = NullPointerException.class)
    public void testFichasIguales(){
        FichasIguales juego = new FichasIguales();
        juego.getSolution();
    }
    
    @Test
    public void agora1(){
        String[] board = {"AVR","AAR","ARR","VVR"};
        FichasIguales juego = new FichasIguales(board, 4, 3);
        String expected = "Movimiento 1 en (1, 3): eliminó 5 fichas de color R y obtuvo 9 puntos.\n" + //
                "Movimiento 2 en (2, 1): eliminó 4 fichas de color A y obtuvo 4 puntos.\n" + //
                "Movimiento 3 en (1, 1): eliminó 3 fichas de color V y obtuvo 1 punto.\n" + //
                "Puntuación final: 1014, quedando 0 fichas.";
        assertEquals(expected, juego.getSolution());
    }

    @Test
    public void agora2(){
        String[] board = {"VRA","RAR","AAR","RVV"};
        FichasIguales juego = new FichasIguales(board, 4, 3);
        String expected = "Movimiento 1 en (2, 3): eliminó 2 fichas de color R y obtuvo 0 puntos.\n" + //
                "Movimiento 2 en (2, 1): eliminó 4 fichas de color A y obtuvo 4 puntos.\n" + //
                "Movimiento 3 en (1, 1): eliminó 3 fichas de color R y obtuvo 1 punto.\n" + //
                "Movimiento 4 en (1, 1): eliminó 3 fichas de color V y obtuvo 1 punto.\n" + //
                "Puntuación final: 1006, quedando 0 fichas."; 
        assertEquals(expected, juego.getSolution());
    }

    @Test
    public void testSolveConflictSameSize(){
        String[] board = {"VVRR","VVRR"};
        FichasIguales juego = new FichasIguales(board, 2, 4);
        String expected = "Movimiento 1 en (1, 1): eliminó 4 fichas de color V y obtuvo 4 puntos.\n" + //
                "Movimiento 2 en (1, 1): eliminó 4 fichas de color R y obtuvo 4 puntos.\n" + //
                "Puntuación final: 1008, quedando 0 fichas."; 
        assertEquals(expected, juego.getSolution());
    }

    @Test
    public void testSolveConflictDifferentSize(){
        String[] board = {"VVRR","VVRR"};
        FichasIguales juego = new FichasIguales(board, 2, 4);
        String expected = "Movimiento 1 en (1, 1): eliminó 4 fichas de color V y obtuvo 4 puntos.\n" + //
                "Movimiento 2 en (1, 1): eliminó 4 fichas de color R y obtuvo 4 puntos.\n" + //
                "Puntuación final: 1008, quedando 0 fichas."; 
        assertEquals(expected, juego.getSolution());
    }
    
    @Test
    public void testNotEmptyBoard(){
        String[] board = { "RVA", "RVV", "RRR" };
        FichasIguales juego = new FichasIguales(board, 3, 3);
        String expected = "Movimiento 1 en (1, 1): eliminó 5 fichas de color R y obtuvo 9 puntos.\n" + //
                "Movimiento 2 en (1, 1): eliminó 3 fichas de color V y obtuvo 1 punto.\n" + //
                "Puntuación final: 10, quedando 1 ficha.";
        assertEquals(expected, juego.getSolution());
    }

    @Test
    public void testVariety(){
        String[] board = { "RAV", "VRA", "ARV" };
        FichasIguales juego = new FichasIguales(board, 3, 3);
        String expected = "Movimiento 1 en (1, 2): eliminó 2 fichas de color R y obtuvo 0 puntos.\n" + //
                "Movimiento 2 en (1, 1): eliminó 2 fichas de color A y obtuvo 0 puntos.\n" + //
                "Movimiento 3 en (1, 1): eliminó 2 fichas de color V y obtuvo 0 puntos.\n" + //
                "Puntuación final: 0, quedando 3 fichas.";
        assertEquals(expected, juego.getSolution());
        board = new String[] { "RAVVR", "RRVAA", "RVAVA" };
        juego = new FichasIguales(board, 3, 5);
        expected = "Movimiento 1 en (1, 1): eliminó 4 fichas de color R y obtuvo 4 puntos.\n" + //
                "Movimiento 2 en (1, 4): eliminó 3 fichas de color A y obtuvo 1 punto.\n" + //
                "Movimiento 3 en (1, 3): eliminó 4 fichas de color V y obtuvo 4 puntos.\n" + //
                "Puntuación final: 9, quedando 4 fichas.";
        assertEquals(expected, juego.getSolution());
        board = new String[] { "VRAR", "RAVV", "VAAR", "RRAA" };
        juego = new FichasIguales(board, 4, 4);
        expected = "Movimiento 1 en (3, 3): eliminó 2 fichas de color V y obtuvo 0 puntos.\n" + //
                "Movimiento 2 en (1, 3): eliminó 6 fichas de color A y obtuvo 16 puntos.\n" + //
                "Movimiento 3 en (1, 1): eliminó 5 fichas de color R y obtuvo 9 puntos.\n" + //
                "Puntuación final: 25, quedando 3 fichas.";
        assertEquals(expected, juego.getSolution());
        board = new String[] { "RARVRA", "VAARVA" };
        juego = new FichasIguales(board, 2, 6);
        expected = "Movimiento 1 en (1, 2): eliminó 3 fichas de color A y obtuvo 1 punto.\n" + //
                "Movimiento 2 en (1, 2): eliminó 2 fichas de color R y obtuvo 0 puntos.\n" + //
                "Movimiento 3 en (1, 1): eliminó 3 fichas de color V y obtuvo 1 punto.\n" + //
                "Movimiento 4 en (1, 1): eliminó 2 fichas de color R y obtuvo 0 puntos.\n" + //
                "Movimiento 5 en (1, 1): eliminó 2 fichas de color A y obtuvo 0 puntos.\n" + //
                "Puntuación final: 1002, quedando 0 fichas.";
        assertEquals(expected, juego.getSolution());
        board = new String[] { "VRARRAA", "AAVARAV" };
        juego = new FichasIguales(board, 2, 7);
        expected = "Movimiento 1 en (1, 5): eliminó 3 fichas de color R y obtuvo 1 punto.\n" + //
                "Movimiento 2 en (1, 4): eliminó 4 fichas de color A y obtuvo 4 puntos.\n" + //
                "Movimiento 3 en (1, 3): eliminó 2 fichas de color V y obtuvo 0 puntos.\n" + //
                "Movimiento 4 en (1, 1): eliminó 3 fichas de color A y obtuvo 1 punto.\n" + //
                "Puntuación final: 6, quedando 2 fichas.";
        assertEquals(expected, juego.getSolution());
        board = new String[] { "AVR", "ARV", "VVV" };
        juego = new FichasIguales(board, 3, 3);
        expected = "Movimiento 1 en (1, 1): eliminó 4 fichas de color V y obtuvo 4 puntos.\n" + //
                "Movimiento 2 en (1, 1): eliminó 2 fichas de color A y obtuvo 0 puntos.\n" + //
                "Movimiento 3 en (1, 1): eliminó 2 fichas de color R y obtuvo 0 puntos.\n" + //
                "Puntuación final: 4, quedando 1 ficha.";
        assertEquals(expected, juego.getSolution());
        board = new String[] { "AVR", "RAR", "RRV", "VVA" };
        juego = new FichasIguales(board, 4, 3);
        expected = "Movimiento 1 en (1, 1): eliminó 2 fichas de color V y obtuvo 0 puntos.\n" + //
                "Movimiento 2 en (1, 1): eliminó 3 fichas de color R y obtuvo 1 punto.\n" + //
                "Movimiento 3 en (1, 1): eliminó 3 fichas de color A y obtuvo 1 punto.\n" + //
                "Movimiento 4 en (1, 1): eliminó 2 fichas de color V y obtuvo 0 puntos.\n" + //
                "Movimiento 5 en (1, 1): eliminó 2 fichas de color R y obtuvo 0 puntos.\n" + //
                "Puntuación final: 1002, quedando 0 fichas.";
        assertEquals(expected, juego.getSolution());
        board = new String[] { "AVR", "AAR", "ARR", "VVV" };
        juego = new FichasIguales(board, 4, 3);
        expected = "Movimiento 1 en (2, 1): eliminó 4 fichas de color A y obtuvo 4 puntos.\n" + //
                "Movimiento 2 en (2, 2): eliminó 4 fichas de color R y obtuvo 4 puntos.\n" + //
                "Movimiento 3 en (1, 1): eliminó 4 fichas de color V y obtuvo 4 puntos.\n" + //
                "Puntuación final: 1012, quedando 0 fichas.";
        assertEquals(expected, juego.getSolution());
        board = new String[] { "AVR", "ARR", "VVR", "RRR" };
        juego = new FichasIguales(board, 4, 3);
        expected = "Movimiento 1 en (1, 1): eliminó 7 fichas de color R y obtuvo 25 puntos.\n" + //
                "Movimiento 2 en (1, 1): eliminó 3 fichas de color V y obtuvo 1 punto.\n" + //
                "Movimiento 3 en (1, 1): eliminó 2 fichas de color A y obtuvo 0 puntos.\n" + //
                "Puntuación final: 1026, quedando 0 fichas.";
        assertEquals(expected, juego.getSolution());
        board = new String[] { "VRA", "RAR", "RRR", "VVR" };
        juego = new FichasIguales(board, 4, 3);
        expected = "Movimiento 1 en (1, 1): eliminó 2 fichas de color V y obtuvo 0 puntos.\n" + //
                "Movimiento 2 en (1, 1): eliminó 7 fichas de color R y obtuvo 25 puntos.\n" + //
                "Movimiento 3 en (1, 2): eliminó 2 fichas de color A y obtuvo 0 puntos.\n" + //
                "Puntuación final: 25, quedando 1 ficha.";
        assertEquals(expected, juego.getSolution());
        board = new String[] { "RRRVAA", "RAVVVA", "VVVVAA" };
        juego = new FichasIguales(board, 3, 6);
        expected = "Movimiento 1 en (2, 1): eliminó 4 fichas de color R y obtuvo 4 puntos.\n" + //
                "Movimiento 2 en (1, 1): eliminó 8 fichas de color V y obtuvo 36 puntos.\n" + //
                "Movimiento 3 en (1, 1): eliminó 6 fichas de color A y obtuvo 16 puntos.\n" + //
                "Puntuación final: 1056, quedando 0 fichas.";
        assertEquals(expected, juego.getSolution());
    }
}