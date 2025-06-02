package ru.dulfi.exceptions;

/**
 * Исключение, выбрасываемое, когда счёт с заданным номером не найден.
 */
public class AccountNotFoundException extends Exception {
    /**
     * Конструктор исключения.
     * @param message сообщение об ошибке
     */
    public AccountNotFoundException(String message) {
        super(message);
    }
}
