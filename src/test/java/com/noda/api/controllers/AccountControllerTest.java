package com.noda.api.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.noda.api.dtos.AccountRequestDTO;
import com.noda.api.dtos.TransactionRequestDTO;
import com.noda.api.dtos.TransactionResponseDTO;
import com.noda.api.dtos.TransferRequestDTO;
import com.noda.api.models.Account;
import com.noda.api.models.Transaction;
import com.noda.api.models.User;
import com.noda.api.models.enums.AccountType;
import com.noda.api.models.enums.TransactionType;
import com.noda.api.services.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private Account buildMockAccount() {
        User user = new User();
        user.setId(1L);


        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("2000");
        account.setAccountType(AccountType.CHECKING);
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);

        return account;
    }

    @Nested
    @DisplayName("POST / accounts")
    class createAccount {

        @Test
        @DisplayName(("Should return 201 and account body when data is valid"))
        void shouldReturn201WhenDataIsValid() throws Exception {
            AccountRequestDTO request = new AccountRequestDTO(AccountType.CHECKING, 1L);

            Mockito.when(accountService.createAccount(Mockito.any())).thenReturn(buildMockAccount());

            mockMvc.perform(
                            post("/accounts")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accountId").value(1))
                    .andExpect(jsonPath("$.accountType").value("CHECKING"));
        }

        @Test
        @DisplayName("Should return 400 when request body is invalid")
        void shouldReturn400WHenBodyIsInvalid() throws Exception {

            mockMvc.perform(
                            post("/accounts")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{}")
                    )
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /accounts/transfer")
    class Transfer {

        @Test
        @DisplayName("Should return 200 and success message when transfer is valid")
        void shouldReturn200WhenTransferIsValid() throws Exception {
            TransferRequestDTO request = new TransferRequestDTO(1L, 2L, BigDecimal.valueOf(50));

            Mockito.doNothing().when(accountService).transfer(1L, 2L, BigDecimal.valueOf(50));

            mockMvc.perform(
                            post("/accounts/transfer")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isOk());
        }
    }

        @Nested
        @DisplayName("POST /accounts/deposit")
        class Deposit {

            @Test
            @DisplayName("Should return 200 and update account when deposit is valid")
            void shouldReturn200WhenDepositIsValid() throws Exception {
                TransactionRequestDTO request = new TransactionRequestDTO(1L, BigDecimal.TEN);
                Mockito.when(accountService.deposit(Mockito.any(), Mockito.any()))
                        .thenReturn(buildMockAccount());

                mockMvc.perform(
                        post("/accounts/deposit")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                );
            }
        }

        @Nested
        @DisplayName("POST /accounts/withdrawal")
        class Withdrawal {


            @Test
            @DisplayName("Should return 200 and updated account when withdrawal is valid")
            void shouldReturn200WhenWithdrawalIsValid() throws Exception {
                TransactionRequestDTO request = new TransactionRequestDTO(1L, BigDecimal.TEN);

                Mockito.when(accountService.withdrawal(1L, BigDecimal.TEN))
                        .thenReturn(buildMockAccount());

                mockMvc.perform(
                        post("/accounts/withdrawal")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.accountId").value(1));
            }
        }
        @Nested
        @DisplayName("GET /accounts/{id}/statement")
        class GetStatement {

            @Test
            @DisplayName("Should return 200 and list of transactions")
            void shouldReturn200WithTransactionList() throws Exception {
                Transaction mockTransaction = new Transaction();
                mockTransaction.setId(1L);
                mockTransaction.setAmount(BigDecimal.TEN);
                mockTransaction.setTransactionType(TransactionType.DEPOSIT);
                mockTransaction.setTimestamp(LocalDateTime.now());


                Mockito.when(accountService.getAccountStatement(1L))
                        .thenReturn(List.of(mockTransaction));


                mockMvc.perform(get("/accounts/1/statement"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].amount").value(10))
                        .andExpect(jsonPath("$[0].type").value("DEPOSIT"));
            }

    }
}



