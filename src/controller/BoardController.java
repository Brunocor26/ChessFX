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
import javafx.event.ActionEvent;

public class BoardController implements Initializable {

    // Elementos FXML
    @FXML
    private Button btnResignBranco, btnResignPreto;
    @FXML
    private BorderPane pane;
    @FXML
    private GridPane boardGrid;
    @FXML
    private FlowPane caixaBranco, caixaPreto;
    @FXML
    private Label lblBranco, lblPreto, lblTempoBranco, lblTempoPreto;
    @FXML
    private VBox cemiterioPreto, cemiterioBranco;

    private final List<Rectangle> highlightRects = new ArrayList<>();
    private final Piece[][] board = new Piece[8][8];
    private Piece selectedPiece = null;

    private String temaPecas = "normal";
    private String temaTabuleiro = "Brown";
    private boolean turnoBranco = true;
    private boolean vsIa = false;
    private boolean gameOver = false;

    private Media somMexer, somCaptura, somFim;

    // ---- Relógio ----
    private int tempoBranco = 10 * 60; // 10 minutos em segundos
    private int tempoPreto = 10 * 60;  // 10 minutos em segundos
    private Timeline timeline;

    /**
     * Método initialize que é o primeiro a ser executado, logo quando o fxml é
     * carregado, vai buscar os ficheiros dos sons, e aplica o tema escolhido
     * pelo utilizador no menu, atualiza as labels de tempo e inicia o relógio.
     *
     * @param url
     * @param rb
     */
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

    /**
     * Método para atualizar as labels de tempo, definindo o texto de cada label
     */
    private void atualizarLabelsTempo() {
        lblTempoBranco.setText("Tempo: " + formatarTempo(tempoBranco));
        lblTempoPreto.setText("Tempo: " + formatarTempo(tempoPreto));
    }

