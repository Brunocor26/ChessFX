/*
 * FXML Controller class para o menu principal do jogo de xadrez.
 * Controla a interação do menu, troca de temas, sons e navegação entre as telas.
 * 
 * @author bfc27
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;

/**
 * Controlador responsável pela interação com o menu principal, incluindo a navegação entre telas,
 * a aplicação de temas, e a execução de sons ao interagir com o menu.
 */
public class MenuController implements Initializable {

    // Elementos do FXML
    @FXML
    private Label arrow1;

    @FXML
    private Label arrow2;

    @FXML
    private Label arrow3;

    @FXML
    private Label arrow4;

    @FXML
    private HBox menuItem1;

    @FXML
    private HBox menuItem2;

    @FXML
    private HBox menuItem3;

    @FXML
    private HBox menuItem4;

    @FXML
    private ImageView imgReiPreto;

    @FXML
    private ImageView imgReiBranco;

    @FXML
    private Button ajuda;

    @FXML
    private Button jogarIA;

    @FXML
    private Button jogarLocal;

    @FXML
    private Button opcoes;

    @FXML
    private Button sair;

    @FXML
    private BorderPane menu;

    // Variáveis para armazenar temas de peças e tabuleiro
    private String temaPecas = "normal";
    private String temaTabuleiro = "Brown";

    // Sons do menu
    Media som = new Media(getClass().getResource("/resources/sound/trumpet.wav").toExternalForm());
    MediaPlayer player = new MediaPlayer(som);

    Media mudarMenu = new Media(getClass().getResource("/resources/sound/Retro11.wav").toExternalForm());
    MediaPlayer player2 = new MediaPlayer(mudarMenu);

    /**
     * Inicializa o controlador. Aplica o CSS e configura a interação com os itens de menu.
     * 
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Adiciona o CSS quando a cena for carregada
        menu.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                // Limpa e aplica o CSS
                newScene.getStylesheets().clear();
                newScene.getStylesheets().add(getClass().getResource("/resources/styles/styles.css").toExternalForm());
                imgReiPreto.setPreserveRatio(true);
                imgReiBranco.setPreserveRatio(true);
            }
        });

        // Configura a interação de mouse para os itens do menu com as setas
        setupMenuItemInteractions();
    }

    /**
     * Configura as interações de rato para os itens do menu, exibindo as setas ao passar.
     */
    private void setupMenuItemInteractions() {
        // Configura a interação com os itens de menu para mostrar as setas
        arrow1.setOpacity(0);
        menuItem1.setOnMouseEntered(e -> {
            arrow1.setOpacity(1);
            tocarSomMenu();
        });
        menuItem1.setOnMouseExited(e -> arrow1.setOpacity(0));

        arrow2.setOpacity(0);
        menuItem2.setOnMouseEntered(e -> {
            arrow2.setOpacity(1);
            tocarSomMenu();
        });
        menuItem2.setOnMouseExited(e -> arrow2.setOpacity(0));

        arrow3.setOpacity(0);
        menuItem3.setOnMouseEntered(e -> {
            arrow3.setOpacity(1);
            tocarSomMenu();
        });
        menuItem3.setOnMouseExited(e -> arrow3.setOpacity(0));

        arrow4.setOpacity(0);
        menuItem4.setOnMouseEntered(e -> {
            arrow4.setOpacity(1);
            tocarSomMenu();
        });
        menuItem4.setOnMouseExited(e -> arrow4.setOpacity(0));
    }

    /**
     * Toca o som ao passar o rato sobre os itens do menu.
     */
    private void tocarSomMenu() {
        if (player2 != null) {
            player2.stop();  // Para evitar som sobreposto
            player2.play();
        }
    }

    /**
     * Inicia o jogo contra a IA ao clicar no botão "Jogo vs IA".
     * 
     * @param event
     * @throws IOException Caso ocorra um erro ao carregar a nova janela.
     */
    @FXML
    public void handleJogarIA(ActionEvent event) throws IOException {
        player.play();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BoardView.fxml"));
        Parent root = loader.load();

        // Criar o novo stage (janela)
        Stage stage = new Stage();
        stage.setTitle("Jogo");
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/img/" + temaPecas.toLowerCase() + "/black_Rook.png")));
        BoardController controller = loader.getController();

        controller.receberTemaPecas(temaPecas);
        controller.receberTemaTabuleiro(temaTabuleiro);
        controller.jogarVSIA(true);
        controller.inicializarTabuleiro(); // Inicializa o tabuleiro com o tema e peças corretos

        // Fecha a janela atual
        Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stageAtual.close();

        // Mostra a nova janela
        stage.show();
    }

