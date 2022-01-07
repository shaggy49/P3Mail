package com.p3mail.application.client.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import com.p3mail.application.ClientMain;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ReplyController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField receiversField;

	@FXML
	private Button sendButton;

	@FXML
	void initialize() {

	}

	/*
	 * When send button or cancel buttons are clicked it changes controller and fxml file to MainWindowController and mainWindow.fxml.
	 */
	public void handleButtons(MouseEvent mouseEvent) throws IOException {
		Parent root = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("mainWindow.fxml"))) ;
		Scene scene = new Scene(root);
		Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		stage.setTitle("Email client");
		stage.setScene(scene);
		stage.show();
	}
}
