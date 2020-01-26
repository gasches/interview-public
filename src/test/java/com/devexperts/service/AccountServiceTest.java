package com.devexperts.service;

import com.devexperts.account.Account;
import com.devexperts.account.AccountKey;
import com.devexperts.account.InsufficientBalanceException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Test
    void transfer() throws InsufficientBalanceException {
        Account source = new Account(AccountKey.valueOf(1L), "SourceF", "SourceL", 100.);
        Account target = new Account(AccountKey.valueOf(2L), "TargetF", "TargetL", 100.);
        accountService.transfer(source, target, 50);

        assertEquals(50., source.getBalance());
        assertEquals(150., target.getBalance());
    }
}