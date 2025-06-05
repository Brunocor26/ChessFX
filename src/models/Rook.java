package models;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    public Boolean firstMove=true;

    // Construtor
    public Rook(String color, int row, int col) {
        super("Rook", color, row, col);
    }
    
    public void hasMoved(){
        this.firstMove=false;
    }
    
    public Boolean isFirstMove(){
        return this.firstMove;
    }

    @Override
    public List<int[]> getValidMoves(Piece[][] board) {
        List<int[]> validMoves = new ArrayList<>();

        int[][] directions = {
            {-1, 0}, // cima
            {1, 0}, // baixo
            {0, -1}, // esquerda
            {0, 1} // direita
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
                    break; // bloqueado por peça
                }
                r += dir[0];
                c += dir[1];
            }
        }

        return validMoves;
    }

}
