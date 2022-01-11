package com.p3mail.application.connection;

import com.p3mail.application.connection.response.ServerResponse;

public class NewEmailNotification extends ServerResponse {
    private String fromEmailAddress;

    public NewEmailNotification(String fromEmailAddress) {
        this.fromEmailAddress = fromEmailAddress;
    }

    public String getFromEmailAddress() {
        return fromEmailAddress;
    }
}
