package controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class OptionsController {
    @FXML
    private ComboBox<String> comboBoxPecas;
    @FXML
    private ComboBox<String> comboBoxTabuleiro;
    @FXML
    private Button btnAplicar;
    @FXML
    private Button btnCancelar;
    private String temaPecasSelecionado;
    private String temaTabuleiroSelecionado;
    
    private Media som=new Media(getClass().getResource("/resources/sound/noti.wav").toExternalForm());

    @FXML
    private void initialize() {
        // Preenche os combobox com os temas disponíveis
        comboBoxPecas.getItems().addAll("Normal", "Outline", "Wood");
        comboBoxTabuleiro.getItems().addAll("Blue", "Brown", "Green");
        // Definir valores padrão
        comboBoxPecas.getSelectionModel().selectFirst();
        comboBoxTabuleiro.getSelectionModel().selectFirst();
        // Definir ação dos botões
        btnAplicar.setOnAction(e -> aplicar());
        btnCancelar.setOnAction(e -> cancelar());
        tocarSom(som);
    }
    public void setTemaPecasAtual(String tema) {
        comboBoxPecas.getSelectionModel().select(tema);
    }
    public void setTemaTabuleiroAtual(String tema) {
        comboBoxTabuleiro.getSelectionModel().select(tema);
    }
    private void aplicar() {
        temaPecasSelecionado = comboBoxPecas.getSelectionModel().getSelectedItem();
        temaTabuleiroSelecionado = comboBoxTabuleiro.getSelectionModel().getSelectedItem();
        // Fecha a janela
        Stage stage = (Stage) btnAplicar.getScene().getWindow();
        stage.close();
        tocarSom(som);
    }
    private void cancelar() {
        // Fecha a janela sem alterar nada
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
    public String getTemaPecasSelecionado() {
        return temaPecasSelecionado;
    }
    public String getTemaTabuleiroSelecionado() {
        return temaTabuleiroSelecionado;
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
