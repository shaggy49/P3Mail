package com.p3mail.application.client.controller;

import com.p3mail.application.client.model.Client;
import com.p3mail.application.connection.model.Email;
import com.p3mail.application.connection.request.DisconnectRequest;
import com.p3mail.application.server.util.MailNotFoundException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class ClientController {

    Socket socketConnection = null;

    ObjectInputStream in = null;

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
        model = new Client("Federico", "Ferreri", "mcs@unito.it");

        selectedEmail = null;

        //binding tra lstEmails e inboxProperty
        lstEmails.itemsProperty().bind(model.inboxProperty());
        lblEmailAddress.textProperty().bind(model.emailAddressProperty());

        try {
            connectWithServer();
//            Alert mailSuccessAlert = new Alert(Alert.AlertType.INFORMATION);
//            mailSuccessAlert.setTitle("Success");
//            mailSuccessAlert.setHeaderText("You entered a valid mail address!");
//            mailSuccessAlert.show();
        } catch (MailNotFoundException e) {
            //maybe handle from ClientMain class
            Alert mailErrorAlert = new Alert(Alert.AlertType.ERROR);
            mailErrorAlert.setTitle("Error");
            mailErrorAlert.setHeaderText(e.getMessage());
            mailErrorAlert.show();
        }
        catch (IOException e) {
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

    private void connectWithServer() throws MailNotFoundException, IOException, ClassNotFoundException {
        String nomeHost = InetAddress.getLocalHost().getHostName();
        System.out.println(nomeHost);
        socketConnection = new Socket(nomeHost, 8189);
        System.out.println("Connection established!");
        InputStream socketInputStream = socketConnection.getInputStream();
        OutputStream socketOutputStream = socketConnection.getOutputStream();

        out = new ObjectOutputStream(socketOutputStream);
        out.writeObject(model.emailAddressProperty().get());
        System.out.println("I send my mail address to the server");

        in = new ObjectInputStream(socketInputStream);
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
    protected void onAddButtonClick() {
        model.addEmail(selectedEmail);
        updateDetailView(emptyEmail);
    }

    /**
     * Elimina la mail selezionata
     */
    @FXML
    protected void onDeleteButtonClick() {
        System.out.println("You want to delete the email with id = " + selectedEmail.getId()); //debug purpose
        if(socketConnection != null) {
            //TODO: also send a delete request to the mail server
            model.deleteEmail(selectedEmail); //do this only if server says that all works fine!
            updateDetailView(emptyEmail);
        }
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
