package com.p3mail.application.client.controller;

import com.p3mail.application.client.model.Client;
import com.p3mail.application.connection.model.Email;
import com.p3mail.application.connection.request.DeleteRequest;
import com.p3mail.application.connection.request.DisconnectRequest;
import com.p3mail.application.connection.request.TriggerServerRequest;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class ClientController {

    Socket socketConnection = null;

    ObjectOutputStream out = null;

    @FXML
    private Label lblFrom;

    @FXML
    private Label lblTo;

    @FXML
    private Label lblObject;

    @FXML
    private Label lblEmailAddress;

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

        selectedEmail = null;

        //binding tra lstEmails e inboxProperty
        lstEmails.itemsProperty().bind(model.inboxProperty());
        lblEmailAddress.textProperty().bind(model.emailAddressProperty());

        try {
            connectWithServer();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Connection failed");
            alert.show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        emptyEmail = new Email(0, "", List.of(""), "", "");

        updateDetailView(emptyEmail);
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

        ClientListener clientListener = new ClientListener(this, socketConnection);
        new Thread(clientListener).start();

    }

    public void closeSocketConnection() {
        try {
            if(out != null) {
                out.writeObject(new DisconnectRequest());
            }
            if(socketConnection != null) {
                socketConnection.close();
                System.out.println("Connessione terminata");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Aggiunge una mail alla lista
     */
    @FXML
    protected void onAddButtonClick() throws IOException {
        out.writeObject(new TriggerServerRequest());
    }


    /**
     * Elimina la mail selezionata
     */
    @FXML
    protected void onDeleteButtonClick() {
        System.out.println("You want to delete the email with id = " + selectedEmail.getId()); //debug purpose
        if(socketConnection != null) {
            try {
                out.writeObject(new DeleteRequest(selectedEmail.getId()));
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("The server doesn't seem connected!");
                alert.show();
                e.printStackTrace();
            }
        }
    }

    public void addEmailToInbox(Email email) {
        model.addEmail(email);
    }

    public void deleteAndUpdateView() {
        model.deleteEmail(selectedEmail); //do this only if server says that all works fine!
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

}
