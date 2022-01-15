package com.p3mail.application.client.model;

import com.p3mail.application.connection.model.Email;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;

/**
 * Classe Client, conterrà la lista di mail che sarà il model
 */

public class Client {
    private final StringProperty name;
    private final StringProperty surname;
    private final StringProperty emailAddress;
    private final ListProperty<Email> inbox;
    private final ObservableList<Email> inboxContent;

    /**
     * Costruttore della classe.
     *
     * @param emailAddress   indirizzo email    
     *
     */

    public Client(String name, String surname, String emailAddress) {
        this.name = new SimpleStringProperty(name);
        this.surname = new SimpleStringProperty(surname);
        this.emailAddress = new SimpleStringProperty(emailAddress);
        this.inboxContent = FXCollections.observableList(new LinkedList<>());
        this.inbox = new SimpleListProperty<>();
        this.inbox.set(inboxContent);
    }

    /**
     * @return      lista di email  
     *
     */
    public ListProperty<Email> inboxProperty() {
        return inbox;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty surnameProperty() {
        return surname;
    }

    /**
     *
     * @return   indirizzo email della casella postale   
     *
     */
    public StringProperty emailAddressProperty() {
        return emailAddress;
    }

    /**
     * @return aggiunge la mail specificata
     */
    public void addEmail(Email selectedEmail) {
        inboxContent.add(selectedEmail);
    }

    /**
     * @return   elimina l'email specificata
     */
    public void deleteEmail(Email email) {
        inboxContent.remove(email);
    }

}

