package controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import javafx.util.Duration;
import models.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class BoardController implements Initializable {

    // Elementos FXML
    @FXML
    private BorderPane pane;
    @FXML
    private GridPane boardGrid;
    @FXML
    private FlowPane caixaBranco;
    @FXML
    private FlowPane caixaPreto;
    @FXML
    private Label lblBranco;
    @FXML
    private Label lblPreto;
    @FXML
    private Label lblTempoBranco;
    @FXML
    private Label lblTempoPreto;
    @FXML
    private VBox cemiterioPreto;
    @FXML
    private VBox cemiterioBranco;

    private final List<Rectangle> highlightRects = new ArrayList<>();
    private final Piece[][] board = new Piece[8][8];
    private Piece selectedPiece = null;

    private String temaPecas = "normal";
    private String temaTabuleiro = "Brown";
    private boolean turnoBranco = true;
    private boolean vsIa = false;
    private boolean gameOver = false;

    private Media somMexer, somCaptura;

    // ---- Relógio ----
    private int tempoBranco = 10 * 60; // 10 minutos em segundos
    private int tempoPreto = 10 * 60;  // 10 minutos em segundos
    private Timeline timeline;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            somMexer = new Media(getClass().getResource("/resources/sound/moving.mp3").toExternalForm());
            somCaptura = new Media(getClass().getResource("/resources/sound/capture.mp3").toExternalForm());
        } catch (Exception e) {
            System.err.println("Erro sons: " + e.getMessage());
        }
        pane.sceneProperty().addListener((obs, o, n) -> {
            if (n != null) {
                aplicarTemaTabuleiro();
                atualizarLabelsTempo();
                iniciarRelogio();
            }
        });
    }

    // ----- Relógio -----
    private void atualizarLabelsTempo() {
        lblTempoBranco.setText("Tempo: " + formatarTempo(tempoBranco));
        lblTempoPreto.setText("Tempo: " + formatarTempo(tempoPreto));
    }

    private String formatarTempo(int totalSegundos) {
        int minutos = totalSegundos / 60;
        int segundos = totalSegundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    private void iniciarRelogio() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (!gameOver) {
                if (turnoBranco) {
                    tempoBranco--;
                } else {
                    tempoPreto--;
                }
                atualizarLabelsTempo();
                if (tempoBranco <= 0 || tempoPreto <= 0) {
                    gameOver = true;
                    timeline.stop();
                    mostrarMensagem("Tempo esgotado! " + (tempoBranco <= 0 ? "Preto" : "Branco") + " vence.");
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // ------ Inicializar tabuleiro e peças -----
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

    private List<int[]> getLegalMoves(Piece piece) {
        List<int[]> legalMoves = new ArrayList<>();
        for (int[] move : piece.getValidMoves(board)) {
            if (!deixaReiEmCheck(piece, move[0], move[1])) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    @FXML
    private void onCellClicked(MouseEvent event) throws IOException {
        if (gameOver) {
            return;
        }

        Node clickedNode = (Node) event.getTarget();
        while (clickedNode != null && !(clickedNode instanceof StackPane)) {
            clickedNode = clickedNode.getParent();
        }
        if (clickedNode == null) {
            return;
        }

        int index = boardGrid.getChildren().indexOf(clickedNode);
        int row = index / 8;
        int col = index % 8;

        String corAtual = turnoBranco ? "white" : "black";
        Piece clickedPiece = getPieceAtPosition(row, col);

        if (clickedPiece != null && clickedPiece.getColor().equals(corAtual)) {
            selectedPiece = clickedPiece;
            showValidMoves();
        } else if (selectedPiece != null) {
            List<int[]> validMoves = getLegalMoves(selectedPiece);
            for (int[] move : validMoves) {
                if (move[0] == row && move[1] == col) {
                    clearHighlights();
                    movePiece(selectedPiece, row, col);
                    verificarEstadoJogo();
                    mudarTurno();
                    selectedPiece = null;
                    return;
                }
            }
        }
    }

    private void showValidMoves() {
        clearHighlights();
        if (selectedPiece == null) {
            return;
        }
        List<int[]> validMoves = getLegalMoves(selectedPiece);
        for (int[] move : validMoves) {
            int row = move[0];
            int col = move[1];
            StackPane cell = (StackPane) boardGrid.getChildren().get(row * 8 + col);

            Rectangle highlight = new Rectangle(cell.getWidth() * 0.88, cell.getHeight() * 0.88);
            highlight.setArcWidth(24);
            highlight.setArcHeight(24);
            highlight.setMouseTransparent(true);

            Piece destino = getPieceAtPosition(row, col);
            if (destino != null && !destino.getColor().equals(selectedPiece.getColor())) {
                highlight.getStyleClass().add("capture-cell");
            } else {
                highlight.getStyleClass().add("highlight-cell");
            }
            cell.getChildren().add(highlight);
            highlightRects.add(highlight);
        }
    }

    private void clearHighlights() {
        for (Rectangle rect : highlightRects) {
            Pane parent = (Pane) rect.getParent();
            if (parent != null) {
                parent.getChildren().remove(rect);
            }
        }
        highlightRects.clear();
    }

    private void tocarSom(Media media) {
        if (media == null) {
            return;
        }
        MediaPlayer player = new MediaPlayer(media);
        player.play();
        player.setOnEndOfMedia(() -> player.dispose());
    }

    private void mostrarCapturada(Piece peca, VBox caixa) {
        String imagePath = "/resources/img/" + temaPecas + "/" + peca.getImageName();
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView imgView = new ImageView(image);
        imgView.setFitWidth(30);
        imgView.setFitHeight(30);
        imgView.setPreserveRatio(true);
        caixa.getChildren().add(imgView);
    }

    private void movePiece(Piece piece, int newRow, int newCol) throws IOException {
    int oldRow = piece.getRow();
    int oldCol = piece.getCol();
    Piece destino = board[newRow][newCol];

    // -------- EN PASSANT --------
    boolean isEnPassant = false;
    if (piece instanceof Pawn && destino == null && newCol != oldCol &&
            Pawn.enPassantVulnerablePawn != null &&
            Pawn.enPassantVulnerablePawn.getRow() == oldRow &&
            Pawn.enPassantVulnerablePawn.getCol() == newCol) {
        isEnPassant = true;
        destino = Pawn.enPassantVulnerablePawn;
        // Remove peão capturado da matriz e UI
        board[oldRow][newCol] = null;
        StackPane capturedCell = (StackPane) boardGrid.getChildren().get(oldRow * 8 + newCol);
        capturedCell.getChildren().removeIf(node -> node instanceof ImageView);
        if (destino.getColor().equals("white")) {
            mostrarCapturada(destino, cemiterioBranco);
        } else {
            mostrarCapturada(destino, cemiterioPreto);
        }
    }

    // -------- CAPTURA NORMAL --------
    if (!isEnPassant && destino != null && !destino.getColor().equals(piece.getColor())) {
        tocarSom(somCaptura);
        if (destino.getColor().equals("white")) {
            mostrarCapturada(destino, cemiterioBranco);
        } else {
            mostrarCapturada(destino, cemiterioPreto);
        }
    } else {
        tocarSom(somMexer);
    }

    // Remove peça antiga da célula
    StackPane oldCell = (StackPane) boardGrid.getChildren().get(oldRow * 8 + oldCol);
    oldCell.getChildren().removeIf(node -> node instanceof ImageView);
    board[oldRow][oldCol] = null;

    // -------- PROMOÇÃO --------
    boolean isPawnPromotion = piece instanceof Pawn
            && ((piece.getColor().equals("white") && newRow == 0)
            || (piece.getColor().equals("black") && newRow == 7));
    if (isPawnPromotion) {
        piece.setRow(newRow);
        piece.setCol(newCol);
        Pawn.enPassantVulnerablePawn = null; // limpa en passant
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Promotion.fxml"));
        Parent root = loader.load();
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.TRANSPARENT);
        popupStage.setAlwaysOnTop(true);
        Scene popupScene = new Scene(root);
        popupScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        popupStage.setScene(popupScene);
        PromotionController controller = loader.getController();
        controller.receberTemaPeca(temaPecas, piece.getColor());
        controller.setStage(popupStage);
        StackPane peaoCell = (StackPane) boardGrid.getChildren().get(newRow * 8 + newCol);
        javafx.geometry.Point2D coords = peaoCell.localToScreen(peaoCell.getWidth() / 2, peaoCell.getHeight() / 2);
        double popupWidth = 270;
        double popupHeight = 80;
        popupStage.setX(coords.getX() - popupWidth / 2);
        popupStage.setY(coords.getY() - popupHeight / 2);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
        String novoTipo = controller.getPecaEscolhida();
        Piece novaPeca;
        switch (novoTipo) {
            case "Rook":
                novaPeca = new Rook(piece.getColor(), newRow, newCol);
                break;
            case "Bishop":
                novaPeca = new Bishop(piece.getColor(), newRow, newCol);
                break;
            case "Knight":
                novaPeca = new Knight(piece.getColor(), newRow, newCol);
                break;
            default:
                novaPeca = new Queen(piece.getColor(), newRow, newCol);
        }
        board[newRow][newCol] = novaPeca;
        StackPane promoCell = (StackPane) boardGrid.getChildren().get(newRow * 8 + newCol);
        promoCell.getChildren().removeIf(node -> node instanceof ImageView);
        String imagePath = "/resources/img/" + temaPecas + "/" + novaPeca.getImageName();
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView pieceImageView = new ImageView(image);
        pieceImageView.setFitWidth(60);
        pieceImageView.setFitHeight(60);
        pieceImageView.setPreserveRatio(true);
        promoCell.getChildren().add(pieceImageView);
        return;
    }

    // -------- ATUALIZAR POSIÇÃO PEÃO (depois da promoção!) --------
    piece.setRow(newRow);
    piece.setCol(newCol);

    // -------- EN PASSANT: marca vulnerável se peão moveu 2 casas --------
    if (piece instanceof Pawn && Math.abs(newRow - oldRow) == 2) {
        Pawn.enPassantVulnerablePawn = (Pawn) piece;
    } else {
        Pawn.enPassantVulnerablePawn = null;
    }

    // -------- ATUALIZAR PEÇA E UI --------
    StackPane newCell = (StackPane) boardGrid.getChildren().get(newRow * 8 + newCol);
    newCell.getChildren().removeIf(node -> node instanceof ImageView);
    board[newRow][newCol] = piece;
    String imagePath = "/resources/img/" + temaPecas + "/" + piece.getImageName();
    Image image = new Image(getClass().getResourceAsStream(imagePath));
    ImageView pieceImageView = new ImageView(image);
    pieceImageView.setFitWidth(60);
    pieceImageView.setFitHeight(60);
    pieceImageView.setPreserveRatio(true);
    newCell.getChildren().add(pieceImageView);
}
    
    // === Lógica de cheque, cheque-mate e fim de jogo ===
    private void verificarEstadoJogo() {
        String corOponente = turnoBranco ? "black" : "white";
        if (reiEmCheck(corOponente)) {
            if (estaEmCheckMate(corOponente)) {
                fimDeJogo("Xeque-mate! " + (turnoBranco ? "Branco" : "Preto") + " vence.");
            } else {
                mostrarMensagem("Xeque ao " + (corOponente.equals("white") ? "Branco" : "Preto") + "!");
            }
        }
    }

    private boolean estaEmCheckMate(String color) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && p.getColor().equals(color)) {
                    for (int[] m : getLegalMoves(p)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean reiEmCheck(String color) {
        int kr = -1, kc = -1;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] instanceof King) {
                    King k = (King) board[r][c];
                    if (k.getColor().equals(color)) {
                        kr = r;
                        kc = c;
                    }
                }
            }
        }
        if (kr == -1) {
            return true;
        }
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && !p.getColor().equals(color)) {
                    for (int[] m : p.getValidMoves(board)) {
                        if (m[0] == kr && m[1] == kc) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean deixaReiEmCheck(Piece p, int dr, int dc) {
        Piece cap = board[dr][dc];
        int or = p.getRow(), oc = p.getCol();
        board[or][oc] = null;
        board[dr][dc] = p;
        p.setRow(dr);
        p.setCol(dc);
        boolean check = reiEmCheck(p.getColor());
        p.setRow(or);
        p.setCol(oc);
        board[or][oc] = p;
        board[dr][dc] = cap;
        return check;
    }

    private void mostrarMensagem(String txt) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, txt, ButtonType.OK);
        a.setHeaderText(null);
        a.show();
    }

    private void fimDeJogo(String mensagem) {
        gameOver = true;
        if (timeline != null) {
            timeline.stop();
        }
        Alert a = new Alert(Alert.AlertType.INFORMATION, mensagem, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
        voltarAoMenu();
    }

    private void voltarAoMenu() {
        try {
            URL url = getClass().getResource("/view/Menu.fxml");
            if (url == null) {
                System.err.println("Menu.fxml não encontrado!");
                return;
            }
            Parent root = FXMLLoader.load(url);
            Stage stage = (Stage) pane.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            System.err.println("Erro ao regressar ao menu: " + ex.getMessage());
        }
    }

    // ----------- Métodos para tema e IA -----------
    public void receberTemaPecas(String tema) {
        this.temaPecas = tema;
    }

    public void receberTemaTabuleiro(String tema) {
        this.temaTabuleiro = tema;
        aplicarTemaTabuleiro();
    }

    public void jogarVSIA(Boolean res) {
        this.vsIa = res;
    }

    private void aplicarTemaTabuleiro() {
        try {
            String css = "board";
            if ("Blue".equalsIgnoreCase(temaTabuleiro)) {
                css = "board_blue";
            }
            if ("Green".equalsIgnoreCase(temaTabuleiro)) {
                css = "board_green";
            }
            if (pane.getScene() != null) {
                pane.getScene().getStylesheets().clear();
                pane.getScene().getStylesheets().add(
                        Objects.requireNonNull(
                                getClass().getResource("/resources/styles/" + css + ".css"))
                                .toExternalForm());
            }
        } catch (Exception ex) {
            System.err.println("Tema tabuleiro: " + ex.getMessage());
        }
    }

    /**
     * Altera o turno e executa jogada da IA se aplicável. Aplica o estilo
     * "destacado" para o que vai jogar.
     */
    public void mudarTurno() {
        turnoBranco = !turnoBranco;

        // Limpar destaque anterior
        caixaBranco.getStyleClass().remove("destacado");
        caixaPreto.getStyleClass().remove("destacado");

        // Destacar a box do jogador do turno atual
        if (turnoBranco) {
            caixaBranco.getStyleClass().add("destacado");
        } else {
            caixaPreto.getStyleClass().add("destacado");
        }

        // Se está no modo IA e é vez das pretas, executa jogada
        if (vsIa && !turnoBranco && !gameOver) {
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (Exception ignored) {
                }
                javafx.application.Platform.runLater(this::jogadaIA);
            }).start();
        }
    }

    private static class Movimento {

        Piece p;
        int r, c;

        Movimento(Piece p, int r, int c) {
            this.p = p;
            this.r = r;
            this.c = c;
        }
    }

    private void jogadaIA() {
        List<Movimento> lista = new ArrayList<>();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && "black".equals(p.getColor())) {
                    for (int[] m : getLegalMoves(p)) {
                        lista.add(new Movimento(p, m[0], m[1]));
                    }
                }
            }
        }
        if (lista.isEmpty()) {
            fimDeJogo("Empate!");
            return;
        }
        Movimento esc = lista.get(new Random().nextInt(lista.size()));
        clearHighlights();
        try {
            movePiece(esc.p, esc.r, esc.c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        verificarEstadoJogo();
        mudarTurno();
    }
}
