package com.p3mail.application.client.controller;

import com.p3mail.application.ClientMain;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class NewMessageController {

	@FXML
	private TextField objectField;

	@FXML
	private TextField receiversField;

	@FXML
	private Button sendButton;

	@FXML
	private Label moreRecipientsLabel;

	/*
	 * It takes a Boolean value from MainWindowController class. This is necessary to set
	 * the object field ad not editable in case of an email forwarding.
	 */
	@FXML
	public void initialize (Boolean isNewMessage){
		System.out.println(isNewMessage);

		moreRecipientsLabel.setVisible(false);

		if(!isNewMessage)
			objectField.setEditable(false);
	}

	/*
	 * When send button or cancel buttons are clicked it changes controller and fxml file to MainWindowController and mainWindow.fxml.
	 */
	public void handleButton(MouseEvent mouseEvent) throws IOException {
		Parent root = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("mainWindow.fxml"))) ;
		Scene scene = new Scene(root);
		Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		stage.setTitle("Email client");
		stage.setScene(scene);
		stage.show();
	}

	/*
	 * When newRecipients button is clicked, the moreRecipientsLabel is set as visible.
	 * The label disappears with a transition thanks to java FadeTransition class.
	 */
	public void handleNewRecipients(MouseEvent mouseEvent) {
		moreRecipientsLabel.setVisible(true);

		FadeTransition fadeOut = new FadeTransition(Duration.millis(8000), moreRecipientsLabel);
		fadeOut.setFromValue(2);
		fadeOut.setToValue(0);
		fadeOut.play();
	}
}
