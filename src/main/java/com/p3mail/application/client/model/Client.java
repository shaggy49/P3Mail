package com.p3mail.application.client.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Classe Client, conterrà la lista di mail che sarà il model
 */

public class Client implements Serializable {
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
     *
     * @return   elimina l'email specificata   
     *
     */
    public void deleteEmail(Email email) {
        inboxContent.remove(email);
    }

    /**
     *genera email random da aggiungere alla lista di email, esse verranno mostrate nella ui
     */
    public void generateRandomEmails(int n) {
        String[] people = new String[] {"Paolo", "Alessandro", "Enrico", "Giulia", "Gaia", "Simone"};
        String[] objects = new String[] {
                "Importante", "A proposito della nostra ultima conversazione", "Tanto va la gatta al lardo",
                "Non dimenticare...", "Domani scuola" };
        String[] texts = new String[] {
                "È necessario che ci parliamo di persona, per mail rischiamo sempre fraintendimenti",
                "Ricordati di comprare il latte tornando a casa",
                "L'appuntamento è per domani alle 9, ci vediamo al solito posto",
                "Ho sempre pensato valesse 42, tu sai di cosa parlo"
        };
        Random r = new Random();
        for (int i=0; i<n; i++) {
            Email email = new Email(
                    people[r.nextInt(people.length)],
                    List.of(people[r.nextInt(people.length)]),
                    objects[r.nextInt(objects.length)],
                    texts[r.nextInt(texts.length)]);
            inboxContent.add(email);
        }
    }

    /**
     * @return for now it just duplicate the selected email
     */
    public void addEmail(Email selectedEmail) {
        inboxContent.add(selectedEmail);
    }
}

