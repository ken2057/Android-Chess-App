package com.example.chess.Data;

public class Account {

    public Account(){}

    public Account(String accountID, String accountPassword, String accountEmail) {
        AccountID = accountID;
        AccountPassword = accountPassword;
        AccountEmail = accountEmail;
    }

    public String getAccountID() {
        return AccountID;
    }

    public void setAccountID(String accountID) {
        AccountID = accountID;
    }

    public String getAccountPassword() {
        return AccountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        AccountPassword = accountPassword;
    }

    public String getAccountEmail() {
        return AccountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        AccountEmail = accountEmail;
    }

    public String AccountID;
    public String AccountPassword;
    public String AccountEmail;

}
