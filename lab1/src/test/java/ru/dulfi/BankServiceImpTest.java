package ru.dulfi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.dulfi.domain.Account;
import ru.dulfi.exceptions.AccountNotFoundException;
import ru.dulfi.exceptions.InsufficientFundsException;
import ru.dulfi.service.BankService;
import ru.dulfi.service.BankServiceImp;

import java.math.BigDecimal;

public class BankServiceImpTest {

    @Test
    void testCreateAccountSuccess() {
        BankService bankService = new BankServiceImp();
        Account account = bankService.createAccount(BigDecimal.valueOf(100.0));
        try {
            BigDecimal balance = bankService.getBalance(account.getAccountNumber());
            Assertions.assertEquals(BigDecimal.valueOf(100.0), balance);
        } catch (AccountNotFoundException e) {
            Assertions.fail("Счёт только что создан, не должен выбрасывать AccountNotFoundException");
        }
    }

    @Test
    void testDepositSuccess() throws AccountNotFoundException {
        BankService bankService = new BankServiceImp();
        Account account = bankService.createAccount(BigDecimal.valueOf(100.0));
        String accountNumber = account.getAccountNumber();
        bankService.deposit(accountNumber, BigDecimal.valueOf(50.0));
        BigDecimal balance = bankService.getBalance(accountNumber);
        Assertions.assertEquals(BigDecimal.valueOf(150.0), balance);
    }

    @Test
    void testDeposit_AccountNotFound() {
        BankService bankService = new BankServiceImp();
        Assertions.assertThrows(AccountNotFoundException.class, () -> {
            bankService.deposit("123", BigDecimal.valueOf(123.0));
        });
    }

    @Test
    void testWithdrawSuccess() throws AccountNotFoundException, InsufficientFundsException {
        BankService bankService = new BankServiceImp();
        Account account = bankService.createAccount(BigDecimal.valueOf(200.0));
        String accountNumber = account.getAccountNumber();
        bankService.withdraw(accountNumber, BigDecimal.valueOf(50.0));
        BigDecimal balance = bankService.getBalance(accountNumber);
        Assertions.assertEquals(BigDecimal.valueOf(250.0), balance);
    }

    @Test
    void testWithdraw_InsufficientFunds() {
        BankService bankService = new BankServiceImp();
        Account account = bankService.createAccount(BigDecimal.valueOf(100.0));
        String accountNumber = account.getAccountNumber();
        Assertions.assertThrows(InsufficientFundsException.class, () -> {
            bankService.withdraw(accountNumber, BigDecimal.valueOf(500.0));
        });
    }

    @Test
    void testWithdraw_AccountNotFound() {
        BankService bankService = new BankServiceImp();
        Assertions.assertThrows(AccountNotFoundException.class, () -> {
            bankService.withdraw("oleja", BigDecimal.valueOf(10.0));
        });
    }

    @Test
    void testGetOperationsSuccess() throws AccountNotFoundException, InsufficientFundsException {
        BankService bankService = new BankServiceImp();
        Account account = bankService.createAccount(BigDecimal.valueOf(100.0));
        String accountNumber = account.getAccountNumber();
        bankService.deposit(accountNumber, BigDecimal.valueOf(50.0));
        bankService.withdraw(accountNumber, BigDecimal.valueOf(20.0));
        var operations = bankService.getOperations(accountNumber);
        Assertions.assertEquals(3, operations.size());
    }

    @Test
    void testGetOperations_AccountNotFound() {
        BankService bankService = new BankServiceImp();
        Assertions.assertThrows(AccountNotFoundException.class, () -> {
            bankService.getOperations("NoSuchAccount");
        });
    }
}
