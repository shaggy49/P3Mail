package com.p3mail.application.client.controller;

import com.p3mail.application.ClientMain;
import com.p3mail.application.client.model.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NewMessageController {

	@FXML
	private TextField objectField;

	@FXML
	private TextField receiversField;

	@FXML
	private Button sendButton;

	@FXML
	private Label moreRecipientsLabel;

	private Client model;
	private List<String> receivers;
	private boolean alreadyChecked;
	private final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
			"[a-zA-Z0-9_+&*-]+)*@" +
			"(?:[a-zA-Z0-9-]+\\.)+[a-z" +
			"A-Z]{2,7}$";
	// private final String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
	//        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

	/**
	 * It takes a Boolean value from MainWindowController class. This is necessary to set
	 * the object field ad not editable in case of an email forwarding.
	 * @param isNewMessage
	 * @param model
	 */
	@FXML
	public void initialize (Boolean isNewMessage, Client model){
		this.model = model;
		receivers = new ArrayList<>();
		alreadyChecked = true;
		moreRecipientsLabel.setVisible(false);

		if(!isNewMessage)
			objectField.setEditable(false);
	}

	/**
	 * This method checks if all emails in list are syntactically correct
	 * by calling isValid(email) method on each email of list.
	 */
	private boolean emailsSyntaxIsCorrect(){
		if(!alreadyChecked) {
			boolean valid = true;
			String allRecipients = receiversField.getText();
			receivers = List.of(allRecipients.split(" "));
			for (String rec : receivers) {
				valid = valid && isValid(rec);
			}
			alreadyChecked = valid;
		}
		return alreadyChecked;
	}

	/**
	 * This method checks if an email is syntactically correct.
	 * @param email email to check
	 */
	private boolean isValid(String email){
		Pattern pat = Pattern.compile(emailRegex);
		if (email == null)
			return false;
		return pat.matcher(email).matches();
	}


	private void recipientsEmailWrong(){
		moreRecipientsLabel.setText("Sintassi email errata!");
		moreRecipientsLabel.setTextFill(Color.web("#ff0000ff"));
		moreRecipientsLabel.setVisible(true);
	}

	/**
	 * This methods checks if recipients are correct.
	 *
	 * @param keyEvent
	 */
	public void handleCheckIfTextChanged(MouseEvent keyEvent) {
		if (!(receiversField.getText().isEmpty() || emailsSyntaxIsCorrect()))
			System.out.println("inserisci giusto furbetto!");
	}

	/**
	 * When text in recipients changes alreadyChecked variable is set to false.
	 *
	 * @param keyEvent
	 */
	public void handleRecipientsChanged(KeyEvent keyEvent) {
		alreadyChecked = false;
		if(receiversField.getText().isEmpty())
			alreadyChecked = true;
	}

	/**
	 * When newRecipients button is clicked, the moreRecipientsLabel is set as visible.
	 * The label disappears with a transition thanks to java FadeTransition class.
	 *
	 * @param mouseEvent
	 */
	public void handleNewRecipients(MouseEvent mouseEvent) {
		if(emailsSyntaxIsCorrect()) {
			moreRecipientsLabel.setText("Inserisci uno spazio tra le mail se vuoi aggiungere più destinatari");
			moreRecipientsLabel.setTextFill(Color.web("#06bf9d"));
			moreRecipientsLabel.setVisible(true);
			FadeTransition fadeOut = new FadeTransition(Duration.millis(8000), moreRecipientsLabel);
			fadeOut.setFromValue(2);
			fadeOut.setToValue(0);
			fadeOut.play();
		}
		else
			System.out.println("inserisci giusto furbetto!");
	}

	/**
	 * When send button is clicked it changes controller and fxml file to MainWindowController and mainWindow.fxml.
	 *
	 * @param mouseEvent
	 */
	public void handleSendButton(MouseEvent mouseEvent) throws IOException {
		if(!receiversField.getText().isEmpty() && emailsSyntaxIsCorrect()){
			FXMLLoader loader = new FXMLLoader((ClientMain.class.getResource("mainWindow.fxml"))) ;
			Parent root = (Parent) loader.load();
			MainWindowController newMainWindowController = loader.getController();
			newMainWindowController.initialize(model);

			Scene scene = new Scene(root);
			Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
			stage.setTitle("Email client");
			stage.setScene(scene);
			stage.show();
		}
		else
			System.out.println("inserisci giusto furbetto!");
	}

	/**
	 * When cancel button is clicked it changes controller and fxml file to MainWindowController and mainWindow.fxml.
	 *
	 * @param mouseEvent
	 */
	public void handleCancelButton(MouseEvent mouseEvent) throws IOException {
		FXMLLoader loader = new FXMLLoader((ClientMain.class.getResource("mainWindow.fxml"))) ;
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
