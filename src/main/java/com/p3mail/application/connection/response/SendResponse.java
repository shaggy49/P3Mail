package com.p3mail.application.connection.response;

public class SendResponse extends ServerResponse{
    private boolean result;

    public SendResponse (){
        result = true;
    }

    public SendResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }
}
