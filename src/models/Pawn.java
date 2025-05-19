package models;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    
     // Construtor
    public Pawn(String color, int row, int col) {
        super("Pawn", color, row, col);
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
        
        // Se na diagonal para a frente houver um peao da outra cor IMPLEMENTAR
        return false;
    }
    
    @Override
    public List<int[]> getValidMoves() {
        List<int[]> validMoves = new ArrayList<>();

        return validMoves;
    }
}
