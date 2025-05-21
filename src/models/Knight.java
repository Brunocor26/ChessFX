package models;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    // Construtor
    public Knight(String color, int row, int col) {
        super("Knight", color, row, col);
    }

    @Override
    public List<int[]> getValidMoves(Piece[][] board) {
        List<int[]> validMoves = new ArrayList<>();

        int[][] moves = {
            {-2, -1}, {-2, 1},
            {-1, -2}, {-1, 2},
            {1, -2}, {1, 2},
            {2, -1}, {2, 1}
        };

        for (int[] move : moves) {
            int newRow = this.row + move[0];
            int newCol = this.col + move[1];

            if (isInBounds(newRow, newCol)) {
                Piece p = board[newRow][newCol];
                // Pode mover se a casa estiver vazia ou com peça adversária
                if (p == null || !p.getColor().equals(this.color)) {
                    validMoves.add(new int[]{newRow, newCol});
                }
            }
        }

        return validMoves;
    }
}
