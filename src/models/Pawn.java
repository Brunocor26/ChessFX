package models;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public static Pawn enPassantVulnerablePawn = null;

    public Pawn(String color, int row, int col) { super("Pawn", color, row, col); }

    @Override
    public List<int[]> getValidMoves(Piece[][] board) {
        List<int[]> vm = new ArrayList<>();

        int dir      = color.equals("white") ? -1 : 1;   // brancos sobem, pretos descem
        int startRow = color.equals("white") ? 6  : 1;

        /* ---------- frente ---------- */
        int f = row + dir;
        if (isInBounds(f,col) && board[f][col] == null){
            vm.add(new int[]{f,col});
            int ff = row + 2*dir;
            if (row == startRow && board[ff][col] == null) vm.add(new int[]{ff,col});
        }

        /* ---------- capturas ---------- */
        for (int dc : new int[]{-1,1}){
            int c = col + dc;
            if (!isInBounds(f,c)) continue;

            // captura normal
            if (board[f][c] != null && !board[f][c].getColor().equals(color))
                vm.add(new int[]{f,c});

            // en passant
            if (board[f][c] == null && enPassantVulnerablePawn != null &&
                enPassantVulnerablePawn.getRow() == row && enPassantVulnerablePawn.getCol() == c)
                vm.add(new int[]{f,c});
        }
        return vm;
    }
}