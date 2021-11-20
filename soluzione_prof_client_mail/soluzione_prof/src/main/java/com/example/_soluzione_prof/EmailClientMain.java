package com.example._soluzione_prof;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class EmailClientMain extends Application {

    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("client.fxml")));

        Scene scene = new Scene(root);

//        URL clientUrl = EmailClientMain.class.getResource("client.fxml");
//        FXMLLoader fxmlLoader = new FXMLLoader(clientUrl);
//        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Email client");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}