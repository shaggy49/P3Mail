package com.p3mail.application.server;

import com.p3mail.application.client.model.Client;

import java.util.ArrayList;
import java.util.List;

public class RegisteredClient {
    List<Client> registeredUser;

    public RegisteredClient() {
        this.registeredUser = new ArrayList<>();
        registeredUser.add(new Client("Federico", "Ferreri", "ff@unito.it"));
        registeredUser.add(new Client("Mattia", "Carlino", "mc@unito.it"));
        registeredUser.add(new Client("Anna", "Fontana", "af@unito.it"));
    }

    public List<String> getRegisteredMails() {
        return registeredUser
                .stream()
                .map(client -> client.emailAddressProperty().get())
                .toList();
    }

    public Client getClientInfoByEmail(String email) {
        return registeredUser
                .stream()
                .filter(client -> client.emailAddressProperty().get().equals(email))
                .findAny()
                .orElse(null);
    }
}
