package com.p3mail.application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/*
 * This class must implement Initializable class beacause we want to pass a Boolean value
 * between this class and MainWindowController class. This is necessary to set
 * the object field ad not editable in case of an email forwarding.
 */
public class NewMessageController implements Initializable {

	@FXML
	private TextField objectField;

	@FXML
	private TextField receiversField;

	@FXML
	private Button sendButton;

	@FXML
	private Label moreRecipientsLabel;


	@FXML
	void initialize (Boolean isNewMessage){
		System.out.println(isNewMessage);

		assert objectField != null : "fx:id=\"objectField\" was not injected: check your FXML file 'newMessage.fxml'.";
		assert receiversField != null : "fx:id=\"receiversField\" was not injected: check your FXML file 'newMessage.fxml'.";
		assert sendButton != null : "fx:id=\"sendButton\" was not injected: check your FXML file 'newMessage.fxml'.";

		moreRecipientsLabel.setVisible(false);

		if(!isNewMessage)
			objectField.setEditable(false);
	}

	/*
	 * When send button or cancel buttons are clicked it changes controller and fxml file to MainWindowController and mainWindow.fxml.
	 */
	public void handleButton(MouseEvent mouseEvent) throws IOException {
		Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("mainWindow.fxml"))) ;
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

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}
}
