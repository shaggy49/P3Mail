package com.p3mail.application.client.controller;

import com.p3mail.application.ClientMain;
import com.p3mail.application.client.model.Client;
import com.p3mail.application.client.model.Email;
import com.p3mail.application.server.MailNotFoundException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class LoginController {
	@FXML
	private RadioButton accountFf;

	@FXML
	private RadioButton accountAf;

	@FXML
	private RadioButton accountMc;

	@FXML
	private ToggleGroup tgEmail;

	private Client model;

	@FXML
		// This method is called by the FXMLLoader when initialization is complete
	void initialize() {
	}

	/*
	 * When a email is clicked twice --> it changes controller and fxml file to MainWindowController and mainWindow.fxml.
	 */
	@FXML
	private void handleRadioButton(MouseEvent mouseEvent) throws IOException {
		RadioButton rb = (RadioButton) mouseEvent.getSource();
		if(mouseEvent.getClickCount() == 2) {
			if (this.model != null)
				throw new IllegalStateException("Model can only be initialized once");

			if (rb.getId().equals("accountFf")){
				//istanza nuovo client
				model = new Client("Federico", "Ferreri", "ff@unito.it");
			} else if (rb.getId().equals("accountAf")) {
				model = new Client("Anna", "Fontana", "af@unito.it");
			} else if(rb.getId().equals("accountMc")){
				model = new Client("Mattia", "Carlino", "mc@unito.it");
			}
			newConnection();

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
//		RadioButton selectRadioButton() {
//			RadioButton selectedRadioButton = (RadioButton) tgEmail.getSelectedToggle();
//			return	selectedRadioButton;
//		}
	}

	private void newConnection() {
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
	}

	public void connectWithServer() throws MailNotFoundException, IOException, ClassNotFoundException {
		Socket s = null;

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

}
