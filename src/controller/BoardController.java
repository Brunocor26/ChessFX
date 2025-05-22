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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class BoardController implements Initializable {

    @FXML
    private BorderPane pane;

    @FXML
    private Label turnoLabel;

    @FXML
    private GridPane boardGrid;

    private String temaPecas = "";
    private String temaTabuleiro = "board";

    private Boolean turnoBranco = true;  //define em que turno está
    private Boolean vsIa = false; //se é contra ia ou nao

    // Sons
    private Media somMexer;
    private Media somCaptura;

    private Piece[][] board = new Piece[8][8];
    private Piece selectedPiece = null;
    private List<StackPane> highlightedCells = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            somMexer = new Media(getClass().getResource("/resources/sound/moving.mp3").toExternalForm());
            somCaptura = new Media(getClass().getResource("/resources/sound/capture.mp3").toExternalForm());
        } catch (Exception e) {
            System.err.println("Erro a carregar sons: " + e.getMessage());
        }

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
        Node clickedNode = (Node) event.getTarget();

        // Encontrar StackPane (célula) clicado
        while (clickedNode != null && !(clickedNode instanceof StackPane)) {
            clickedNode = clickedNode.getParent();
        }
        if (clickedNode == null) {
            return;
        }

        int index = boardGrid.getChildren().indexOf(clickedNode);
        int row = index / 8;
        int col = index % 8;

        Piece clickedPiece = getPieceAtPosition(row, col);

        if (clickedPiece != null && clickedPiece.getColor().equals("white") == turnoBranco) {
            // Seleciona a peça e mostra os movimentos válidos
            selectedPiece = clickedPiece;
            showValidMoves();
        } else if (selectedPiece != null) {
            List<int[]> validMoves = selectedPiece.getValidMoves(board);
            boolean validMove = false;
            for (int[] move : validMoves) {
                if (move[0] == row && move[1] == col) {
                    validMove = true;
                    break;
                }
            }
            if (validMove) {
                movePiece(selectedPiece, row, col);
                mudarTurno();
                selectedPiece = null;
                clearHighlights();
            }
        }
    }

    private void showValidMoves() {
        clearHighlights();

        if (selectedPiece == null) {
            return;
        }

        List<int[]> validMoves = selectedPiece.getValidMoves(board);

        for (int[] move : validMoves) {
            int row = move[0];
            int col = move[1];

            StackPane cell = (StackPane) boardGrid.getChildren().get(row * 8 + col);

            Rectangle highlight = new Rectangle(cell.getWidth(), cell.getHeight());
            highlight.setFill(Color.color(1, 1, 0, 0.5)); // amarelo transparente
            highlight.setMouseTransparent(true);
            cell.getChildren().add(highlight);
            highlightedCells.add(cell);
        }
    }

    private void clearHighlights() {
        for (StackPane cell : highlightedCells) {
            cell.getChildren().removeIf(node -> node instanceof Rectangle && ((Rectangle) node).getFill().equals(Color.color(1, 1, 0, 0.5)));
        }
        highlightedCells.clear();
    }

    private void tocarSom(Media media) {
        if (media == null) {
            return;
        }
        MediaPlayer player = new MediaPlayer(media);
        player.play();
        player.setOnEndOfMedia(() -> player.dispose());
    }

    private void movePiece(Piece piece, int newRow, int newCol) {
        Piece destino = board[newRow][newCol];
        if (destino != null && !destino.getColor().equals(piece.getColor())) {
            tocarSom(somCaptura);
        } else {
            tocarSom(somMexer);
        }

        // Remover peça da posição antiga
        StackPane oldCell = (StackPane) boardGrid.getChildren().get(piece.getRow() * 8 + piece.getCol());
        oldCell.getChildren().removeIf(node -> node instanceof ImageView);

        // Atualizar o tabuleiro lógico
        board[piece.getRow()][piece.getCol()] = null;
        piece.setRow(newRow);
        piece.setCol(newCol);

        // Remover qualquer peça adversária na posição nova
        StackPane newCell = (StackPane) boardGrid.getChildren().get(newRow * 8 + newCol);
        newCell.getChildren().removeIf(node -> node instanceof ImageView);
        board[newRow][newCol] = piece;

        // Adicionar imagem da peça na nova célula
        String imagePath = "/resources/img/" + temaPecas + "/" + piece.getImageName();
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView pieceImageView = new ImageView(image);
        pieceImageView.setFitWidth(60);
        pieceImageView.setFitHeight(60);
        pieceImageView.setPreserveRatio(true);
        newCell.getChildren().add(pieceImageView);
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

    public void mudarTurno() {
        this.turnoBranco = !turnoBranco;
        turnoLabel.setText((turnoBranco ? "Turno: Branco" : "Turno: Preto"));

        if (vsIa && !turnoBranco) {  // Se for turno da IA e modo IA ativo
            // Delay para parecer natural (usar Thread para não bloquear UI)
            new Thread(() -> {
                try {
                    Thread.sleep(500);  // meio segundo de pausa
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                javafx.application.Platform.runLater(() -> {
                    jogadaIA();
                });
            }).start();
        }
    }

    private static class Movimento {

        Piece peca;
        int destinoRow;
        int destinoCol;

        Movimento(Piece peca, int destinoRow, int destinoCol) {
            this.peca = peca;
            this.destinoRow = destinoRow;
            this.destinoCol = destinoCol;
        }
    }

    private void jogadaIA() {
        List<Movimento> movimentosPossiveis = new ArrayList<>();

        // Percorrer todas as peças pretas
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.getColor().equals("black")) {
                    List<int[]> validMoves = p.getValidMoves(board);
                    for (int[] move : validMoves) {
                        movimentosPossiveis.add(new Movimento(p, move[0], move[1]));
                    }
                }
            }
        }

        if (movimentosPossiveis.isEmpty()) {
            System.out.println("IA não tem movimentos possíveis!");
            // Aqui poderias detectar xeque-mate ou empate
            return;
        }

        // Escolhe movimento aleatório
        Movimento escolhido = movimentosPossiveis.get(new Random().nextInt(movimentosPossiveis.size()));

        // Executa o movimento
        movePiece(escolhido.peca, escolhido.destinoRow, escolhido.destinoCol);

        // Muda turno para o humano
        mudarTurno();
    }

    public void jogarVSIA(Boolean res) {
        this.vsIa = res;
    }
}
