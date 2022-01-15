package com.p3mail.application.connection.request;

import com.p3mail.application.connection.model.Email;

public class SendRequest extends ClientRequest{
    private Email emailToSend;

    public SendRequest(Email emailToSend) {
        this.emailToSend = emailToSend;
    }

    public Email getEmailToSend() {
        return emailToSend;
    }
}
