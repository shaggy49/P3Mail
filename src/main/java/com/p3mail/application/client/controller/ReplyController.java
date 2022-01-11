package com.p3mail.application.client.controller;

import java.io.IOException;
import com.p3mail.application.ClientMain;
import com.p3mail.application.client.model.Client;
import com.p3mail.application.client.model.Email;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ReplyController {
	@FXML
	private Label receiversLbl;

	@FXML
	private TextField receiversField;

	@FXML
	private  TextArea sendText;

	@FXML
	private TextArea receivedText;

	@FXML
	private Button sendButton;

	private Client model;
	private Email email;

	/**
	 * This method takes the model as parameter to save information about model.
	 *
	 * @param isReplyAll
	 * @param model
	 * @param email
	 */
	@FXML
	void initialize(Boolean isReplyAll, Client model, Email email) {
		if(isReplyAll) {
			receiversLbl.setText("Destinatari:");
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 1; i < email.getReceivers().size(); i++) {
				stringBuilder.append(", ").append(email.getReceivers().get(i));
			}
			receiversField.setText(email.getSender() + stringBuilder.toString());
		}
		else {
			receiversLbl.setText("Destinatario:");
			receiversField.setText(email.getSender());
		}
		receivedText.setText("Data email ricevuta: " + email.getDate() + '\n' + "Oggetto: " + email.getObject() + '\n' + "Contenuto: " + email.getText());
		this.model = model;
		this.email = email;
	}


	/**
	 * When send button is clicked if sendText area is empty it emits alert,
	 * else it changes controller and fxml file to MainWindowController and mainWindow.fxml.
	 * When cancel buttons is clicked it changes controller and fxml file to MainWindowController
	 * and mainWindow.fxml.
	 */
	public void handleButtons(MouseEvent mouseEvent) throws IOException {
		Button b = (Button) mouseEvent.getSource();
		if(b.getId().equals("sendButton") && sendText.getText().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Inserisci un messaggio di risposta per inviare la mail");
			alert.showAndWait();
		} else {
			FXMLLoader loader = new FXMLLoader((ClientMain.class.getResource("mainWindow.fxml")));
			Parent root = (Parent) loader.load();
			MainWindowController newMainWindowController = loader.getController();
			newMainWindowController.initialize(model);

			Scene scene = new Scene(root);
			Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
			stage.setTitle("Email client");
			stage.setScene(scene);
			stage.show();
		}
	}
}
