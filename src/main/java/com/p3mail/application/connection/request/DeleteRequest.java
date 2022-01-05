package com.p3mail.application.connection.request;

public class DeleteRequest extends ClientRequest{

    private int emailId;

    public DeleteRequest(int emailId) {
        this.emailId = emailId;
    }

    public int getEmailId() {
        return emailId;
    }
}
