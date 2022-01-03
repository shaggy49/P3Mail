package com.p3mail.application.server;

import com.p3mail.application.server.model.MailAccount;

import java.util.ArrayList;
import java.util.List;

public class RegisteredClient {
    private List<MailAccount> registeredUser;

    public RegisteredClient() {
        this.registeredUser = new ArrayList<>();
        registeredUser.add(new MailAccount("Anna", "Fontana", "af@unito.it"));
        registeredUser.add(new MailAccount("Federico", "Ferreri", "ff@unito.it"));
        registeredUser.add(new MailAccount("Mattia", "Carlino", "mc@unito.it"));
    }

    public List<MailAccount> getRegisteredUser() {
        return registeredUser;
    }

    public List<String> getRegisteredMails() {
        return registeredUser
                .stream()
                .map(MailAccount::getEmailAddress)
                .toList();
    }

}
