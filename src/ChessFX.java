/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author bfc27
 */
public class ChessFX extends Application {
     @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/Menu.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/img/outline/black_Rook.png")));
        primaryStage.setTitle("Xadrez em JavaFX");
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
