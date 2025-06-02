package ru.dulfi.exceptions;

/**
 * Исключение, выбрасываемое, когда на счёте недостаточно средств для совершения операции.
 */
public class InsufficientFundsException extends Exception {
    /**
     * Конструктор исключения.
     * @param message сообщение об ошибке
     */
    public InsufficientFundsException(String message) {
        super(message);
    }
}
