package com.devexperts.service;

import java.util.concurrent.ConcurrentHashMap;

import com.devexperts.account.Account;
import com.devexperts.account.AccountKey;

import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final ConcurrentHashMap<AccountKey, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public void clear() {
        accounts.clear();
    }

    @Override
    public void createAccount(Account account) {
        AccountKey accountKey = account.getAccountKey();
        if (accounts.putIfAbsent(accountKey, account) != null) {
            throw new IllegalArgumentException("Account with key" + accountKey + " is already exists");
        }
    }

    @Override
    public Account getAccount(long id) {
        return accounts.get(AccountKey.valueOf(id));
    }

    @Override
    public void transfer(Account source, Account target, double amount) {
        //do nothing for now
    }
}
