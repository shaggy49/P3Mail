package com.p3mail.application.connection;

import com.p3mail.application.connection.model.Email;
import com.p3mail.application.connection.response.ServerResponse;

public class NewEmailNotification extends ServerResponse {
    private Email newEmail;

    public NewEmailNotification(Email newEmail) {
        this.newEmail = newEmail;
    }

    public Email getNewEmail() {
        return newEmail;
    }
}
