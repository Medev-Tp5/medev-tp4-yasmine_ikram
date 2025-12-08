package com.medev.tp4;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game implements Serializable {

    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;

    public Game() {
        this.board = new Board();
        this.player1 = new Player(Color.WHITE);
        this.player2 = new Player(Color.BLACK);
        this.currentPlayer = this.player1;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (!isGameOver()) {
            this.board.display();
            System.out.println("Current player: " + this.currentPlayer.getColor());

            List<Move> captures = getPossibleCaptures();
            if (!captures.isEmpty()) {
                System.out.println("Capture is mandatory!");
            }

            System.out.println("Enter your move (fromRow fromCol toRow toCol), 'save' to save, or 'load' to load:");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("save")) {
                saveGame("dame.txt");
                continue;
            } else if (input.equalsIgnoreCase("load")) {
                loadGame("dame.txt");
                continue;
            }

            try {
                String[] parts = input.split(" ");
                int fromRow = Integer.parseInt(parts[0]);
                int fromCol = Integer.parseInt(parts[1]);
                int toRow = Integer.parseInt(parts[2]);
                int toCol = Integer.parseInt(parts[3]);

                Move move = new Move(fromRow, fromCol, toRow, toCol);
                if (isValidMove(move)) {
                    boolean isCapture = Math.abs(move.getFromRow() - move.getToRow()) == 2;
                    this.board.movePiece(move.getFromRow(), move.getFromCol(), move.getToRow(), move.getToCol());

                    if (isCapture) {
                        int capturedRow = (move.getFromRow() + move.getToRow()) / 2;
                        int capturedCol = (move.getFromCol() + move.getToCol()) / 2;
                        this.board.removePiece(capturedRow, capturedCol);
                    }

                    promotePawn(move.getToRow(), move.getToCol());

                    if (isCapture) {
                        List<Move> nextCaptures = getPossibleCaptures(move.getToRow(), move.getToCol());
                        if (!nextCaptures.isEmpty()) {
                            continue;
                        }
                    }

                    switchPlayer();
                } else {
                    System.out.println("Invalid move. Try again.");
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.out.println("Invalid input. Please use the format 'fromRow fromCol toRow toCol'.");
            }
        }
        this.board.display();
        System.out.println("Game over!");
        System.out.println("Winner: " + getWinner().getColor());
    }

    private boolean isValidMove(Move move) {
        if (move == null) return false;
        List<Move> possibleMoves = getPossibleMoves(move.getFromRow(), move.getFromCol());
        for (Move possibleMove : possibleMoves) {
            if (possibleMove.getToRow() == move.getToRow() && possibleMove.getToCol() == move.getToCol()) {
                return true;
            }
        }
        return false;
    }

    private List<Move> getPossibleMoves(int row, int col) {
        List<Move> captures = getPossibleCaptures(row, col);
        if (!captures.isEmpty()) {
            return captures;
        }

        List<Move> moves = new ArrayList<>();
        Piece piece = this.board.getPiece(row, col);
        if (piece == null || piece.getColor() != this.currentPlayer.getColor()) {
            return moves;
        }

        if (piece.isQueen()) {
            moves.addAll(getQueenMoves(row, col));
        } else {
            moves.addAll(getPawnMoves(row, col));
        }
        return moves;
    }

    private List<Move> getPawnMoves(int row, int col) {
        List<Move> moves = new ArrayList<>();
        int direction = (this.board.getPiece(row, col).getColor() == Color.WHITE) ? -1 : 1;
        int[] dCols = {-1, 1};

        for (int dCol : dCols) {
            int newRow = row + direction;
            int newCol = col + dCol;
            if (isValidPosition(newRow, newCol) && this.board.getPiece(newRow, newCol) == null) {
                moves.add(new Move(row, col, newRow, newCol));
            }
        }
        return moves;
    }

    private List<Move> getQueenMoves(int row, int col) {
        List<Move> moves = new ArrayList<>();
        int[] directions = {-1, 1};
        for (int dRow : directions) {
            for (int dCol : directions) {
                for (int i = 1; i < this.board.getSize(); i++) {
                    int newRow = row + i * dRow;
                    int newCol = col + i * dCol;
                    if (!isValidPosition(newRow, newCol)) break;
                    if (this.board.getPiece(newRow, newCol) == null) {
                        moves.add(new Move(row, col, newRow, newCol));
                    } else {
                        break;
                    }
                }
            }
        }
        return moves;
    }

    private List<Move> getPossibleCaptures() {
        List<Move> captures = new ArrayList<>();
        for (int row = 0; row < this.board.getSize(); row++) {
            for (int col = 0; col < this.board.getSize(); col++) {
                captures.addAll(getPossibleCaptures(row, col));
            }
        }
        return captures;
    }

    private List<Move> getPossibleCaptures(int row, int col) {
        List<Move> captures = new ArrayList<>();
        Piece piece = this.board.getPiece(row, col);
        if (piece == null || piece.getColor() != this.currentPlayer.getColor()) {
            return captures;
        }

        if (piece.isQueen()) {
            captures.addAll(getQueenCaptures(row, col));
        } else {
            captures.addAll(getPawnCaptures(row, col));
        }
        return captures;
    }

    private List<Move> getPawnCaptures(int row, int col) {
        List<Move> captures = new ArrayList<>();
        int[] directions = {-1, 1};

        for (int dRow : directions) {
            for (int dCol : directions) {
                int opponentRow = row + dRow;
                int opponentCol = col + dCol;
                int destRow = row + 2 * dRow;
                int destCol = col + 2 * dCol;

                if (isValidPosition(destRow, destCol) &&
                        this.board.getPiece(destRow, destCol) == null &&
                        this.board.getPiece(opponentRow, opponentCol) != null &&
                        this.board.getPiece(opponentRow, opponentCol).getColor() != this.currentPlayer.getColor()) {
                    captures.add(new Move(row, col, destRow, destCol));
                }
            }
        }
        return captures;
    }

    private List<Move> getQueenCaptures(int row, int col) {
        List<Move> captures = new ArrayList<>();
        int[] directions = {-1, 1};
        for (int dRow : directions) {
            for (int dCol : directions) {
                for (int i = 1; i < this.board.getSize(); i++) {
                    int newRow = row + i * dRow;
                    int newCol = col + i * dCol;
                    if (!isValidPosition(newRow, newCol)) break;

                    Piece piece = this.board.getPiece(newRow, newCol);
                    if (piece != null) {
                        if (piece.getColor() != this.currentPlayer.getColor()) {
                            int jumpRow = newRow + dRow;
                            int jumpCol = newCol + dCol;
                            if (isValidPosition(jumpRow, jumpCol) && this.board.getPiece(jumpRow, jumpCol) == null) {
                                captures.add(new Move(row, col, jumpRow, jumpCol));
                            }
                        }
                        break;
                    }
                }
            }
        }
        return captures;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < this.board.getSize() && col >= 0 && col < this.board.getSize();
    }

    private void promotePawn(int row, int col) {
        Piece piece = this.board.getPiece(row, col);
        if (piece != null && !piece.isQueen()) {
            if (piece.getColor() == Color.WHITE && row == 0) {
                piece.promote();
            } else if (piece.getColor() == Color.BLACK && row == this.board.getSize() - 1) {
                piece.promote();
            }
        }
    }

    private void switchPlayer() {
        this.currentPlayer = (this.currentPlayer == this.player1) ? this.player2 : this.player1;
    }

    private void saveGame(String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(this);
            System.out.println("Game saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving game: " + e.getMessage());
        }
    }

    private void loadGame(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Game loadedGame = (Game) ois.readObject();
            this.board = loadedGame.board;
            this.player1 = loadedGame.player1;
            this.player2 = loadedGame.player2;
            this.currentPlayer = loadedGame.currentPlayer;
            System.out.println("Game loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading game: " + e.getMessage());
        }
    }

    public boolean isGameOver() {
        if (getPieces(Color.WHITE).isEmpty() || getPieces(Color.BLACK).isEmpty() || getPossibleMovesForPlayer(this.currentPlayer).isEmpty()) {
            return true;
        }
        return false;
    }

    public List<Piece> getPieces(Color color) {
        List<Piece> pieces = new ArrayList<>();
        for (int row = 0; row < this.board.getSize(); row++) {
            for (int col = 0; col < this.board.getSize(); col++) {
                Piece piece = this.board.getPiece(row, col);
                if (piece != null && piece.getColor() == color) {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }

    private List<Move> getPossibleMovesForPlayer(Player player) {
        List<Move> moves = new ArrayList<>();
        for (int row = 0; row < this.board.getSize(); row++) {
            for (int col = 0; col < this.board.getSize(); col++) {
                Piece piece = this.board.getPiece(row, col);
                if (piece != null && piece.getColor() == player.getColor()) {
                    moves.addAll(getPossibleMoves(row, col));
                }
            }
        }
        return moves;
    }

    public Player getWinner() {
        if (!isGameOver()) {
            return null;
        }
        if (getPieces(Color.BLACK).isEmpty()) {
            return player1;
        }
        if (getPieces(Color.WHITE).isEmpty()) {
            return player2;
        }
        return (this.currentPlayer == this.player1) ? this.player2 : this.player1;
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}