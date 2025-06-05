package controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    
    @FXML
    private void initialize() {
        // Preenche os combobox com os temas disponíveis
        comboBoxPecas.getItems().addAll("Normal", "Outline", "Wood");
        comboBoxTabuleiro.getItems().addAll("Blue", "Brown", "Green");
        // Opcional: definir valores padrão
        comboBoxPecas.getSelectionModel().selectFirst();
        comboBoxTabuleiro.getSelectionModel().selectFirst();
        // Definir ação dos botões
        btnAplicar.setOnAction(e -> aplicar());
        btnCancelar.setOnAction(e -> cancelar());
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
}
