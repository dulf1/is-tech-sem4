package ru.dulfi.domain;

import lombok.Getter;
import ru.dulfi.exceptions.InsufficientFundsException;
import java.math.BigDecimal;

/**
 * Класс, представляющий банковский счёт.
 */
@Getter
public class Account {
    /**
     * -- GETTER --
     *  Возвращает уникальный номер счёта.
     */
    private final String accountNumber;
    /**
     * -- GETTER --
     *  Возвращает текущий баланс счёта.
     */
    private BigDecimal balance;

    /**
     * Конструктор для создания нового аккаунта.
     * @param accountNumber уникальный идентификатор счёта.
     * @param initialBalance начальный баланс счёта, не может быть отрицательным
     * @throws IllegalArgumentException если начальный баланс отрицательный
     */
    public Account(String accountNumber, BigDecimal initialBalance) {
        this.accountNumber = accountNumber;
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Начальный баланс не может быть отрицательным");
        }
        this.balance = initialBalance;
    }

    /**
     * Пополняет счёт на заданную сумму.
     * @param amount сумма для пополнения, должна быть > 0
     * @throws IllegalArgumentException если сумма для пополнения не положительная
     */
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Сумма для депозита должна быть > 0");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * Снимает с счёта заданную сумму.
     * @param amount сумма для снятия, должна быть > 0 и не превышать текущий баланс
     * @throws IllegalArgumentException если сумма не положительная или превышает баланс
     * @throws InsufficientFundsException если на счёте недостаточно средств
     */
    public void withdraw(BigDecimal amount) throws InsufficientFundsException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма для депозита должна быть > 0");
        }
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Недостаточно средств");
        }
        this.balance = this.balance.add(amount);
    }
}