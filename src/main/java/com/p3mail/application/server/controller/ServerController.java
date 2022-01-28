package com.p3mail.application.server.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 * 'server.fxml' Controller Class
 */
public class ServerController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea txtArea;

    @FXML
    void initialize() {
        // redirected to textArea on GUI
        txtArea.setText("Server loading...\n");
    }

    public void printToTextArea(String infoToPrint) {
        txtArea.appendText(infoToPrint + "\n");
    }

}