    /**
     * Recebe o total de segundos e converte em minutos e segundos.
     *
     * @param totalSegundos Número a converter/formatar.
     * @return
     */
    private String formatarTempo(int totalSegundos) {
        int minutos = totalSegundos / 60;
        int segundos = totalSegundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    /**
     * Inicia o relógio do jogo e atualiza as labels de tempo de cada jogador a
     * cada segundo. Se o tempo de um jogador for 0 (ou menor) declara o game
     * over, mostra a mensagem de vitória e acaba o jogo.
     */
    private void iniciarRelogio() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> { //nova timeline que executa uma keyframe a cada segundo, a propria keyframe gera um evento e executa a cada segundo
            if (!gameOver) { //se jogo nao acabou, decrementa
                if (turnoBranco) {
                    tempoBranco--;
                } else {
                    tempoPreto--;
                }
                atualizarLabelsTempo(); //atualiza rotulos
                if (tempoBranco <= 0 || tempoPreto <= 0) { //verifica se algum dos jogadores acabou o tempo
                    gameOver = true;
                    timeline.stop();
                    mostrarMensagem("Tempo esgotado! " + (tempoBranco <= 0 ? "Preto" : "Branco") + " vence.");
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE); //diz que o relogio é infinito e so para manualmente
        timeline.play();  //comeca
    }

    /**
     * Inicializa o tabuleiro colocando as peças de cada jogador nas posições
     * iniciais.
     */
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

    /**
     * Método que mete as peças no tabuleiro. Usa a variavel global com o tema
     * da peca para definir o caminho para a imagem.
     *
     * @param piece
     */
    private void addPiece(Piece piece) {
        int row = piece.getRow(); //linha onde deve ser colocada
        int col = piece.getCol(); //coluna
        board[row][col] = piece;  //colocar na matriz
        String imagePath = "/resources/img/" + temaPecas + "/" + piece.getImageName();
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView pieceImageView = new ImageView(image);
        pieceImageView.setFitWidth(60);
        pieceImageView.setFitHeight(60);
        pieceImageView.setPreserveRatio(true);
        StackPane cell = (StackPane) boardGrid.getChildren().get(row * 8 + col);  //boardgrid é o tabuleiro, que tem 1 stackpane por celula
        cell.getChildren().add(pieceImageView); //adiciona a imagem à stackpane
    }

    /**
     * Devolve a peça que está na posição indicada.
     *
     * @param row
     * @param col
     * @return
     */
    private Piece getPieceAtPosition(int row, int col) {
        return board[row][col];
    }

    /**
     * Função que determina os movimentos possíveis de uma peça na situação
     * atual do tabuleiro e as coloca numa ArrayList.
     *
     * @param piece
     * @return
     */
    private List<int[]> getLegalMoves(Piece piece) {
        List<int[]> legalMoves = new ArrayList<>();
        for (int[] move : piece.getValidMoves(board)) {
            if (!deixaReiEmCheck(piece, move[0], move[1])) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    /**
     * O listener que lida com os cliques numa célula do tabuleiro, está
     * associada a todos os membros do gridpane. Os eventos que acontecem são:
     * limpar highlights que estejam anteriormente colocados, e mostrar os
     * movimentos possíveis da peça escolhida.
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void onCellClicked(MouseEvent event) throws IOException {
        if (gameOver) {
            return;
        }

        Node clickedNode = (Node) event.getTarget(); //extrai node clicado
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

    /**
     * Método para mostrar os movimentos válidos de uma peça selecionada.
     */
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

    /**
     * Limpa os destaques atuais do tabuleiro.
     */
    private void clearHighlights() {
        for (Rectangle rect : highlightRects) {
            Pane parent = (Pane) rect.getParent();
            if (parent != null) {
                parent.getChildren().remove(rect);
            }
        }
        highlightRects.clear();
    }

    /**
     * Função para tocar som a partir de uma "media".
     *
     * @param media
     */
    private void tocarSom(Media media) {
        if (media == null) {
            return;
        }
        MediaPlayer player = new MediaPlayer(media);
        player.play();
        player.setOnEndOfMedia(() -> player.dispose());
    }

    /**
     * Mostra uma peça que foi capturada no cemitério do seu jogador
     *
     * @param peca
     * @param caixa
     */
    private void mostrarCapturada(Piece peca, VBox caixa) {
        String imagePath = "/resources/img/" + temaPecas + "/" + peca.getImageName();
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView imgView = new ImageView(image);
        imgView.setFitWidth(30);
        imgView.setFitHeight(30);
        imgView.setPreserveRatio(true);
        caixa.getChildren().add(imgView);
    }

    /**
     * Função que lida com o movimento das peças. Lida com: captura, roque
     * (grande e pequeno), en passant e promoção de peão.
     *
     * @param piece
     * @param newRow
     * @param newCol
     * @throws IOException
     */
    void movePiece(Piece piece, int newRow, int newCol) throws IOException {
        int oldRow = piece.getRow();
        int oldCol = piece.getCol();
        Piece destino = board[newRow][newCol];

        // -------- ROQUE -------------
        if (piece instanceof King && Math.abs(newCol - oldCol) == 2) {
            // Roque pequeno (lado do rei)
            if (newCol == 6) {
                Piece rook = board[oldRow][7];
                if (rook instanceof Rook && rook.getColor().equals(piece.getColor())) {
                    // Remove torre da posição antiga
                    board[oldRow][7] = null;
                    StackPane rookOldCell = (StackPane) boardGrid.getChildren().get(oldRow * 8 + 7);
                    rookOldCell.getChildren().removeIf(node -> node instanceof ImageView);

                    // Move torre para a coluna 5
                    rook.setRow(oldRow);
                    rook.setCol(5);
                    board[oldRow][5] = rook;

                    StackPane rookNewCell = (StackPane) boardGrid.getChildren().get(oldRow * 8 + 5);
                    String imagePathRook = "/resources/img/" + temaPecas + "/" + rook.getImageName();
                    Image imageRook = new Image(getClass().getResourceAsStream(imagePathRook));
                    ImageView rookImageView = new ImageView(imageRook);
                    rookImageView.setFitWidth(60);
                    rookImageView.setFitHeight(60);
                    rookImageView.setPreserveRatio(true);
                    rookNewCell.getChildren().add(rookImageView);

                    if (rook instanceof Rook) {
                        ((Rook) rook).hasMoved();
                    }
                }
            } // Roque grande (lado da dama)
            else if (newCol == 2) {
                Piece rook = board[oldRow][0];
                if (rook instanceof Rook && rook.getColor().equals(piece.getColor())) {
                    // Remove torre da posição antiga
                    board[oldRow][0] = null;
                    StackPane rookOldCell = (StackPane) boardGrid.getChildren().get(oldRow * 8 + 0);
                    rookOldCell.getChildren().removeIf(node -> node instanceof ImageView);

                    // Move torre para a coluna 3
                    rook.setRow(oldRow);
                    rook.setCol(3);
                    board[oldRow][3] = rook;

                    StackPane rookNewCell = (StackPane) boardGrid.getChildren().get(oldRow * 8 + 3);
                    String imagePathRook = "/resources/img/" + temaPecas + "/" + rook.getImageName();
                    Image imageRook = new Image(getClass().getResourceAsStream(imagePathRook));
                    ImageView rookImageView = new ImageView(imageRook);
                    rookImageView.setFitWidth(60);
                    rookImageView.setFitHeight(60);
                    rookImageView.setPreserveRatio(true);
                    rookNewCell.getChildren().add(rookImageView);

                    if (rook instanceof Rook) {
                        ((Rook) rook).hasMoved();
                    }
                }
            }
        }

        // -------- EN PASSANT --------
        boolean isEnPassant = false;
        if (piece instanceof Pawn && destino == null && newCol != oldCol
                && Pawn.enPassantVulnerablePawn != null
                && Pawn.enPassantVulnerablePawn.getRow() == oldRow
                && Pawn.enPassantVulnerablePawn.getCol() == newCol) {
            isEnPassant = true;
            destino = Pawn.enPassantVulnerablePawn;
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
            Pawn.enPassantVulnerablePawn = null;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Promotion.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.initStyle(StageStyle.TRANSPARENT);
            popupStage.setAlwaysOnTop(true);
            Scene popupScene = new Scene(root);
            popupScene.setFill(Color.TRANSPARENT);
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

        // Atualiza posição do piece
        piece.setRow(newRow);
        piece.setCol(newCol);

        // Atualiza en passant
        if (piece instanceof Pawn && Math.abs(newRow - oldRow) == 2) {
            Pawn.enPassantVulnerablePawn = (Pawn) piece;
        } else {
            Pawn.enPassantVulnerablePawn = null;
        }

        // Marca que rei ou torre já mexeram
        if (piece instanceof King) {
            ((King) piece).hasMoved();
        }
        if (piece instanceof Rook) {
            ((Rook) piece).hasMoved();
        }

        // Atualiza UI da peça movida
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

    /**
     * Função que deteta cheque e cheque-mate.
     */
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

    /**
     * Calcula se o rei está em cheque-mate.
     *
     * @param color
     * @return
     */
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

    /**
     * Calcula se o rei está em cheque.
     *
     * @param color
     * @return
     */
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

    /**
     * Calcula se um movimento futuro deixaria o rei em cheque.
     *
     * @param p
     * @param dr
     * @param dc
     * @return
     */
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

    /**
     * Mostra um alterta com uma mensagem.
     *
     * @param txt
     */
    private void mostrarMensagem(String txt) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, txt, ButtonType.OK);
        a.setHeaderText(null);
        a.show();
    }

    /**
     * Define o fim de jogo com um alerta adequado e som.
     *
     * @param mensagem
     */
    private void fimDeJogo(String mensagem) {
        gameOver = true;
        if (timeline != null) {
            timeline.stop();
        }
        somFim = new Media(getClass().getResource("/resources/sound/game-end.mp3").toExternalForm());
        tocarSom(somFim);
        Alert a = new Alert(Alert.AlertType.INFORMATION, mensagem, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
        voltarAoMenu();
    }

    /**
     * Função para voltar à view anterior (menu inicial).
     */
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
    /**
     * Usado pelo menu para passar o valor do tema das peças escolhido.
     *
     * @param tema
     */
    public void receberTemaPecas(String tema) {
        this.temaPecas = tema;
    }

    /**
     * Usado pelo menu para passar o valor do tema do tabuleiro escolhido.
     *
     * @param tema
     */
    public void receberTemaTabuleiro(String tema) {
        this.temaTabuleiro = tema;
        aplicarTemaTabuleiro();
    }

    /**
     * Usado pelo menu para passar se a escolha do jogador for jogar vs IA.
     *
     * @param tema
     */
    public void jogarVSIA(Boolean res) {
        this.vsIa = res;
    }

    /**
     * Aplica o tema do tabuleiro (castanho, azul ou verde) escolhendo o css
     * adequado.
     */
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
        int melhorValor = Integer.MIN_VALUE;
        Movimento melhorMovimento = null;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && "black".equals(p.getColor())) {
                    for (int[] m : getLegalMoves(p)) {
                        int destinoR = m[0];
                        int destinoC = m[1];
                        Piece capturada = board[destinoR][destinoC];
                        int valor = valorPeca(capturada); // Valor da peça capturada, 0 se nenhuma

                        if (valor > melhorValor) {
                            melhorValor = valor;
                            melhorMovimento = new Movimento(p, destinoR, destinoC);
                        }
                    }
                }
            }
        }
        if (melhorMovimento == null) {
            fimDeJogo("Empate!");
            return;
        }
        clearHighlights();
        try {
            movePiece(melhorMovimento.p, melhorMovimento.r, melhorMovimento.c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        verificarEstadoJogo();
        mudarTurno();
    }

    @FXML
    private void handleDesistir(ActionEvent event) {
        if (gameOver) {
            return;
        }

        Button btn = (Button) event.getSource();
        String corDesistente = null;
        String corVencedor = null;

        if (btn.equals(btnResignBranco)) {
            corDesistente = "Branco";
            corVencedor = "Preto";
        } else if (btn.equals(btnResignPreto)) {
            corDesistente = "Preto";
            corVencedor = "Branco";
        }

        if (corDesistente != null) {
            final String corDesistenteFinal = corDesistente;
            final String corVencedorFinal = corVencedor;

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação de Desistência");
            alert.setHeaderText("O jogador " + corDesistenteFinal + " vai desistir.");
            alert.setContentText("Tem a certeza que deseja desistir do jogo?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    gameOver = true;
                    if (timeline != null) {
                        timeline.stop();
                    }

                    somFim = new Media(getClass().getResource("/resources/sound/game-end.mp3").toExternalForm());
                    tocarSom(somFim);
                    Alert aviso = new Alert(Alert.AlertType.INFORMATION);
                    aviso.setTitle("Fim de Jogo");
                    aviso.setHeaderText(null);
                    aviso.setContentText("Jogador " + corDesistenteFinal + " desistiu. Jogador " + corVencedorFinal + " vence!");
                    aviso.showAndWait();

                    voltarAoMenu();
                }
            });
        }
    }

    private int valorPeca(Piece p) {

        if (p == null) {
            return 0;
        }

        switch (p.getClass().getSimpleName()) {

            case "Queen":
                return 9;

            case "Rook":
                return 5;

            case "Bishop":
                return 3;

            case "Knight":
                return 3;

            case "Pawn":
                return 1;

            case "King":
                return 1000; // Rei

            default:
                return 0;

        }

    }
}
