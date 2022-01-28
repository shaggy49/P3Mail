package com.p3mail.application.client.controller;

import com.p3mail.application.ClientMain;
import com.p3mail.application.client.model.Client;
import com.p3mail.application.connection.request.DisconnectRequest;
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

public class LoginController {
    Socket socketConnection = null;
    ObjectOutputStream out = null;
	MainWindowController mainWindowController = null;

    @FXML
    private RadioButton accountFf;

    @FXML
    private RadioButton accountAf;

    @FXML
    private RadioButton accountMc;

    @FXML
    private ToggleGroup tgEmail;

    private Client model;
    private Stage stage;

    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    public void initialize(Stage stage) {
        this.stage = stage;
        stage.setResizable(false);
    }

    /*
     * When a email is clicked twice --> it changes controller and fxml file to MainWindowController and mainWindow.fxml.
     */
    @FXML
    private void handleRadioButton(MouseEvent mouseEvent) throws IOException {
        RadioButton rb = (RadioButton) mouseEvent.getSource();
        if (mouseEvent.getClickCount() == 2) {

            if (rb.getId().equals("accountFf")) {
                //istanza nuovo client
                model = new Client("Federico", "Ferreri", "ff@unito.it");
            } else if (rb.getId().equals("accountAf")) {
                model = new Client("Anna", "Fontana", "af@unito.it");
            } else if (rb.getId().equals("accountMc")) {
                model = new Client("Mattia", "Carlino", "mc@unito.it");
            }

            try {
                connectWithServer();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Sembra ci sia un problema col server..");
                alert.show();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            FXMLLoader loader = new FXMLLoader((ClientMain.class.getResource("mainWindow.fxml")));
            Parent root = (Parent) loader.load();
            mainWindowController = loader.getController();
			mainWindowController.initialize(true, model, socketConnection, out, stage);
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            stage.setTitle("Email client");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.show();
        }
//		RadioButton selectRadioButton() {
//			RadioButton selectedRadioButton = (RadioButton) tgEmail.getSelectedToggle();
//			return	selectedRadioButton;
//		}
    }

    private void connectWithServer() throws IOException, ClassNotFoundException {
        String nomeHost = InetAddress.getLocalHost().getHostName();
        System.out.println(nomeHost);
        socketConnection = new Socket(nomeHost, 8189);
        System.out.println("Connection established!");
        OutputStream socketOutputStream = socketConnection.getOutputStream();


        out = new ObjectOutputStream(socketOutputStream);
        out.writeObject(model.emailAddressProperty().get());
        System.out.println("I send my mail address to the server");

    }

    public void closeSocketConnection() {
		if (mainWindowController != null)
			mainWindowController.closeSocketConnection();
	}

}
