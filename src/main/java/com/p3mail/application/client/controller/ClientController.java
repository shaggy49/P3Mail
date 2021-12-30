package com.p3mail.application.client.controller;

import com.p3mail.application.client.model.Client;
import com.p3mail.application.client.model.Email;
import com.p3mail.application.server.MailNotFoundException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe Controller 
 */

public class ClientController {
    @FXML
    private Label lblFrom;

    @FXML
    private Label lblTo;

    @FXML
    private Label lblObject;

    @FXML
    private Label lblEmailAddress;

    @FXML
    public Button connectButton;

    @FXML
    private TextArea txtEmailContent;

    @FXML
    private ListView<Email> lstEmails;

    @FXML
    public Button addEmailButton;

    @FXML
    public Button deleteEmailButton;

    private Client model;
    private Email selectedEmail;
    private Email emptyEmail;

    @FXML
    public void initialize(){
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");
        //istanza nuovo client
        model = new Client("Federico", "Ferreri", "ff@unito.it");
        model.generateRandomEmails(10);

        selectedEmail = null;

        //binding tra lstEmails e inboxProperty
        lstEmails.itemsProperty().bind(model.inboxProperty());
        lblEmailAddress.textProperty().bind(model.emailAddressProperty());

        emptyEmail = new Email("", List.of(""), "", "");

        updateDetailView(emptyEmail);
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
     * Elimina la mail selezionata
     */
    @FXML
    protected void onDeleteButtonClick() {
        model.deleteEmail(selectedEmail);
        updateDetailView(emptyEmail);
    }

     /**
     * Mostra la mail selezionata nella vista
     */
     @FXML
    protected void showSelectedEmail(MouseEvent mouseClick) {
        Email email = lstEmails.getSelectionModel().getSelectedItem();

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

    @FXML
    public void tryConnection(MouseEvent mouseClick) {
        Socket s = null;
        try {
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
            if(serverResponse instanceof MailNotFoundException mailNotFoundException){
                Alert mailErrorAlert = new Alert(Alert.AlertType.ERROR);
                mailErrorAlert.setTitle("Error");
                mailErrorAlert.setHeaderText(mailNotFoundException.getMessage());
                mailErrorAlert.show();
            }
            else {
                Alert mailSuccessAlert = new Alert(Alert.AlertType.INFORMATION);
                mailSuccessAlert.setTitle("Success");
                mailSuccessAlert.setHeaderText("You entered a valid mail address!");
                mailSuccessAlert.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Connection failed");
            alert.show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
