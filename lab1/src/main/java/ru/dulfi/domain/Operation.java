package ru.dulfi.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Класс, представляющий операцию над счётом (создание, пополнение, снятие).
 */
@Getter
public class Operation {
    /**
     * -- GETTER --
     *  Возвращает номер счёта, к которому относится операция.
     */
    private final String accountNumber;
    /**
     * -- GETTER --
     *  Возвращает тип операции.
     */
    private final String operationType;
    /**
     * -- GETTER --
     *  Возвращает сумму операции.
     */
    private final BigDecimal amount;
    /**
     * -- GETTER --
     *  Возвращает дату и время проведения операции.
     */
    private final LocalDateTime dateTime;

    /**
     * Конструктор для создания операции.
     * @param accountNumber номер счёта, к которому относится операция
     * @param type тип операции (например, "CREATE_ACCOUNT", "DEPOSIT", "WITHDRAW")
     * @param amount сумма операции
     */
    public Operation(String accountNumber, String type, BigDecimal amount) {
        this.accountNumber = accountNumber;
        this.operationType = type;
        this.amount = amount;
        this.dateTime = LocalDateTime.now();
    }

}
