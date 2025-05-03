package models;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    // Construtor
    public Queen(String color, int row, int col) {
        super("queen", color, row, col);
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol) {
        // A rainha pode mover-se na vertical, horizontal ou diagonal
        return (this.row == targetRow || this.col == targetCol || 
                Math.abs(targetRow - this.row) == Math.abs(targetCol - this.col));
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
