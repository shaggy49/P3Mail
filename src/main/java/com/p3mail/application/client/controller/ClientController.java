package com.p3mail.application.client.controller;

import com.p3mail.application.client.model.Client;
import com.p3mail.application.client.model.Email;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

import java.util.List;

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
        model = new Client("Luigi", "Rossi","luigirossi@unito.it");
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

}
