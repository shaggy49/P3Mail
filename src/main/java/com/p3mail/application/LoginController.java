package com.p3mail.application;

import com.p3mail.application.ClientMain;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
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

	@FXML
		// This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert tgEmail != null : "fx:id=\"tgEmail\" was not injected: check your FXML file 'logIn.fxml'.";
	}

	/*
	 * When a email is clicked twice --> it changes controller and fxml file to MainWindowController and mainWindow.fxml.
	 */
	@FXML
	private void handleRadioButton(MouseEvent mouseEvent) throws IOException {
		RadioButton rb = (RadioButton) mouseEvent.getSource();
		if(mouseEvent.getClickCount() == 2) {
			System.out.println("2 click sulla stessa mail");
			System.out.println(rb.getId());

			//qui sotto si cambia schermata al doppio click
			Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("mainWindow.fxml"))) ;
			Scene scene = new Scene(root);
			Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
			stage.setTitle("Email client");
			stage.setScene(scene);
			stage.show();
		}
	}

	RadioButton selectRadioButton() {
		RadioButton selectedRadioButton = (RadioButton) tgEmail.getSelectedToggle();
		return	selectedRadioButton;
	}
}
