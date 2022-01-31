package com.p3mail.application.client.model;

import com.p3mail.application.connection.model.Email;
import com.p3mail.application.connection.request.ClientRequest;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Classe Client, conterrà la lista di mail che sarà il model
 */

public class Client {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private ReadWriteLock rwl;
    private Lock read, write;

    private final StringProperty name;
    private final StringProperty surname;
    private final StringProperty emailAddress;
    private final ListProperty<Email> inbox;
    private final ObservableList<Email> inboxContent;

    /**
     * Constructor of class.
     * @param name
     * @param surname
     * @param emailAddress
     */
    public Client(String name, String surname, String emailAddress) {
        this.name = new SimpleStringProperty(name);
        this.surname = new SimpleStringProperty(surname);
        this.emailAddress = new SimpleStringProperty(emailAddress);
        this.inboxContent = FXCollections.observableList(new LinkedList<>());
        this.inbox = new SimpleListProperty<>();
        this.inbox.set(inboxContent);

        rwl = new ReentrantReadWriteLock();
        read = rwl.readLock();
        write = rwl.writeLock();
    }

    /**
     * @return lista di email
     */
    public ListProperty<Email> inboxProperty() {
        return inbox;
    }

    /**
     * @return nameProperty
     */
    public StringProperty nameProperty() {
        return name;
    }

    /**
     * @return surnameProperty
     */
    public StringProperty surnameProperty() {
        return surname;
    }

    /**
     * @return indirizzo email della casella postale
     */
    public StringProperty emailAddressProperty() {
        return emailAddress;
    }

    /**
     * Add Email to inboxContent.
     * @param selectedEmail
     */
    public void addEmail(Email selectedEmail) {
        inboxContent.add(selectedEmail);
    }

    /**
     * Delete Email to inbocContent.
     * @param email
     */
    public void deleteEmail(Email email) {
        inboxContent.remove(email);
    }

    public void deleteEmail(int id) {
        inboxContent.removeIf(email1 -> email1.getId() == id);
    }

    public Socket getSocket() {
        read.lock();
        Socket temp = socket;
        read.unlock();
        return temp;
    }

    public void setSocket(Socket socket) {
        write.lock();
        this.socket = socket;
        write.unlock();
    }

    public ObjectOutputStream getOut() {
        read.lock();
        ObjectOutputStream temp = out;
        read.unlock();
        return temp;
    }

    public void setOut(ObjectOutputStream out) {
        write.lock();
        this.out = out;
        write.unlock();
    }

    public ObjectInputStream getIn() {
        read.lock();
        ObjectInputStream temp = in;
        read.unlock();
        return temp;
    }

    public void setIn(ObjectInputStream in) {
        write.lock();
        this.in = in;
        write.unlock();
    }

    // si fanno le ClientRequest con client aggiornato
    public void writeOut(ClientRequest r) throws IOException {
        write.lock();
        out.writeObject(r);
        write.unlock();
    }
}
