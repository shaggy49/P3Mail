package com.p3mail.application.connection.response;

import com.p3mail.application.connection.model.Email;

public class DeleteEmailNotification extends ServerResponse{
    private Email deletedEmail;

    public DeleteEmailNotification(Email deletedEmail) {
        this.deletedEmail = deletedEmail;
    }

    public Email getDeletedEmail() {
        return deletedEmail;
    }


}
