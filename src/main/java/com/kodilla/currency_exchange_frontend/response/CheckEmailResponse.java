package com.kodilla.currency_exchange_frontend.response;
public class CheckEmailResponse {
    private boolean emailTaken;

    public boolean isEmailTaken() {
        return emailTaken;
    }

    public void setEmailTaken(boolean emailTaken) {
        this.emailTaken = emailTaken;
    }
}
