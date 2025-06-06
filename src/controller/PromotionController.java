package controller;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
public class PromotionController implements Initializable {
    
    @FXML
    private BorderPane rootPane; 
    
    @FXML
    private Button btnQueen;
    
    @FXML
    private Button btnRook;
    
    @FXML
    private Button btnKnight;
    
    @FXML
    private Button btnBishop;
    
    @FXML
    private ImageView bishop;
    @FXML
    private ImageView knight;
    @FXML
    private ImageView queen;
    @FXML
    private ImageView rook;

    private String temaPeca;
    private String corPeca;
    private String pecaEscolhida = "Queen"; // Valor padrão
    private Stage stage;
    
    private Media somPromocao=new Media(getClass().getResource("/resources/sound/promote.mp3").toExternalForm());

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> rootPane.requestFocus());
    }

    // Para o main passar o stage deste popup
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // Receber o tema e a cor das peças
    public void receberTemaPeca(String temaPeca, String corPeca) {
        this.temaPeca = temaPeca;
        this.corPeca = corPeca;
        atualizarImagens();
    }

    private void atualizarImagens() {
        bishop.setImage(new Image(getClass().getResourceAsStream("/resources/img/" + temaPeca + "/" + corPeca + "_Bishop.png")));
        knight.setImage(new Image(getClass().getResourceAsStream("/resources/img/" + temaPeca + "/" + corPeca + "_Knight.png")));
        rook.setImage(new Image(getClass().getResourceAsStream("/resources/img/" + temaPeca + "/" + corPeca + "_Rook.png")));
        queen.setImage(new Image(getClass().getResourceAsStream("/resources/img/" + temaPeca + "/" + corPeca + "_Queen.png")));
    }

    // Cada botão fecha o popup e regista a escolha
    @FXML
    public void handleBtnRook(ActionEvent action) {
        pecaEscolhida = "Rook";
        tocarSom(somPromocao);
        if (stage != null) stage.close();
    }
    @FXML
    public void handleBtnQueen(ActionEvent action) {
        pecaEscolhida = "Queen";
        tocarSom(somPromocao);
        if (stage != null) stage.close();
    }
    @FXML
    public void handleBtnBishop(ActionEvent action) {
        pecaEscolhida = "Bishop";
        tocarSom(somPromocao);
        if (stage != null) stage.close();
    }
    @FXML
    public void handleBtnKnight(ActionEvent action) {
        pecaEscolhida = "Knight";
        tocarSom(somPromocao);
        if (stage != null) stage.close();
    }

    // Para devolver a escolha ao chamador
    public String getPecaEscolhida() {
        return pecaEscolhida;
    }
    
        /**
     * Função para tocar som a partir de uma "media".
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
}