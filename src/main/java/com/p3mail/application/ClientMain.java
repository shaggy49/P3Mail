package com.p3mail.application;

import com.p3mail.application.client.controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientMain extends Application {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Email client");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        LoginController loginController = fxmlLoader.getController();
        loginController.closeSocketConnection();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}