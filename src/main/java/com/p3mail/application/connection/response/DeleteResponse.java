package com.p3mail.application.connection.response;

public class DeleteResponse extends ServerResponse{
    private boolean result;


    public DeleteResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }


}
