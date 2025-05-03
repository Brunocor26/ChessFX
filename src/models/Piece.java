package models;

import java.util.List;

public abstract class Piece {
    protected String type;  // Tipo da peça (e.g., "king", "queen", "rook", etc.)
    protected String color; // Cor da peça ("white" ou "black")
    protected int row;      // Linha da peça no tabuleiro
    protected int col;      // Coluna da peça no tabuleiro
    protected String imageName; // Nome da imagem associada à peça

    // Construtor
    public Piece(String type, String color, int row, int col) {
        this.type = type;
        this.color = color;
        this.row = row;
        this.col = col;
        this.imageName = color + "-" + type + ".png"; // Exemplo: "white-pawn.png" para peão branco
    }

    // Getters e Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    // Metodo abstrato para verificar se o movimento é válido
    public abstract boolean isValidMove(int targetRow, int targetCol);
    
    // Metodo abstrato para devolver os movimentos validos numa lista
    public abstract List<int[]> getValidMoves();
}
