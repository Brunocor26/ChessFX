package models;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    
     // Construtor
    public Pawn(String color, int row, int col) {
        super("pawn", color, row, col);
    }
    
    @Override
    public boolean isValidMove(int targetRow, int targetCol) {
        // Peões brancos movem-se uma casa para frente
        if (this.color.equals("white") && targetRow == this.row + 1 && targetCol == this.col) {
            return true;
        }
        // Peões negros movem-se uma casa para frente
        if (this.color.equals("black") && targetRow == this.row - 1 && targetCol == this.col) {
            return true;
        }
        return false;
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
