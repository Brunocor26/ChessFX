package models;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    // Construtor
    public King(String color, int row, int col) {
        super("King", color, row, col);
    }

    @Override
    public List<int[]> getValidMoves(Piece[][] board) {
        List<int[]> validMoves = new ArrayList<>();

        int[][] moves = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] move : moves) {
            int r = row + move[0];
            int c = col + move[1];

            if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                Piece p = board[r][c];
                if (p == null || !p.getColor().equals(this.color)) {
                    validMoves.add(new int[]{r, c});
                }
            }
        }

        return validMoves;
    }

}
