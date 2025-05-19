package models;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    // Construtor
    public King(String color, int row, int col) {
        super("King", color, row, col);
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol) {
        // O rei pode mover-se uma casa em qualquer direção
        return Math.abs(targetRow - this.row) <= 1 && Math.abs(targetCol - this.col) <= 1;
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
