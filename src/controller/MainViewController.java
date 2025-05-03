package controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import models.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class MainViewController implements Initializable {

    @FXML
    private GridPane boardGrid;

    private Piece[][] board = new Piece[8][8]; // Matriz para armazenar as peças no tabuleiro
    private Piece selectedPiece = null; // Variável para manter a peça selecionada
    private List<StackPane> highlightedCells = new ArrayList<>(); // Lista de células destacadas

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializa as peças
        for(int i=0; i<=7; i++){
            addPiece(new Pawn("white", 6, i));
            addPiece(new Pawn("black", 1, i));
        }
    }

    // Método para adicionar uma peça ao tabuleiro e atualizar a matriz
    private void addPiece(Piece piece) {
        int row = piece.getRow();
        int col = piece.getCol();
        
        // Atualiza a posição da peça na matriz de peças
        board[row][col] = piece;

        // Lógica para adicionar a peça ao GridPane
        ImageView pieceImageView = new ImageView("/resources/img/"+piece.getImageName());
        pieceImageView.setFitWidth(60);
        pieceImageView.setFitHeight(60);
        pieceImageView.setPreserveRatio(true);

        // Pega o StackPane da célula correspondente e adiciona a imagem
        StackPane cell = (StackPane) boardGrid.getChildren().get(row * 8 + col);
        cell.getChildren().add(pieceImageView);
    }

    // Método para verificar a peça em uma posição específica no tabuleiro
    private Piece getPieceAtPosition(int row, int col) {
        return board[row][col]; // Retorna a peça na posição (row, col) ou null se não houver peça
    }

    // Método para lidar com o clique do jogador na célula do tabuleiro
    @FXML
    private void onCellClicked(MouseEvent event) {
        // Obtenha a célula clicada
        StackPane clickedCell = (StackPane) event.getSource();

        // Obtenha a linha e coluna da célula clicada
        int row = GridPane.getRowIndex(clickedCell);
        int col = GridPane.getColumnIndex(clickedCell);

        // Se não houver peça selecionada, selecione a peça clicada
        if (selectedPiece == null) {
            selectedPiece = getPieceAtPosition(row, col);
            if (selectedPiece != null) {
                highlightValidMoves(selectedPiece); // Destacar os movimentos válidos da peça
            }
        } else {
            // Se já houver uma peça selecionada, verifique se o movimento é válido
            if (isValidMove(selectedPiece, row, col)) {
                movePiece(selectedPiece, row, col);
            }
            selectedPiece = null; // Desmarcar a peça após o movimento
             // Limpar células destacadas
        }
    }

    // Método para verificar se o movimento é válido para uma peça
    public boolean isValidMove(Piece piece, int targetRow, int targetCol) {
        return piece.isValidMove(targetRow, targetCol); //so vai buscar a superclasse piece
    }

    // Método para mover uma peça
    private void movePiece(Piece piece, int targetRow, int targetCol) {
        // Remover a peça da posição anterior
        board[piece.getRow()][piece.getCol()] = null;

        // Atualizar a posição da peça
        piece.setRow(targetRow);
        piece.setCol(targetCol);

        // Atualizar a matriz e a interface do usuário
        board[targetRow][targetCol] = piece;

        // Atualiza a imagem da peça na nova posição
        addPiece(piece);
    }

    // Método para destacar as células válidas de movimento
    private void highlightValidMoves(Piece piece) {
        // Limpa células destacadas antigas
        //ainda nao fiz

        // Obter movimentos válidos da peça
        List<int[]> validMoves = piece.getValidMoves();

        // Para cada movimento válido, destacar a célula correspondente
        for (int[] move : validMoves) {
            int targetRow = move[0];
            int targetCol = move[1];

            // Pega o StackPane da célula correspondente
            StackPane targetCell = (StackPane) boardGrid.getChildren().get(targetRow * 8 + targetCol);

            // Cria um retângulo semi-transparente para destacar a célula
            Rectangle highlight = new Rectangle(60, 60, Color.rgb(0, 255, 0, 0.3)); // Verde claro com transparência
            targetCell.getChildren().add(highlight);
            highlightedCells.add(targetCell); // Adiciona à lista de células destacadas
        }
    }

}
