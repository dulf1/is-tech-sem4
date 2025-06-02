package ru.dulfi.domain;

import java.math.BigDecimal;

/**
 * Builder для создания объектов {@link Account}.
 */
public class AccountBuilder {
    private String accountNumber;
    private BigDecimal balance = BigDecimal.ZERO;
    /**
     * Конструктор по умолчанию
     */
    public AccountBuilder() {
    }
    /**
     * Устанавливает уникальный номер счёта
     * @param accountNumber уникальный номер
     * @return текущий экземпляр билдера
     */
    public AccountBuilder accountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    /**
     * Устанавливает начальный баланс счёта.
     * @param balance начальный баланс
     * @return текущий экземпляр билдера
     */
    public AccountBuilder balance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    /**
     * Создаёт объект {@link Account} с заданными параметрами.
     * @return новый экземпляр {@link Account}
     */
    public Account build() {
        return new Account(accountNumber, balance);
    }
}
