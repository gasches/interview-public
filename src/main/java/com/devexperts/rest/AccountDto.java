package com.devexperts.rest;

import com.devexperts.account.Account;

public class AccountDto {

    private final String firstName;
    private final String lastName;
    private final Double balance;

    public AccountDto(String firstName, String lastName, Double balance) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Double getBalance() {
        return balance;
    }

    public static AccountDto of(Account account) {
        return new AccountDto(account.getFirstName(), account.getLastName(), account.getBalance());
    }
}
