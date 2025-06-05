package models;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {

    // Construtor
    public Queen(String color, int row, int col) {
        super("Queen", color, row, col);
    }

    @Override
    public List<int[]> getValidMoves(Piece[][] board) {
        List<int[]> validMoves = new ArrayList<>();

        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // Torre (linhas e colunas)
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Bispo (diagonais)
        };

        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];

            while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                if (board[r][c] == null) {
                    validMoves.add(new int[]{r, c});
                } else {
                    if (!board[r][c].getColor().equals(this.color)) {
                        validMoves.add(new int[]{r, c});
                    }
                    break;
                }
                r += dir[0];
                c += dir[1];
            }
        }

        return validMoves;
    }
}