    /**
     * Inicia o jogo local ao clicar no botão "Jogo Local".
     * 
     * @param event
     * @throws
     */
    @FXML
    public void handleJogarLocal(ActionEvent event) throws IOException {
        player.play();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BoardView.fxml"));
        Parent root = loader.load();

        // Criar o novo stage (janela)
        Stage stage = new Stage();
        stage.setTitle("Jogo");
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/img/" + temaPecas.toLowerCase() + "/black_Rook.png")));
        BoardController controller = loader.getController();

        controller.receberTemaPecas(temaPecas);
        controller.receberTemaTabuleiro(temaTabuleiro);
        controller.inicializarTabuleiro(); // Inicializa o tabuleiro com o tema e peças corretos

        // Fecha a janela atual
        Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stageAtual.close();

        // Mostra a nova janela
        stage.show();
    }

    /**
     * Fecha a janela atual e termina a aplicação.
     * 
     * @param event
     */
    public void handleSair(ActionEvent event) {
        Stage stage = (Stage) sair.getScene().getWindow(); // Obtém a janela atual
        stage.close(); // Fecha a janela
    }

    /**
     * Abre a janela de opções para escolher o tema das peças e do tabuleiro.
     * 
     * @param event
     * @throws IOException Caso ocorra um erro ao carregar a janela de opções.
     */
    @FXML
    public void handleOpcoes(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Options.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Escolher Tema");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL);
        OptionsController controller = loader.getController();
        controller.setTemaPecasAtual(temaPecas);
        controller.setTemaTabuleiroAtual(temaTabuleiro);
        stage.showAndWait();
        
        // Atualizar tema com as escolhas do utilizador
        if (controller.getTemaPecasSelecionado() != null && controller.getTemaTabuleiroSelecionado() != null) {
            temaPecas = controller.getTemaPecasSelecionado();
            temaTabuleiro = controller.getTemaTabuleiroSelecionado();
            aplicarTemaPecas(temaPecas);
            aplicarTemaTabuleiro(temaTabuleiro);
        }
    }

    /**
     * Aplica o tema das peças selecionado pelo utilizador.
     * 
     * @param temaPeca O nome do tema das peças.
     */
    public void aplicarTemaPecas(String temaPeca) {
        temaPecas = temaPeca.toLowerCase();
        String base = "/resources/img/" + temaPeca.toLowerCase() + "/";
        String reiBrancoPath = base + "white_King.png";
        String reiPretoPath = base + "black_King.png";

        // Tenta carregar as imagens dos reis
        try {
            Image ReiBranco = new Image(getClass().getResourceAsStream(reiBrancoPath));
            imgReiPreto.setImage(ReiBranco);
        } catch (Exception e) {
            System.err.println("Erro ao carregar a imagem do Rei: " + reiBrancoPath);
            e.printStackTrace();
        }
        try {
            Image Rainha = new Image(getClass().getResourceAsStream(reiPretoPath));
            imgReiBranco.setImage(Rainha);
        } catch (Exception e) {
            System.err.println("Erro ao carregar a imagem da Rainha: " + reiPretoPath);
            e.printStackTrace();
        }
    }

    /**
     * Aplica o tema do tabuleiro selecionado pelo utilizador.
     * 
     * @param tema O nome do tema do tabuleiro.
     */
    public void aplicarTemaTabuleiro(String tema) {
        switch (tema) {
            case "Blue":
                menu.getScene().getStylesheets().clear();
                menu.getScene().getStylesheets().add(getClass().getResource("/resources/styles/styles_blue.css").toExternalForm());
                break;

            case "Brown":
                menu.getScene().getStylesheets().clear();
                menu.getScene().getStylesheets().add(getClass().getResource("/resources/styles/styles.css").toExternalForm());
                break;

            case "Green":
                menu.getScene().getStylesheets().clear();
                menu.getScene().getStylesheets().add(getClass().getResource("/resources/styles/styles_green.css").toExternalForm());
                break;
        }
    }

    // Getters para aceder a temas de peças e tabuleiro
    public String getTemaPecas() {
        return temaPecas;
    }

    public String getTemaTabuleiro() {
        return temaTabuleiro;
    }
}
