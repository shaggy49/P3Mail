package com.p3mail.application.connection.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Rappresenta una mail
 */

public class Email implements Serializable {

    private int id;
    private String sender;
    private List<String> receivers;
    private String object;
    private String text;
    private Date date;

    private Email() {}

    public Email(String sender, List<String> receivers, String object, String text) {
        this.sender = sender;
        this.receivers = receivers;
        this.object = object;
        this.text = text;
        this.date = new Date();
    }

    public Email(String sender, List<String> receivers, String object, String text, Date date) {
        this.sender = sender;
        this.receivers = receivers;
        this.object = object;
        this.text = text;
        this.date = date;
    }

    /**
     * Costruttore della classe.
     *
     * @param id         email id
     * @param sender     email del mittente
     * @param receivers  emails dei destinatari
     * @param object     oggetto della mail
     * @param text       testo della mail
     */

    /*
     */
    public Email(int id, String sender, List<String> receivers, String object, String text) {
        this.id = id;
        this.sender = sender;
        this.object = object;
        this.text = text;
        this.receivers = new ArrayList<>(receivers);
        this.date = new Date();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public String getObject() {
        return object;
    }

    public String getText() {
        return text;
    }

    public Date getDate() {
        return date;
    }

    /**
     * @return      stringa composta dagli indirizzi e-mail del mittente più destinatari
     */
    @Override
    public String toString() {
        return String.join(" - ", List.of(this.sender,this.object));
    }

    public String classictoString() {
        return "Email{" +
                "sender='" + sender + '\'' +
                ", receivers=" + receivers +
                ", object='" + object + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }
}
