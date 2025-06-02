package ru.dulfi.console;

import ru.dulfi.domain.Account;
import ru.dulfi.exceptions.AccountNotFoundException;
import ru.dulfi.exceptions.InsufficientFundsException;
import ru.dulfi.service.BankService;
import ru.dulfi.service.BankServiceImp;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Точка входа в консольное приложение T-Bank.
 * Позволяет пользователю создавать счёт, проверять баланс, пополнять и снимать деньги, а также просматривать историю операций.
 */
public class App {
    /**
     * Конструктор по умолчанию для класса App.
     */
    public App() {
    }
    /**
     * Точка входа в приложение.
     * @param args Аргументы командной строки.
     */
    public static void main(String[] args) {
        BankService bankService = new BankServiceImp();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать в T-Bank!");
        while (true) {
            System.out.println("\nВыберите действие:");
            System.out.println("1. Создать счёт");
            System.out.println("2. Посмотреть баланс");
            System.out.println("3. Пополнить счёт");
            System.out.println("4. Снять деньги");
            System.out.println("5. Посмотреть историю операций");
            System.out.println("0. Выход");
            System.out.print(">>> ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Введите начальный баланс: ");
                    BigDecimal initBalance = new BigDecimal(scanner.nextLine());
                    try {
                        Account account = bankService.createAccount(initBalance);
                        System.out.println("Счёт создан! Ваш ключ для входа в банк: \n" + account.getAccountNumber());
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;

                case "2":
                    System.out.print("Введите номер счёта: ");
                    String acc1 = scanner.nextLine();
                    try {
                        BigDecimal balance = bankService.getBalance(acc1);
                        System.out.println("Баланс: " + balance);
                    } catch (AccountNotFoundException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;

                case "3":
                    System.out.print("Введите номер счёта: ");
                    String acc2 = scanner.nextLine();
                    System.out.print("Введите сумму пополнения: ");
                    BigDecimal depAmount = new BigDecimal(scanner.nextLine());
                    try {
                        bankService.deposit(acc2, depAmount);
                        System.out.println("Счёт пополнен!");
                    } catch (AccountNotFoundException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;

                case "4":
                    System.out.print("Введите номер счёта: ");
                    String acc3 = scanner.nextLine();
                    System.out.print("Введите сумму для снятия: ");
                    BigDecimal wAmount = new BigDecimal(scanner.nextLine());
                    try {
                        bankService.withdraw(acc3, wAmount);
                        System.out.println("Деньги сняты!");
                    } catch (AccountNotFoundException | InsufficientFundsException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;

                case "5":
                    System.out.print("Введите номер счёта: ");
                    String acc4 = scanner.nextLine();
                    try {
                        var ops = bankService.getOperations(acc4);
                        if (ops.isEmpty()) {
                            System.out.println("Нет операций.");
                        } else {
                            System.out.println("История операций:");
                            for (var op : ops) {
                                System.out.printf("%s | %s | %.2f | %s\n",
                                        op.getAccountNumber(),
                                        op.getOperationType(),
                                        op.getAmount(),
                                        op.getDateTime()
                                );
                            }
                        }
                    } catch (AccountNotFoundException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;

                case "0":
                    System.out.println("Выход из программы...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Неверный ввод!");
                    break;
            }
        }
    }
}
