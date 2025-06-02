package ru.dulfi.service;

import ru.dulfi.domain.Account;
import ru.dulfi.domain.Operation;
import ru.dulfi.exceptions.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Интерфейс, определяющий базовые операции системы банкомата.
 */
public interface BankService {
    /**
     * Создает новый счет с заданным начальным балансом.
     * Номер счета генерируется автоматически.
     * @param balance начальный баланс
     * @return созданный счет
     */
    Account createAccount(BigDecimal balance);
    /**
     * Возвращает текущий баланс счета.
     * @param accountNumber номер счета
     * @return баланс счета
     * @throws AccountNotFoundException если счет не найден
     */
    BigDecimal getBalance(String accountNumber) throws AccountNotFoundException;
    /**
     * Пополняет счет на заданную сумму.
     * @param accountNumber номер счета
     * @param amount сумма пополнения
     * @throws AccountNotFoundException если счет не найден
     */
    void deposit(String accountNumber, BigDecimal amount) throws AccountNotFoundException;
    /**
     * Снимает с счета заданную сумму.
     * @param accountNumber номер счета
     * @param amount сумма для снятия
     * @throws AccountNotFoundException если счет не найден
     * @throws InsufficientFundsException если на счете недостаточно средств
     */
    void withdraw(String accountNumber, BigDecimal amount) throws AccountNotFoundException, InsufficientFundsException;
    /**
     * Возвращает историю операций по счету.
     * @param accountNumber номер счета
     * @return список операций
     * @throws AccountNotFoundException если счет не найден
     */
    List<Operation> getOperations(String accountNumber) throws AccountNotFoundException;
}
