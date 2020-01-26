package com.devexperts.rest;

import com.devexperts.account.Account;
import com.devexperts.account.AccountKey;
import com.devexperts.service.AccountService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    private static final String TRANSFER_PATH = "/api/operations/transfer";
    private static final long ACCOUNT1_ID = 1L;
    private static final long ACCOUNT2_ID = 2L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    void before() {
        Account account1 = new Account(AccountKey.valueOf(ACCOUNT1_ID), "SourceF1", "SourceL1", 100.);
        Account account2 = new Account(AccountKey.valueOf(ACCOUNT2_ID), "SourceF2", "SourceL2", 100.);
        accountService.createAccount(account1);
        accountService.createAccount(account2);
    }

    @AfterEach
    void after() {
        accountService.clear();
    }

    @Test
    void successfulTransfer() throws Exception {
        mockMvc.perform(transferReqBuilder(ACCOUNT1_ID, ACCOUNT2_ID, 50.))
                .andExpect(status().isOk());
        assertEquals(50., accountService.getAccount(ACCOUNT1_ID).getBalance());
        assertEquals(150., accountService.getAccount(ACCOUNT2_ID).getBalance());
    }

    @Test
    void missingSourceIdError() throws Exception {
        mockMvc.perform(post(TRANSFER_PATH)
                .param("target_id", Long.toString(ACCOUNT2_ID))
                .param("amount", Double.toString(50.)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void illegalAmountError() throws Exception {
        mockMvc.perform(transferReqBuilder(ACCOUNT1_ID, ACCOUNT2_ID, -50.))
                .andExpect(status().isBadRequest());
    }

    @Test
    void nonExistingAccountTransferError() throws Exception {
        mockMvc.perform(transferReqBuilder(ACCOUNT1_ID, Long.MAX_VALUE, 50.))
                .andExpect(status().isNotFound());
    }

    @Test
    void insufficientAccountBalanceError() throws Exception {
        mockMvc.perform(transferReqBuilder(ACCOUNT1_ID, ACCOUNT2_ID, Double.MAX_VALUE))
                .andExpect(status().isInternalServerError());
        assertEquals(100., accountService.getAccount(ACCOUNT1_ID).getBalance());
        assertEquals(100., accountService.getAccount(ACCOUNT2_ID).getBalance());
    }

    private static MockHttpServletRequestBuilder transferReqBuilder(long sourceId, long targetId, double amount) {
        return post(TRANSFER_PATH)
                .param("source_id", Long.toString(sourceId))
                .param("target_id", Long.toString(targetId))
                .param("amount", Double.toString(amount));
    }
}