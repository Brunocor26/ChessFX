package models;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {

    // Construtor
    public Rook(String color, int row, int col) {
        super("rook", color, row, col);
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol) {
        // A torre pode mover-se apenas na mesma linha ou coluna
        return this.row == targetRow || this.col == targetCol;
    }
    
    @Override
    public List<int[]> getValidMoves() {
        List<int[]> validMoves = new ArrayList<>();

        int direction = (getColor().equals("white")) ? -1 : 1; // Direção para frente depende da cor

        // Movimento básico para frente
        if (getRow() + direction >= 0 && getRow() + direction < 8) {
            validMoves.add(new int[]{getRow() + direction, getCol()});
        }

        // Movimento de captura (diagonal)
        if (getRow() + direction >= 0 && getRow() + direction < 8) {
            if (getCol() - 1 >= 0) validMoves.add(new int[]{getRow() + direction, getCol() - 1});
            if (getCol() + 1 < 8) validMoves.add(new int[]{getRow() + direction, getCol() + 1});
        }

        return validMoves;
    }
}
