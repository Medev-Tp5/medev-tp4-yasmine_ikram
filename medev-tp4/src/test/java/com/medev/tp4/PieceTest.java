/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.medev.tp4;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author yasminebouhadji
 */
public class PieceTest {

    @Test
    public void testPieceCreation() {
        Piece whitePiece = new Piece(Color.WHITE);
        assertEquals(Color.WHITE, whitePiece.getColor());
        assertFalse(whitePiece.isQueen());

        Piece blackPiece = new Piece(Color.BLACK);
        assertEquals(Color.BLACK, blackPiece.getColor());
        assertFalse(blackPiece.isQueen());
    }

    @Test
    public void testPromote() {
        Piece piece = new Piece(Color.WHITE);
        assertFalse(piece.isQueen());
        piece.promote();
        assertTrue(piece.isQueen());
    }

    @Test
    public void testToString() {
        Piece whitePawn = new Piece(Color.WHITE);
        assertEquals("w", whitePawn.toString());

        Piece blackPawn = new Piece(Color.BLACK);
        assertEquals("b", blackPawn.toString());

        Piece whiteQueen = new Piece(Color.WHITE);
        whiteQueen.promote();
        assertEquals("W", whiteQueen.toString());

        Piece blackQueen = new Piece(Color.BLACK);
        blackQueen.promote();
        assertEquals("B", blackQueen.toString());
    }
}

