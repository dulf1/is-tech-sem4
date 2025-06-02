package ru.dulfi.service;

import ru.dulfi.domain.Account;
import ru.dulfi.domain.AccountBuilder;
import ru.dulfi.domain.Operation;
import ru.dulfi.exceptions.AccountNotFoundException;
import ru.dulfi.exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса {@link BankService}.
 * Номер счета генерируется автоматически с помощью {@link UUID} и создается через Builder.
 */
public class BankServiceImp implements BankService {
    private final Map<String, Account> accounts = new HashMap<>();
    private final List<Operation> operations = new ArrayList<>();

    /**
     * Конструктор по дефолту
     */
    public BankServiceImp() {}

    @Override
    public Account createAccount(BigDecimal initialBalance) {
        String accountNumber = UUID.randomUUID().toString();
        Account account = new AccountBuilder()
                .accountNumber(accountNumber)
                .balance(initialBalance)
                .build();
        accounts.put(accountNumber, account);
        operations.add(new Operation(accountNumber, "CREATE_ACCOUNT", initialBalance));
        return account;
    }

    @Override
    public BigDecimal   getBalance(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Счёт не найден: " + accountNumber);
        }
        return account.getBalance();
    }

    @Override
    public void deposit(String accountNumber, BigDecimal amount) throws AccountNotFoundException {
        Account account = getAccount(accountNumber);
        account.deposit(amount);
        operations.add(new Operation(accountNumber, "DEPOSIT", amount));
    }

    @Override
    public void withdraw(String accountNumber, BigDecimal amount) throws AccountNotFoundException, InsufficientFundsException {
        Account account = getAccount(accountNumber);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientFundsException("Сумма должна быть > 0");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Недостаточно средств");
        }
        account.withdraw(amount);
        operations.add(new Operation(accountNumber, "WITHDRAW", amount));
    }

    @Override
    public List<Operation> getOperations(String accountNumber) throws AccountNotFoundException {
        getAccount(accountNumber);
        List<Operation> result = operations.stream()
                .filter(operation -> operation.getAccountNumber().equals(accountNumber))
                .collect(Collectors.toList());
        return result;
    }

    /**
     * Находит счет по номеру.
     * @param accountNumber номер счета
     * @return найденный счет
     * @throws AccountNotFoundException если счет не найден
     */
    private Account getAccount(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Счёт не найден: " + accountNumber);
        }
        return account;
    }
}
