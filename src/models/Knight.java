package models;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    // Construtor
    public Knight(String color, int row, int col) {
        super("Knight", color, row, col);
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol) {
        // O cavalo se move em um padrão de "L" (2 casas em uma direção e 1 na perpendicular)
        int rowDiff = Math.abs(targetRow - this.row);
        int colDiff = Math.abs(targetCol - this.col);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
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
