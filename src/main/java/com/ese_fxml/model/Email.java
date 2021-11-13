package com.ese_fxml.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

//La classe Email specifica ID, mittente,  destinatario, argomento, testo etc..
public class Email {
    private IntegerProperty mailId;
    private StringProperty mailSender;
    private StringProperty mailReceiver;
    private StringProperty mailObject;
    private StringProperty mailText;
    private BooleanProperty mailStared;

    public Email(int mailId, String mailSender, String mailReceiver, String mailObject, String mailText, boolean mailStared) {
        //setMailId(mailId);
        this.mailIdProperty().set(mailId);
        this.mailSenderProperty().set(mailSender);
        this.mailReceiverProperty().set(mailReceiver);
        this.mailObjectProperty().set(mailObject);
        this.mailTextProperty().set(mailText);
        this.mailStaredProperty().set(mailStared);
    }


    public final IntegerProperty mailIdProperty() {
        return this.mailId;
    }

    public final int getMailId() {
        return this.mailIdProperty().get();
    }

    public final void setMailId(final int mailId) {
        this.mailIdProperty().set(mailId);
    }


    public final StringProperty mailSenderProperty() {
        return this.mailSender;
    }

    public final String getMailSender() {
        return this.mailSenderProperty().get();
    }

    public final void setMailSender(final String mailSender) {
        this.mailSenderProperty().set(mailSender);
    }


    public final StringProperty mailReceiverProperty() {
        return this.mailReceiver;
    }

    public final String getMailReceiver() {
        return this.mailReceiverProperty().get();
    }

    public final void setMailReceiver(final String mailReceiver) {
        this.mailSenderProperty().set(mailReceiver);
    }


    public final StringProperty mailObjectProperty() {
        return this.mailObject;
    }

    public final String getMailObject() {
        return this.mailObjectProperty().get();
    }

    public final void setMailObject(final String mailObject) {
        this.mailObjectProperty().set(mailObject);
    }


    public final StringProperty mailTextProperty() {
        return this.mailText;
    }

    public final String getMailText() {
        return this.mailTextProperty().get();
    }

    public final void setMailText(final String mailObject) {
        this.mailTextProperty().set(mailObject);
    }


    public final BooleanProperty mailStaredProperty() {
        return this.mailStared;
    }

    public final boolean getMailStared() {
        return this.mailStaredProperty().get();
    }

    public final void setMailStared(final boolean mailStared) {
        this.mailStaredProperty().set(mailStared);
    }
}

