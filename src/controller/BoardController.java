package controller;

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
import javafx.stage.Stage;
import models.*;

import java.net.URL;
import java.util.*;

public class BoardController implements Initializable {

    /* ---------- FXML ---------- */
    @FXML private BorderPane pane;
    @FXML private GridPane   boardGrid;
    @FXML private Label      turnoLabel;

    /* ---------- Estado ---------- */
    private final Piece[][] board = new Piece[8][8];
    private Piece   selectedPiece = null;
    private final List<StackPane> highlightedCells = new ArrayList<>();

    private String  temaPecas      = "normal";
    private String  temaTabuleiro  = "Brown";
    private boolean turnoBranco    = true;
    private boolean vsIa           = false;
    private boolean gameOver       = false;

    /* ---------- Som ---------- */
    private Media somMexer;
    private Media somCaptura;

    /* ---------- INIT ---------- */
    @Override public void initialize(URL url, ResourceBundle rb) {
        try {
            somMexer   = new Media(getClass().getResource("/resources/sound/moving.mp3").toExternalForm());
            somCaptura = new Media(getClass().getResource("/resources/sound/capture.mp3").toExternalForm());
        } catch (Exception e) { System.err.println("Erro sons: "+e.getMessage()); }

        pane.sceneProperty().addListener((obs,o,n) -> { if (n!=null) aplicarTemaTabuleiro(); });
    }

    /* ---------- Setup Peças ---------- */
    public void inicializarTabuleiro() {
        String[] cor = {"white","black"};
        for (int c=0;c<8;c++){
            addPiece(new Pawn(cor[0],6,c));
            addPiece(new Pawn(cor[1],1,c));
        }
        addPiece(new Rook  (cor[1],0,0)); addPiece(new Rook  (cor[1],0,7));
        addPiece(new Rook  (cor[0],7,0)); addPiece(new Rook  (cor[0],7,7));
        addPiece(new Knight(cor[1],0,1)); addPiece(new Knight(cor[1],0,6));
        addPiece(new Knight(cor[0],7,1)); addPiece(new Knight(cor[0],7,6));
        addPiece(new Bishop(cor[1],0,2)); addPiece(new Bishop(cor[1],0,5));
        addPiece(new Bishop(cor[0],7,2)); addPiece(new Bishop(cor[0],7,5));
        addPiece(new Queen (cor[1],0,3)); addPiece(new Queen (cor[0],7,3));
        addPiece(new King  (cor[1],0,4)); addPiece(new King  (cor[0],7,4));
    }

    private void addPiece(Piece p){
        board[p.getRow()][p.getCol()] = p;
        String img="/resources/img/"+temaPecas.toLowerCase()+"/"+p.getImageName();
        ImageView iv=new ImageView(new Image(getClass().getResourceAsStream(img)));
        iv.setFitWidth(60); iv.setFitHeight(60); iv.setPreserveRatio(true);
        StackPane cell=(StackPane) boardGrid.getChildren().get(p.getRow()*8+p.getCol());
        cell.getChildren().add(iv);
    }

    /* ---------- Clique ---------- */
    @FXML private void onCellClicked(MouseEvent e){
        if(gameOver) return;

        Node n=(Node)e.getTarget();
        while(n!=null && !(n instanceof StackPane)) n=n.getParent();
        if(n==null) return;

        int idx=boardGrid.getChildren().indexOf(n);
        int r=idx/8, c=idx%8;
        Piece p=board[r][c];

        if(p!=null && p.getColor().equals(turnoBranco?"white":"black")){
            selectedPiece=p;
            showValidMoves();
            return;
        }

        if(selectedPiece!=null){
            for(int[] m:selectedPiece.getValidMoves(board))
                if(m[0]==r && m[1]==c){
                    clearHighlights();
                    movePiece(selectedPiece,r,c);
                    verificarEstadoJogo();
                    mudarTurno();
                    selectedPiece=null;
                    return;
                }
        }
    }

