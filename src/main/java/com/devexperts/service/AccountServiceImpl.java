package com.devexperts.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.devexperts.account.Account;
import com.devexperts.account.AccountKey;

import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final ConcurrentHashMap<AccountKey, Account> accounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<AccountKey, ReentrantLock> accountLocks = new ConcurrentHashMap<>();

    @Override
    public void clear() {
        accounts.clear();
        accountLocks.clear();
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
        AccountKey sourceKey = source.getAccountKey();
        AccountKey targetKey = target.getAccountKey();
        if (sourceKey.equals(targetKey)) {
            throw new IllegalArgumentException("Same account transfer: " + sourceKey);
        }
        AccountKey key1;
        AccountKey key2;
        if (sourceKey.compareTo(targetKey) < 0) {
            key1 = sourceKey;
            key2 = targetKey;
        } else {
            key1 = targetKey;
            key2 = sourceKey;
        }
        ReentrantLock lock1 = accountLocks.computeIfAbsent(key1, AccountServiceImpl::newLock);
        ReentrantLock lock2 = accountLocks.computeIfAbsent(key2, AccountServiceImpl::newLock);
        lock1.lock();
        try {
            lock2.lock();
            try {
                double sourceBalance = defaultIfNull(source.getBalance());
                if (sourceBalance < amount) {
                    return;
                }
                source.setBalance(sourceBalance - amount);
                target.setBalance(defaultIfNull(target.getBalance()) + amount);
            } finally {
                lock2.unlock();
            }
        } finally {
            lock1.unlock();
        }
    }

    private static double defaultIfNull(Double value) {
        return value == null ? 0. : value;
    }

    private static ReentrantLock newLock(AccountKey ignore) {
        return new ReentrantLock();
    }
}
