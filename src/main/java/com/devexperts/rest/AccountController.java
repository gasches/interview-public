package com.devexperts.rest;

import javax.validation.constraints.Min;

import com.devexperts.account.Account;
import com.devexperts.account.AccountKey;
import com.devexperts.account.InsufficientBalanceException;
import com.devexperts.service.AccountService;

import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/operations")
public class AccountController extends AbstractAccountController {
    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestParam("account_id") long accountId,
            @Length(min = 2) @RequestParam("first_name") String firstName,
            @Length(min = 2) @RequestParam("last_name") String lastName,
            @Min(0L) @RequestParam("balance") double balance) {
        Account account = new Account(AccountKey.valueOf(accountId), firstName, lastName, balance);
        accountService.createAccount(account);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/get", consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AccountDto> getAccount(@RequestParam("account_id") long accountId) {
        Account account = accountService.getAccount(accountId);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(AccountDto.of(account));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestParam("source_id") long sourceId,
            @RequestParam("target_id") long targetId, @RequestParam("amount") double amount) {
        log.debug("Accept transfer request: source: {}, target: {}, amount: {}", sourceId, targetId, amount);
        if (amount < 0.) {
            log.error("Invalid transfer amount: {}", amount);
            return ResponseEntity.badRequest().build();
        }
        Account source = accountService.getAccount(sourceId);
        if (source == null) {
            log.error("Transfer source account with ID '{}' is not found", sourceId);
            return ResponseEntity.notFound().build();
        }
        Account target = accountService.getAccount(targetId);
        if (target == null) {
            log.error("Transfer target account with ID '{}' is not found", targetId);
            return ResponseEntity.notFound().build();
        }
        try {
            accountService.transfer(source, target, amount);
        } catch (InsufficientBalanceException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }
}
