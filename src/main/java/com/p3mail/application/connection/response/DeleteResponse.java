package com.p3mail.application.connection.response;

public class DeleteResponse extends ServerResponse{
    private boolean result;
    private String errorMessage = "";

    public DeleteResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
