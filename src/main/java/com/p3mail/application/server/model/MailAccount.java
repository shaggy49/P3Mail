package com.p3mail.application.server.model;

public class MailAccount {
    private String name;
    private String surname;
    private String emailAddress;

    public MailAccount(String name, String surname, String emailAddress) {
        this.name = name;
        this.surname = surname;
        this.emailAddress = emailAddress;
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

}
