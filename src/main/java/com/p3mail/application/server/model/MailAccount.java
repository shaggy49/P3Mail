package com.p3mail.application.server.model;

import com.p3mail.application.connection.model.Email;

import java.util.ArrayList;
import java.util.List;

public class MailAccount {
    private String name;
    private String surname;
    private String emailAddress;
    private List<Email> inbox;

    public MailAccount(String name, String surname, String emailAddress) {
        this.name = name;
        this.surname = surname;
        this.emailAddress = emailAddress;
        this.inbox = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public List<Email> getInbox() {
        return inbox;
    }

    public void addEmailToInbox(Email email) {
        inbox.add(email);
    }
}
