package models;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public static Pawn enPassantVulnerablePawn = null; //campo extra para deterar se o peão está vulnerável ao en passant

    // Construtor
    public Pawn(String color, int row, int col) {
        super("Pawn", color, row, col);
    }

    @Override
    public List<int[]> getValidMoves(Piece[][] board) {
        List<int[]> validMoves = new ArrayList<>();

        int direction = this.color.equals("white") ? -1 : 1; // Branco sobe no tabuleiro (linhas decrescem), preto desce (linhas crescem)
        int startRow = this.color.equals("white") ? 6 : 1;

        int nextRow = this.row + direction;

        // Movimento 1 casa à frente se estiver livre
        if (isInBounds(nextRow, this.col) && board[nextRow][this.col] == null) {
            validMoves.add(new int[]{nextRow, this.col});

            // Movimento 2 casas se estiver na linha inicial e as duas casas estiverem livres
            int twoAheadRow = this.row + 2 * direction;
            if (this.row == startRow && board[twoAheadRow][this.col] == null) {
                validMoves.add(new int[]{twoAheadRow, this.col});
            }
        }

        // Capturas diagonais (esquerda e direita)
        int[] colsToCheck = {this.col - 1, this.col + 1};
        for (int c : colsToCheck) {
            if (isInBounds(nextRow, c) && board[nextRow][c] != null && !board[nextRow][c].getColor().equals(this.color)) {
                validMoves.add(new int[]{nextRow, c});
            }
        }

        return validMoves;
    }
}
