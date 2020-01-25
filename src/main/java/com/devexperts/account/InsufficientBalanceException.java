package com.devexperts.account;

public class InsufficientBalanceException extends Exception {

    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientBalanceException(AccountKey accountKey) {
        this("Insufficient Account Balance: " + accountKey);
    }
}
