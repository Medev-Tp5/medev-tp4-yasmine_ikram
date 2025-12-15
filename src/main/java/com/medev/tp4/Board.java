package com.medev.tp4;

import java.io.Serializable;

public class Board implements Serializable {

    private final Piece[][] board;
    private final int size = 10;

    public Board() {
        board = new Piece[size][size];
        setup();
    }

    public void setup() {
        // Clear the board
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                board[row][col] = null;
            }
        }

        // Place black pieces
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < size; col++) {
                if ((row + col) % 2 != 0) {
                    board[row][col] = new Piece(Color.BLACK);
                }
            }
        }

        // Place white pieces
        for (int row = size - 4; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if ((row + col) % 2 != 0) {
                    board[row][col] = new Piece(Color.WHITE);
                }
            }
        }
    }

    public Piece getPiece(int row, int col) {
        return board[row][col];
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = getPiece(fromRow, fromCol);
        board[fromRow][fromCol] = null;
        board[toRow][toCol] = piece;
    }

    public void removePiece(int row, int col) {
        board[row][col] = null;
    }

    public void display() {
        System.out.print("  ");
        for (int i = 0; i < size; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        for (int row = 0; row < size; row++) {
            System.out.print(row + " ");
            for (int col = 0; col < size; col++) {
                if (board[row][col] != null) {
                    System.out.print(board[row][col] + " ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }
    
    public int getSize() {
        return this.size;
    }
}
