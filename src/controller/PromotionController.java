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
        if (stage != null) stage.close();
    }
    @FXML
    public void handleBtnQueen(ActionEvent action) {
        pecaEscolhida = "Queen";
        if (stage != null) stage.close();
    }
    @FXML
    public void handleBtnBishop(ActionEvent action) {
        pecaEscolhida = "Bishop";
        if (stage != null) stage.close();
    }
    @FXML
    public void handleBtnKnight(ActionEvent action) {
        pecaEscolhida = "Knight";
        if (stage != null) stage.close();
    }

    // Para devolver a escolha ao chamador
    public String getPecaEscolhida() {
        return pecaEscolhida;
    }
}