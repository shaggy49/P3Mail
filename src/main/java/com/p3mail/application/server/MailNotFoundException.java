package com.p3mail.application.server;

import java.io.Serializable;

public class MailNotFoundException extends Exception implements Serializable {
    public MailNotFoundException() {
        super("User not registered!");
    }
}
