package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import models.*;
import java.net.URL;
import java.util.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.image.Image;

public class BoardController implements Initializable {

    @FXML
    private BorderPane pane;

    @FXML
    private Label turnoLabel;

    @FXML
    private GridPane boardGrid;

    private String temaPecas = "";
    private String temaTabuleiro = "board";

    private Boolean turnoBranco = true;
    private Piece[][] board = new Piece[8][8];
    private Piece selectedPiece = null;
    private List<StackPane> highlightedCells = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Aplica CSS do tema quando a scene está pronta
        pane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                aplicarTemaTabuleiro();
            }
        });
    }

    public void inicializarTabuleiro() {
        String[] cores = {"white", "black"};
        for (int i = 0; i <= 7; i++) {
            addPiece(new Pawn(cores[0], 6, i));
            addPiece(new Pawn(cores[1], 1, i));
        }
        addPiece(new Rook(cores[1], 0, 0));
        addPiece(new Rook(cores[1], 0, 7));
        addPiece(new Rook(cores[0], 7, 7));
        addPiece(new Rook(cores[0], 7, 0));
        addPiece(new Knight(cores[1], 0, 1));
        addPiece(new Knight(cores[1], 0, 6));
        addPiece(new Knight(cores[0], 7, 1));
        addPiece(new Knight(cores[0], 7, 6));
        addPiece(new Bishop(cores[1], 0, 2));
        addPiece(new Bishop(cores[1], 0, 5));
        addPiece(new Bishop(cores[0], 7, 2));
        addPiece(new Bishop(cores[0], 7, 5));
        addPiece(new Queen(cores[1], 0, 3));
        addPiece(new Queen(cores[0], 7, 3));
        addPiece(new King(cores[1], 0, 4));
        addPiece(new King(cores[0], 7, 4));
    }

    private void addPiece(Piece piece) {
        int row = piece.getRow();
        int col = piece.getCol();
        board[row][col] = piece;
        String imagePath = "/resources/img/" + temaPecas + "/" + piece.getImageName();
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView pieceImageView = new ImageView(image);
        pieceImageView.setFitWidth(60);
        pieceImageView.setFitHeight(60);
        pieceImageView.setPreserveRatio(true);
        StackPane cell = (StackPane) boardGrid.getChildren().get(row * 8 + col);
        cell.getChildren().add(pieceImageView);
    }

    private Piece getPieceAtPosition(int row, int col) {
        return board[row][col];
    }

    @FXML
    private void onCellClicked(MouseEvent event) {
        System.out.println("Carregado!");
        // Começa no nó que originou o evento
        Node node = (Node) event.getSource();
        // Sobe até encontrar o StackPane
        while (node != null && !(node instanceof StackPane)) {
            node = node.getParent();
        }
        if (node == null) {
            return; // Defensive: não era suposto!
        }
        StackPane clickedCell = (StackPane) node;

        Integer row = GridPane.getRowIndex(clickedCell);
        Integer col = GridPane.getColumnIndex(clickedCell);

        row = (row == null) ? 0 : row;
        col = (col == null) ? 0 : col;

        if (selectedPiece == null) {
            selectedPiece = getPieceAtPosition(row, col);
            if (selectedPiece != null) {
                highlightValidMoves(selectedPiece);
            }
        } else {
            if (isValidMove(selectedPiece, row, col)) {
                movePiece(selectedPiece, row, col);
            }
            selectedPiece = null;
            clearHighlights();
        }
    }

    public boolean isValidMove(Piece piece, int targetRow, int targetCol) {
        return piece.isValidMove(targetRow, targetCol);
    }

    private void movePiece(Piece piece, int targetRow, int targetCol) {
        StackPane oldCell = (StackPane) boardGrid.getChildren().get(piece.getRow() * 8 + piece.getCol());
        oldCell.getChildren().clear();
        board[piece.getRow()][piece.getCol()] = null;
        piece.setRow(targetRow);
        piece.setCol(targetCol);
        board[targetRow][targetCol] = piece;
        addPiece(piece);
    }

    private void highlightValidMoves(Piece piece) {
        clearHighlights();
        List<int[]> validMoves = piece.getValidMoves();
        for (int[] move : validMoves) {
            int targetRow = move[0];
            int targetCol = move[1];
            StackPane targetCell = (StackPane) boardGrid.getChildren().get(targetRow * 8 + targetCol);
            Rectangle highlight = new Rectangle(60, 60, Color.rgb(0, 255, 0, 0.3));
            targetCell.getChildren().add(highlight);
            highlightedCells.add(targetCell);
        }
    }

    private void clearHighlights() {
        for (StackPane cell : highlightedCells) {
            if (cell.getChildren().size() > 1) {
                cell.getChildren().remove(cell.getChildren().size() - 1);
            }
        }
        highlightedCells.clear();
    }

    public void receberTemaPecas(String tema) {
        this.temaPecas = tema;
    }

    public void receberTemaTabuleiro(String tema) {
        this.temaTabuleiro = tema;
        aplicarTemaTabuleiro();
    }

    private void aplicarTemaTabuleiro() {
        System.out.println(temaTabuleiro);
        try {
            String path = "/resources/styles/";
            String ficheiro = "board";
            if (temaTabuleiro.equals("Blue")) {
                ficheiro = "board_blue";
            } else if (temaTabuleiro.equals("Green")) {
                ficheiro = "board_green";
            }
            if (pane.getScene() != null) {
                pane.getScene().getStylesheets().clear();
                System.out.println(path + ficheiro + ".css");
                pane.getScene().getStylesheets().add(getClass().getResource(path + ficheiro + ".css").toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Erro a aplicar CSS do tema: " + e.getMessage());
        }
    }
}
