package com.p3mail.application.server.model;

import java.util.ArrayList;
import java.util.List;

public class RegisteredClient {
    private List<String> registeredUser;

    public RegisteredClient() {
        this.registeredUser = new ArrayList<>();
        registeredUser.add("af@unito.it");
        registeredUser.add("ff@unito.it");
        registeredUser.add("mc@unito.it");
    }

    public List<String> getRegisteredUser() {
        return registeredUser;
    }
}
