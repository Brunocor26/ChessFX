package models;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    // Construtor
    public Bishop(String color, int row, int col) {
        super("Bishop", color, row, col);
    }

    @Override
    public List<int[]> getValidMoves(Piece[][] board) {
        List<int[]> validMoves = new ArrayList<>();

        int[][] directions = {
            {-1, -1}, // diagonal cima-esquerda
            {-1, 1}, // diagonal cima-direita
            {1, -1}, // diagonal baixo-esquerda
            {1, 1} // diagonal baixo-direita
        };

        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];

            while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                if (board[r][c] == null) {
                    validMoves.add(new int[]{r, c});
                } else {
                    if (!board[r][c].getColor().equals(this.color)) {
                        validMoves.add(new int[]{r, c}); // captura
                    }
                    break; // bloqueado
                }
                r += dir[0];
                c += dir[1];
            }
        }

        return validMoves;
    }
}