    /* ---------- Destaques ---------- */
    private void showValidMoves(){
        clearHighlights();
        if(selectedPiece==null) return;

        for(int[] m:selectedPiece.getValidMoves(board)){
            StackPane cell=(StackPane) boardGrid.getChildren().get(m[0]*8+m[1]);
            Rectangle r=new Rectangle(cell.getWidth(),cell.getHeight());
            r.setFill(Color.web("#ffff00",0.45));
            r.setMouseTransparent(true);
            r.getStyleClass().add("highlight");
            cell.getChildren().add(r);
            highlightedCells.add(cell);
        }
    }
    private void clearHighlights(){
        for(StackPane c:highlightedCells)
            c.getChildren().removeIf(node -> node instanceof Rectangle &&
                                             node.getStyleClass().contains("highlight"));
        highlightedCells.clear();
    }

    /* ---------- Movimento ---------- */
    private void movePiece(Piece p,int r,int c){
        tocarSom(board[r][c]==null?somMexer:somCaptura);

        StackPane oldCell=(StackPane) boardGrid.getChildren().get(p.getRow()*8+p.getCol());
        oldCell.getChildren().removeIf(node -> node instanceof ImageView);

        StackPane newCell=(StackPane) boardGrid.getChildren().get(r*8+c);
        newCell.getChildren().removeIf(node -> node instanceof ImageView);

        board[p.getRow()][p.getCol()] = null;
        p.setRow(r); p.setCol(c);
        board[r][c] = p;

        // Promoção?
        if(p instanceof Pawn && (r==0 || r==7))
            promoverPeao((Pawn)p);

        String img="/resources/img/"+temaPecas.toLowerCase()+"/"+p.getImageName();
        ImageView iv=new ImageView(new Image(getClass().getResourceAsStream(img)));
        iv.setFitWidth(60); iv.setFitHeight(60); iv.setPreserveRatio(true);
        newCell.getChildren().add(iv);
    }

    /* ---------- Promoção ---------- */
    private void promoverPeao(Pawn pawn){
        Piece nova;
        if(vsIa && "black".equals(pawn.getColor())){           // IA – sempre rainha
            nova=new Queen(pawn.getColor(),pawn.getRow(),pawn.getCol());
        }else{
            List<String> opcoes=List.of("Queen","Rook","Bishop","Knight");
            ChoiceDialog<String> dlg=new ChoiceDialog<>("Queen",opcoes);
            dlg.setTitle("Promoção de Peão");
            dlg.setHeaderText("Escolha a peça:");
            String escolha=dlg.showAndWait().orElse("Queen");
            nova = switch(escolha){
                case "Rook"   -> new Rook  (pawn.getColor(),pawn.getRow(),pawn.getCol());
                case "Bishop" -> new Bishop(pawn.getColor(),pawn.getRow(),pawn.getCol());
                case "Knight" -> new Knight(pawn.getColor(),pawn.getRow(),pawn.getCol());
                default       -> new Queen (pawn.getColor(),pawn.getRow(),pawn.getCol());
            };
        }
        addPiece(nova);
    }

    /* ---------- Verificar fim ---------- */
    private void verificarEstadoJogo(){
        String corOponente = turnoBranco ? "black" : "white";

        if (reiEmCheck(corOponente)) {
            if (estaEmCheckMate(corOponente)) {
                fimDeJogo("Xeque-mate! " + (turnoBranco ? "Branco" : "Preto") + " vence.");
            } else {
                mostrarMensagem("Xeque ao " + (corOponente.equals("white") ? "Branco" : "Preto") + "!");
            }
        }
    }

    private boolean estaEmCheckMate(String color){
        if(!reiEmCheck(color)) return false;
        for(int r=0;r<8;r++)
            for(int c=0;c<8;c++){
                Piece p=board[r][c];
                if(p!=null && p.getColor().equals(color))
                    for(int[] m:p.getValidMoves(board))
                        if(!deixaReiEmCheck(p,m[0],m[1])) return false;
            }
        return true;
    }

