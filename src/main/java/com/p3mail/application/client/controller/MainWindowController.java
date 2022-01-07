package com.p3mail.application.client.controller;

import com.p3mail.application.ClientMain;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import com.p3mail.application.client.model.Client;
import com.p3mail.application.client.model.Email;
import com.p3mail.application.server.MailNotFoundException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class MainWindowController {
	@FXML
	private ImageView imgIcon;

	@FXML
	private Label lblEmailAddress;

	@FXML
	private Label lblEmailAddress1;

	@FXML
	private Label lblEmailAddress11;

	@FXML
	private Label lblFrom;

	@FXML
	private Label lblObject;

	@FXML
	private Label lblTo;

	@FXML
	private TextArea txtEmailContent;

	@FXML
	private ListView<Email> lstEmails;

	@FXML
	private BorderPane pnlEmailList;

	@FXML
	private BorderPane pnlReadMessage;

	Socket s = null;
	private Client model;
	private Email selectedEmail;
	private Email emptyEmail;
	private static Stage stage;


	@FXML
	public void initialize(){
		if (this.model != null)
			throw new IllegalStateException("Model can only be initialized once");

		//istanza nuovo client
		model = new Client("Federico", "Ferreri", "ff@unito.it");

		selectedEmail = null;

		//binding tra lstEmails e inboxProperty
		lstEmails.itemsProperty().bind(model.inboxProperty());
		lblEmailAddress.textProperty().bind(model.emailAddressProperty());

		try {
			connectWithServer();
			Alert mailSuccessAlert = new Alert(Alert.AlertType.INFORMATION);
			mailSuccessAlert.setTitle("Success");
			mailSuccessAlert.setHeaderText("You entered a valid mail address!");
			mailSuccessAlert.show();
		} catch (MailNotFoundException e) {
			Alert mailErrorAlert = new Alert(Alert.AlertType.ERROR);
			mailErrorAlert.setTitle("Error");
			mailErrorAlert.setHeaderText(e.getMessage());
			mailErrorAlert.show();
		} catch (IOException e) {
			e.printStackTrace();
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Connection failed");
			alert.show();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		emptyEmail = new Email("", List.of(""), "", "");

		updateDetailView(emptyEmail);
	}

	public void connectWithServer() throws MailNotFoundException, IOException, ClassNotFoundException {
		String nomeHost = InetAddress.getLocalHost().getHostName();
		System.out.println(nomeHost);
		s = new Socket(nomeHost, 8189);
		System.out.println("Connection established!");
		InputStream inStream = s.getInputStream();
		OutputStream outStream = s.getOutputStream();
//            Scanner in = new Scanner(inStream);
		ObjectInputStream in = new ObjectInputStream(inStream);
		PrintWriter out = new PrintWriter(outStream, true);
		out.println(model.emailAddressProperty().get());
		System.out.println("Ho spedito il messaggio al socket");

		Object serverResponse = in.readObject();
		if(serverResponse instanceof MailNotFoundException){
			throw new MailNotFoundException();
		}
		else {
			List<Email> userEMail = (List<Email>) serverResponse;
			for (Email email : userEMail) {
				model.addEmail(email);
			}
		}
		s.close();
	}

	/**
	 * Aggiunge una mail alla lista
	 */
	@FXML
	protected void onAddButtonClick() {
		model.addEmail(selectedEmail);
		updateDetailView(emptyEmail);
	}

	/**
	 * Mostra la mail selezionata nella vista
	 */
	@FXML
	protected void showSelectedEmail(MouseEvent mouseClick) {
		Email email = (Email) lstEmails.getSelectionModel().getSelectedItem();

		selectedEmail = email;

		if(mouseClick.getClickCount() == 2)
			updateDetailView(email);
	}

	/**
	 * Aggiorna la vista con la mail selezionata
	 */
	protected void updateDetailView(Email email) {
		if(email != null) {
			lblFrom.setText(email.getSender());
			lblTo.setText(String.join(", ", email.getReceivers()));
			lblObject.setText(email.getObject());
			txtEmailContent.setText(email.getText());
		}
	}




	/*
	 * When a write button is clicked it changes controller and fxml file to
	 * NewMessageController and newMessageController.fxml.
	 * This method pass a Boolean value to NewMessageController class.
	 * This is necessary to set the object field ad not editable in case of
	 * an email forwarding.
	 */
	public void handleWriteButton(MouseEvent mouseEvent) throws IOException {
		FXMLLoader loader = new FXMLLoader((ClientMain.class.getResource("newMessage.fxml"))) ;
		Parent root = (Parent) loader.load();

		NewMessageController newMsgController = loader.getController();
		newMsgController.initialize(true);

		Scene scene = new Scene(root);
		stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		stage.setTitle("Nuovo messaggio");
		stage.setScene(scene);
		stage.show();
	}

	/*
	 * When a reply button or replyAll are clicked it changes controller
	 * and fxml file to ReplyController and reply.fxml.
	 */
	public void handleRepliesButton(MouseEvent mouseEvent) throws IOException {
		Parent root = FXMLLoader.load(Objects.requireNonNull(ClientMain.class.getResource("reply.fxml"))) ;
		Scene scene = new Scene(root);
		stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		stage.setTitle("Rispondi al messaggio");
		stage.setScene(scene);
		stage.show();
	}

	/*
	 * When a forward button is clicked it changes controller and fxml file
	 * to NewMessageController and newMessage.fxml.
	 * This method pass a Boolean value to NewMessageController class.
	 * This is necessary to set the object field ad not editable in case of
	 * an email forwarding.
	 */
	public void handleForwardButton(MouseEvent mouseEvent) throws IOException {
		FXMLLoader loader = new FXMLLoader((ClientMain.class.getResource("newMessage.fxml"))) ;
		Parent root = (Parent) loader.load();

		NewMessageController newMsgController = loader.getController();
		newMsgController.initialize(false);

		Scene scene = new Scene(root);
		stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
		stage.setTitle("Inoltra mail");
		stage.setScene(scene);
		stage.show();
	}

	/*
	 * When delete button is clicked the email that is open is deleted.
	 */
	public void handleDeleteButton(MouseEvent mouseEvent) {
		System.out.println("delete button is clicked --> it should delete the open email");
		model.deleteEmail(selectedEmail);
		updateDetailView(emptyEmail);
	}
}
