package com.medev.tp4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testInitialSetup() {
        // Test some black pieces
        assertEquals(Color.BLACK, board.getPiece(0, 1).getColor());
        assertEquals(Color.BLACK, board.getPiece(1, 0).getColor());
        assertNull(board.getPiece(0, 0));

        // Test some white pieces
        assertEquals(Color.WHITE, board.getPiece(9, 0).getColor());
        assertEquals(Color.WHITE, board.getPiece(8, 1).getColor());
        assertNull(board.getPiece(9, 1));
        
        // Test empty spaces
        assertNull(board.getPiece(4, 4));
        assertNull(board.getPiece(5, 5));
    }

    @Test
    public void testMovePiece() {
        Piece piece = board.getPiece(3, 0);
        assertNotNull(piece);
        board.movePiece(3, 0, 4, 1);
        assertNull(board.getPiece(3, 0));
        assertEquals(piece, board.getPiece(4, 1));
    }

    @Test
    public void testRemovePiece() {
        assertNotNull(board.getPiece(0, 1));
        board.removePiece(0, 1);
        assertNull(board.getPiece(0, 1));
    }
}
