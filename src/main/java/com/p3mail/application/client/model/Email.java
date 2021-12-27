package com.p3mail.application.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Rappresenta una mail
 */

public class Email {

    private int id;
    private String sender;
    private List<String> receivers;
    private String object;
    private String text;
    private Date date;

    private Email() {}

    /**
     * Costruttore della classe.
     *
     * @param sender     email del mittente
     * @param receivers  emails dei destinatari
     * @param object    oggetto della mail
     * @param text       testo della mail
     */


    public Email(String sender, List<String> receivers, String object, String text) {
        this.sender = sender;
        this.object = object;
        this.text = text;
        this.receivers = new ArrayList<>(receivers);
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

    /**
     * @return      stringa composta dagli indirizzi e-mail del mittente più destinatari
     */
    @Override
    public String toString() {
        return String.join(" - ", List.of(this.sender,this.object));
    }
}
