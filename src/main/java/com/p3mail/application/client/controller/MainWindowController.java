package com.p3mail.application.client.controller;

import com.p3mail.application.ClientMain;
import com.p3mail.application.client.ClientListener;
import com.p3mail.application.connection.model.Email;
import com.p3mail.application.connection.request.DeleteRequest;
import com.p3mail.application.connection.request.DisconnectRequest;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import com.p3mail.application.client.model.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class MainWindowController {
	Socket socketConnection = null;
	ObjectOutputStream out = null;

	@FXML
	private ImageView imgIcon;

	@FXML
	private Label lblNameAndSurname;

	@FXML
	private Label lblSurname;

	@FXML
	private Label lblEmailAddress;

	@FXML
	private Label lblFrom;

	@FXML
	private Label lblObject;

	@FXML
	private Label lblTo;

	@FXML
	private Label lblDate;

	@FXML
	private TextArea txtEmailContent;

	@FXML
	private ListView<Email> lstEmails;

	@FXML
	private BorderPane pnlEmailList;

	@FXML
	private BorderPane pnlReadMessage;

	@FXML
	private Button reply;

	@FXML
	private Button replyAll;

	private Email selectedEmail;
	private Email emptyEmail;
	private Client model;

	@FXML
	public void initialize(boolean firstTime, Client model, Socket socketConnection, ObjectOutputStream out) {
		this.model = model;
		this.socketConnection = socketConnection;
		this.out = out;

		selectedEmail = null;

		//binding tra lstEmails e inboxProperty
		lblNameAndSurname.setText(model.nameProperty().get() + " " + model.surnameProperty().get());
		lblEmailAddress.textProperty().bind(model.emailAddressProperty());
		lstEmails.itemsProperty().bind(model.inboxProperty());

		if(firstTime) {
			ClientListener clientListener = null;
			try {
				clientListener = new ClientListener(this, socketConnection);
			} catch (IOException e) {
				e.printStackTrace();
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Connection failed");
				alert.show();
			}
			new Thread(clientListener).start();
		}

		emptyEmail = new Email("", List.of(""), "", "", null);

		updateDetailView(emptyEmail);

	}

	/**
	 * Mostra la mail selezionata nella vista
	 */
	@FXML
	protected void showSelectedEmail(MouseEvent mouseClick) {
		Email email = (Email) lstEmails.getSelectionModel().getSelectedItem();

		selectedEmail = email;

		if (mouseClick.getClickCount() == 2)
			updateDetailView(email);
	}

	/**
	 * This method updates view with selected email.
	 */
	protected void updateDetailView(Email email) {
		if (email != null) {
			lblFrom.setText(email.getSender());
			lblTo.setText(String.join(", ", email.getReceivers()));
			lblObject.setText(email.getObject());
			if(email.getDate() == null)
				lblDate.setText("");
			else
				lblDate.setText(email.getDate().toString());
			txtEmailContent.setText(email.getText());
		}
	}

	public void addEmailToInbox(Email email) {
		model.addEmail(email);
	}

	public void deleteAndUpdateView() {
		model.deleteEmail(selectedEmail); //do this only if server says that all works fine!
		updateDetailView(emptyEmail);
	}

	public void deleteAndUpdateView(Email email) {
		model.deleteEmail(email.getId()); //do this only if server says that all works fine!
		updateDetailView(emptyEmail);
	}

	/**
	 * When a write button is clicked it changes controller and fxml file to
	 * NewMessageController and newMessageController.fxml.
	 * This method pass a Boolean value to NewMessageController class.
	 * This is necessary to set the object field ad not editable in case of
	 * an email forwarding.
	 */
	public void handleWriteButton(MouseEvent mouseEvent) throws IOException {
		FXMLLoader loader = new FXMLLoader((ClientMain.class.getResource("newMessage.fxml")));
		Parent root = (Parent) loader.load();
		NewMessageController newMsgController = loader.getController();
		newMsgController.initialize(true, model, selectedEmail, socketConnection, out);

		Scene scene = new Scene(root);
		Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		stage.setTitle("Nuovo messaggio");
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * When a reply button or replyAll are clicked it changes controller
	 * and fxml file to ReplyController and reply.fxml.
	 */
	public void handleRepliesButton(MouseEvent mouseEvent) throws IOException {
		if (selectedEmail == null)
			errorDialog();
		else {
			FXMLLoader loader = new FXMLLoader((ClientMain.class.getResource("reply.fxml")));
			Parent root = (Parent) loader.load();
			ReplyController newReplyController = loader.getController();
			Button b = (Button) mouseEvent.getSource();
			if (b.getId().equals("reply"))
				newReplyController.initialize(false, model, selectedEmail, socketConnection, out);
			else
				newReplyController.initialize(true, model, selectedEmail, socketConnection, out);

			Scene scene = new Scene(root);
			Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
			stage.setTitle("Rispondi al messaggio");
			stage.setScene(scene);
			stage.show();
		}
	}

	/**
	 * When a forward button is clicked it changes controller and fxml file
	 * to NewMessageController and newMessage.fxml.
	 * This method pass a Boolean value to NewMessageController class.
	 * This is necessary to set the object field ad not editable in case of
	 * an email forwarding.
	 */
	public void handleForwardButton(MouseEvent mouseEvent) throws IOException {
		if (selectedEmail == null)
			errorDialog();
		else {
			FXMLLoader loader = new FXMLLoader((ClientMain.class.getResource("newMessage.fxml")));
			Parent root = (Parent) loader.load();

			NewMessageController newMsgController = loader.getController();
			newMsgController.initialize(false, model, selectedEmail, socketConnection, out);

			Scene scene = new Scene(root);
			Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
			stage.setTitle("Inoltra mail");
			stage.setScene(scene);
			stage.show();
		}
	}

	/**
	 * When delete button is clicked the email that is open is deleted.
	 */
	public void handleDeleteButton(MouseEvent mouseEvent) {
		if (selectedEmail == null) {
			errorDialog();
		}
		else if (confirmDialog()) {
//			System.out.println("You want to delete the email with id = " + selectedEmail.getId()); //debug purpose
			if(socketConnection != null) {
				try {
					out.writeObject(new DeleteRequest(selectedEmail));
				} catch (IOException e) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("The server doesn't seem connected!");
					alert.show();
					e.printStackTrace();
				}
			}
		}
	}

	public void setSocketConnection(Socket socketConnection) {
		this.socketConnection = socketConnection;
	}

	public void setOut(ObjectOutputStream out) {
		this.out = out;
	}

	public String getEmailAddress() {
		return model.emailAddressProperty().get();
	}

	public void closeSocketConnection() {
		try {
			if(out != null) {
				out.writeObject(new DisconnectRequest());
			}
//			if(socketConnection != null) {
//				socketConnection.close();
//				System.out.println("Connessione terminata");
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Error dialog.
	 */
	private void errorDialog() {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setHeaderText("Seleziona una mail prima di proseguire");
		alert.showAndWait();
	}

	/**
	 * Confirmation dialog.
	 */
	private boolean confirmDialog() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
		alert.setHeaderText("Sei sicuro di voler eliminare la mail selezionata?");
		alert.showAndWait();
		return alert.getResult() == ButtonType.YES;
	}
}