    private boolean reiEmCheck(String color){
        int kr = -1, kc = -1;

        /* procura o rei dessa cor */
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++)
                if (board[r][c] instanceof King) {
                    King k = (King) board[r][c];
                    if (k.getColor().equals(color)) { kr = r; kc = c; }
                }

        if (kr == -1) return true;              // rei desapareceu → já seria mate

        /* vê se alguma peça inimiga o ataca */
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p != null && !p.getColor().equals(color))
                    for (int[] m : p.getValidMoves(board))
                        if (m[0] == kr && m[1] == kc) return true;
            }
        return false;
    }

    private boolean deixaReiEmCheck(Piece p,int dr,int dc){
        Piece cap=board[dr][dc];
        int or=p.getRow(), oc=p.getCol();
        board[or][oc]=null; board[dr][dc]=p;
        p.setRow(dr); p.setCol(dc);
        boolean check=reiEmCheck(p.getColor());
        p.setRow(or); p.setCol(oc);
        board[or][oc]=p; board[dr][dc]=cap;
        return check;
    }

    /* ---------- UI util ---------- */
    private void tocarSom(Media m){
        if(m==null) return;
        MediaPlayer pl=new MediaPlayer(m);
        pl.play();
        pl.setOnEndOfMedia(pl::dispose);
    }

    private void mostrarMensagem(String txt){
        Alert a = new Alert(Alert.AlertType.INFORMATION, txt, ButtonType.OK);
        a.setHeaderText(null);
        a.show();
    }

    private void fimDeJogo(String mensagem){
        gameOver=true;
        Alert a=new Alert(Alert.AlertType.INFORMATION,mensagem,ButtonType.OK);
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

    /* ---------- Tema / opções vindas do menu ---------- */
    public void receberTemaPecas(String t){ temaPecas=t; }
    public void receberTemaTabuleiro(String t){ temaTabuleiro=t; aplicarTemaTabuleiro(); }

    private void aplicarTemaTabuleiro(){
        try{
            String css="board";
            if("Blue".equalsIgnoreCase(temaTabuleiro))  css="board_blue";
            if("Green".equalsIgnoreCase(temaTabuleiro)) css="board_green";

            if(pane.getScene()!=null){
                pane.getScene().getStylesheets().clear();
                pane.getScene().getStylesheets().add(
                    Objects.requireNonNull(
                        getClass().getResource("/resources/styles/"+css+".css"))
                        .toExternalForm());
            }
        }catch(Exception ex){ System.err.println("Tema tabuleiro: "+ex.getMessage()); }
    }

    /* ---------- Turnos & IA ---------- */
    public void jogarVSIA(boolean b){ vsIa=b; }

    public void mudarTurno(){
        turnoBranco=!turnoBranco;
        turnoLabel.setText(turnoBranco?"Turno: Branco":"Turno: Preto");

        if(vsIa && !turnoBranco && !gameOver){
            new Thread(()->{
                try{Thread.sleep(500);}catch(Exception ignored){}
                javafx.application.Platform.runLater(this::jogadaIA);
            }).start();
        }
    }

    private static class Movimento{
        Piece p; int r,c;
        Movimento(Piece p,int r,int c){ this.p=p; this.r=r; this.c=c; }
    }

    private void jogadaIA(){
        List<Movimento> lista=new ArrayList<>();
        for(int r=0;r<8;r++)
            for(int c=0;c<8;c++){
                Piece p=board[r][c];
                if(p!=null && "black".equals(p.getColor()))
                    for(int[] m:p.getValidMoves(board))
                        lista.add(new Movimento(p,m[0],m[1]));
            }

        if(lista.isEmpty()){ fimDeJogo("Empate!"); return; }

        Movimento esc=lista.get(new Random().nextInt(lista.size()));
        clearHighlights();
        movePiece(esc.p,esc.r,esc.c);
        verificarEstadoJogo();
        mudarTurno();
    }
}
