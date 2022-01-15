package com.p3mail.application.connection.request;

import com.p3mail.application.connection.model.Email;

public class DeleteRequest extends ClientRequest{
    private Email emailToDelete;

    public DeleteRequest(Email emailToDelete) {
        this.emailToDelete = emailToDelete;
    }

    public Email getEmailToDelete() {
        return emailToDelete;
    }

}
