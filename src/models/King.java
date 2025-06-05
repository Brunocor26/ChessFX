package models;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public Boolean firstMove = true;

    // Construtor
    public King(String color, int row, int col) {
        super("King", color, row, col);
    }

    public void hasMoved() {
        this.firstMove = false;
    }

    public Boolean isFirstMove() {
        return this.firstMove;
    }

    // Novo método para verificar se uma casa está atacada pela cor adversária
    private boolean isSquareAttacked(Piece[][] board, int row, int col, String attackerColor) {
        // Verifica ataques de peões
        int direction = attackerColor.equals("white") ? -1 : 1;
        int pawnRow = row + direction;
        if (pawnRow >= 0 && pawnRow < 8) {
            if (col - 1 >= 0) {
                Piece p = board[pawnRow][col - 1];
                if (p instanceof Pawn && p.getColor().equals(attackerColor)) {
                    return true;
                }
            }
            if (col + 1 < 8) {
                Piece p = board[pawnRow][col + 1];
                if (p instanceof Pawn && p.getColor().equals(attackerColor)) {
                    return true;
                }
            }
        }

        // Verifica ataques de cavalos
        int[][] knightMoves = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };
        for (int[] m : knightMoves) {
            int r = row + m[0];
            int c = col + m[1];
            if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                Piece p = board[r][c];
                if (p instanceof Knight && p.getColor().equals(attackerColor)) {
                    return true;
                }
            }
        }

        // Verifica ataques nas diagonais para bispos e rainhas
        int[][] diagonalDirs = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] dir : diagonalDirs) {
            int r = row + dir[0];
            int c = col + dir[1];
            while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                Piece p = board[r][c];
                if (p != null) {
                    if (p.getColor().equals(attackerColor)
                            && (p instanceof Bishop || p instanceof Queen)) {
                        return true;
                    } else {
                        break;
                    }
                }
                r += dir[0];
                c += dir[1];
            }
        }

        // Verifica ataques nas linhas e colunas para torres e rainhas
        int[][] straightDirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : straightDirs) {
            int r = row + dir[0];
            int c = col + dir[1];
            while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                Piece p = board[r][c];
                if (p != null) {
                    if (p.getColor().equals(attackerColor)
                            && (p instanceof Rook || p instanceof Queen)) {
                        return true;
                    } else {
                        break;
                    }
                }
                r += dir[0];
                c += dir[1];
            }
        }

        // Verifica ataques do rei (adjacentes)
        int[][] kingMoves = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
        };
        for (int[] m : kingMoves) {
            int r = row + m[0];
            int c = col + m[1];
            if (r >= 0 && r < 8 && c >= 0 && c < 8) {
                Piece p = board[r][c];
                if (p instanceof King && p.getColor().equals(attackerColor)) {
                    return true;
                }
            }
        }

        return false;
    }

    // Método para verificar se o rei está em cheque
    private boolean reiEmCheck(Piece[][] board, String color) {
        int kr = -1, kc = -1;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p instanceof King && p.getColor().equals(color)) {
                    kr = r;
                    kc = c;
                    break;
                }
            }
        }
        if (kr == -1) {
            return true; // rei não encontrado: considera em cheque
        }
        String adversaryColor = color.equals("white") ? "black" : "white";
        return isSquareAttacked(board, kr, kc, adversaryColor);
    }

    // Método que simula o rei em nova posição para ver se fica em cheque
    private boolean reiEmCheckSimulado(Piece[][] board, int newRow, int newCol, String color) {
        int oldRow = this.row;
        int oldCol = this.col;
        Piece originalPieceAtDest = board[newRow][newCol];
        board[oldRow][oldCol] = null;
        board[newRow][newCol] = this;
        this.row = newRow;
        this.col = newCol;

        boolean inCheck = reiEmCheck(board, color);

        // Reverte estado
        this.row = oldRow;
        this.col = oldCol;
        board[oldRow][oldCol] = this;
        board[newRow][newCol] = originalPieceAtDest;

        return inCheck;
    }

    @Override
    public List<int[]> getValidMoves(Piece[][] board) {
        List<int[]> validMoves = new ArrayList<>();
        int[][] moves = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        // Movimentos normais
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

        // Verificar roque
        if (this.firstMove && !reiEmCheck(board, this.color)) {
            int r = this.row;

            // Roque pequeno (lado rei)
            Piece rookPequeno = board[r][7];
            if (rookPequeno instanceof Rook && rookPequeno.getColor().equals(this.color) && ((Rook) rookPequeno).isFirstMove()) {
                if (board[r][5] == null && board[r][6] == null) {
                    if (!reiEmCheckSimulado(board, r, 5, this.color) && !reiEmCheckSimulado(board, r, 6, this.color)) {
                        validMoves.add(new int[]{r, 6});
                    }
                }
            }

            // Roque grande (lado dama)
            Piece rookGrande = board[r][0];
            if (rookGrande instanceof Rook && rookGrande.getColor().equals(this.color) && ((Rook) rookGrande).isFirstMove()) {
                if (board[r][1] == null && board[r][2] == null && board[r][3] == null) {
                    if (!reiEmCheckSimulado(board, r, 3, this.color) && !reiEmCheckSimulado(board, r, 2, this.color)) {
                        validMoves.add(new int[]{r, 2});
                    }
                }
            }
        }

        return validMoves;
    }
}
