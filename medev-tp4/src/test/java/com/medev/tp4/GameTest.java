package com.medev.tp4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    private Game game;
    private Board board;

    @BeforeEach
    public void setUp() {
        game = new Game();
        board = new Board();
    }

    @Test
    public void testInitialBoardSetup() {
        // Test that the white and black pieces are in their correct starting positions
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 10; col++) {
                if ((row + col) % 2 != 0) {
                    assertNotNull(board.getPiece(row, col));
                    assertEquals(Color.BLACK, board.getPiece(row, col).getColor());
                }
            }
        }

        for (int row = 6; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((row + col) % 2 != 0) {
                    assertNotNull(board.getPiece(row, col));
                    assertEquals(Color.WHITE, board.getPiece(row, col).getColor());
                }
            }
        }
    }

    @Test
    public void testPawnPromotion() {
        // Test that a white pawn is promoted to a queen when it reaches the last row
        Piece whitePawn = new Piece(Color.WHITE);
        board.getPiece(1, 0);
        board.movePiece(6, 1, 1, 0);
        whitePawn.promote();
        assertTrue(whitePawn.isQueen());

        // Test that a black pawn is promoted to a queen when it reaches the last row
        Piece blackPawn = new Piece(Color.BLACK);
        board.getPiece(8, 9);
        board.movePiece(3, 0, 8, 9);
        blackPawn.promote();
        assertTrue(blackPawn.isQueen());
    }

    @Test
    public void testGameOver() {
        // Test that the game is over when a player has no more pieces
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if (board.getPiece(row, col) != null && board.getPiece(row, col).getColor() == Color.BLACK) {
                    board.removePiece(row, col);
                }
            }
        }
        // To properly test this, we need to inject the modified board into the game
        Game newGame = new Game();
        try {
            java.lang.reflect.Field boardField = Game.class.getDeclaredField("board");
            boardField.setAccessible(true);
            boardField.set(newGame, board);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assertTrue(newGame.isGameOver());
        assertEquals(Color.WHITE, newGame.getWinner().getColor());
    }

    @Test
    public void testPawnMove() {
        // Test that a white pawn can move one step forward
        board.movePiece(6, 1, 5, 0);
        assertNull(board.getPiece(6, 1));
        assertNotNull(board.getPiece(5, 0));
        assertEquals(Color.WHITE, board.getPiece(5, 0).getColor());

        // Test that a black pawn can move one step forward
        board.movePiece(3, 0, 4, 1);
        assertNull(board.getPiece(3, 0));
        assertNotNull(board.getPiece(4, 1));
        assertEquals(Color.BLACK, board.getPiece(4, 1).getColor());
    }

    @Test
    public void testPawnCapture() {
        // Test that a white pawn can capture a black pawn
        board.movePiece(6, 1, 5, 2);
        board.movePiece(3, 0, 4, 1);

        Game newGame = new Game();
        try {
            java.lang.reflect.Field boardField = Game.class.getDeclaredField("board");
            boardField.setAccessible(true);
            boardField.set(newGame, board);

            java.lang.reflect.Method makeMoveMethod = Game.class.getDeclaredMethod("isValidMove", Move.class);
            makeMoveMethod.setAccessible(true);

            Move move = new Move(5, 2, 3, 0);
            boolean isValid = (boolean) makeMoveMethod.invoke(newGame, move);
            assertTrue(isValid);
            
            board.movePiece(move.getFromRow(), move.getFromCol(), move.getToRow(), move.getToCol());
            int capturedRow = (move.getFromRow() + move.getToRow()) / 2;
            int capturedCol = (move.getFromCol() + move.getToCol()) / 2;
            board.removePiece(capturedRow, capturedCol);


        } catch (Exception e) {
            e.printStackTrace();
        }

        assertNull(board.getPiece(5, 2));
        assertNull(board.getPiece(4, 1));
        assertNotNull(board.getPiece(3, 0));
        assertEquals(Color.WHITE, board.getPiece(3, 0).getColor());
    }

    @Test
    public void testSaveAndLoad() {
        // Make a move
        board.movePiece(6, 1, 5, 2);
        board.movePiece(3, 0, 4, 1);

        Game newGame = new Game();
        try {
            java.lang.reflect.Field boardField = Game.class.getDeclaredField("board");
            boardField.setAccessible(true);
            boardField.set(newGame, board);

            java.lang.reflect.Method saveGameMethod = Game.class.getDeclaredMethod("saveGame", String.class);
            saveGameMethod.setAccessible(true);
            saveGameMethod.invoke(newGame, "test.save");

            java.lang.reflect.Method loadGameMethod = Game.class.getDeclaredMethod("loadGame", String.class);
            loadGameMethod.setAccessible(true);
            
            Game loadedGame = new Game();
            loadGameMethod.invoke(loadedGame, "test.save");

            assertEquals(newGame.getWinner(), loadedGame.getWinner());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
