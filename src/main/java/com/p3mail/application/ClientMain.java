package com.p3mail.application;

import com.p3mail.application.client.controller.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ClientMain extends Application {


    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        //Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        Scene scene = new Scene(root);
        stage.setTitle("Email client");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        ClientController clientController = (ClientController) fxmlLoader.getController();
        clientController.closeSocketConnection();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}