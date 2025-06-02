package ru.dulfi.console;

import ru.dulfi.dao.OwnerDao;
import ru.dulfi.dao.PetDao;
import ru.dulfi.db.DatabaseInitializer;
import ru.dulfi.db.EntityManagerUtil;
import ru.dulfi.domain.Owner;
import ru.dulfi.domain.Pet;
import ru.dulfi.domain.PetColor;
import ru.dulfi.exception.EntityNotFoundException;
import ru.dulfi.exception.ValidationException;
import ru.dulfi.service.OwnerService;
import ru.dulfi.service.PetService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private final Scanner scanner;
    private final OwnerService ownerService;
    private final PetService petService;

    public ConsoleApp() {
        this.scanner = new Scanner(System.in);
        OwnerDao ownerDao = new OwnerDao();
        PetDao petDao = new PetDao();
        this.ownerService = new OwnerService(ownerDao);
        this.petService = new PetService(petDao, ownerDao);
    }

    public static void main(String[] args) {
        try {
            DatabaseInitializer.initialize();
            new ConsoleApp().start();
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            EntityManagerUtil.close();
        }
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readIntInput();
            running = handleMainChoice(choice);
        }
    }

    private void printMainMenu() {
        System.out.println("\n=== Главное меню ===");
        System.out.println("1. Владельцы");
        System.out.println("2. Питомцы");
        System.out.println("0. Выход");
        System.out.print("Выберите: ");
    }

    private boolean handleMainChoice(int choice) {
        switch (choice) {
            case 1:
                handleOwners();
                return true;
            case 2:
                handlePets();
                return true;
            case 0:
                return false;
            default:
                System.out.println("Неверный выбор!");
                return true;
        }
    }

    private void handleOwners() {
        boolean back = false;
        while (!back) {
            printOwnersMenu();
            int choice = readIntInput();
            back = handleOwnerChoice(choice);
        }
    }

    private void printOwnersMenu() {
        System.out.println("\n=== Меню владельцев ===");
        System.out.println("1. Добавить владельца");
        System.out.println("2. Показать всех владельцев");
        System.out.println("3. Найти владельца по ID");
        System.out.println("4. Обновить владельца");
        System.out.println("5. Удалить владельца");
        System.out.println("0. Назад");
        System.out.print("Выберите: ");
    }

    private boolean handleOwnerChoice(int choice) {
        try {
            switch (choice) {
                case 1:
                    addOwner();
                    return false;
                case 2:
                    showAllOwners();
                    return false;
                case 3:
                    findOwnerById();
                    return false;
                case 4:
                    updateOwner();
                    return false;
                case 5:
                    deleteOwner();
                    return false;
                case 0:
                    return true;
                default:
                    System.out.println("Неверный выбор!");
                    return false;
            }
        } catch (ValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
            return false;
        } catch (EntityNotFoundException e) {
            System.out.println("Ошибка: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Произошла ошибка: " + e.getMessage());
            return false;
        }
    }

    private void handlePets() {
        boolean back = false;
        while (!back) {
            printPetsMenu();
            int choice = readIntInput();
            back = handlePetChoice(choice);
        }
    }

    private void printPetsMenu() {
        System.out.println("\n=== Меню питомцев ===");
        System.out.println("1. Добавить питомца");
        System.out.println("2. Показать всех питомцев");
        System.out.println("3. Найти питомца по ID");
        System.out.println("4. Обновить питомца");
        System.out.println("5. Удалить питомца");
        System.out.println("6. Добавить друга");
        System.out.println("7. Удалить друга");
        System.out.println("8. Показать друзей");
        System.out.println("0. Назад");
        System.out.print("Выберите: ");
    }

    private boolean handlePetChoice(int choice) {
        try {
            switch (choice) {
                case 1:
                    addPet();
                    return false;
                case 2:
                    showAllPets();
                    return false;
                case 3:
                    findPetById();
                    return false;
                case 4:
                    updatePet();
                    return false;
                case 5:
                    deletePet();
                    return false;
                case 6:
                    addFriend();
                    return false;
                case 7:
                    removeFriend();
                    return false;
                case 8:
                    showFriends();
                    return false;
                case 0:
                    return true;
                default:
                    System.out.println("Неверный выбор!");
                    return false;
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            return false;
        }
    }

    private void addOwner() {
        System.out.println("\n=== Добавление владельца ===");
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        
        System.out.print("Введите дату рождения (yyyy-MM-dd): ");
        LocalDate birthDate = readDate();

        Owner owner = new Owner();
        owner.setName(name);
        owner.setBirthDate(birthDate);

        try {
            owner = ownerService.save(owner);
            System.out.println("Владелец успешно добавлен: " + owner);
        } catch (ValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        }
    }

    private void showAllOwners() {
        System.out.println("\n=== Все владельцы ===");
        List<Owner> owners = ownerService.getAllWithPets();
        if (owners.isEmpty()) {
            System.out.println("Владельцев нет");
        } else {
            owners.forEach(owner -> {
                System.out.println(owner);
                if (!owner.getPets().isEmpty()) {
                    System.out.println("  Питомцы:");
                    owner.getPets().forEach(pet -> System.out.println("    - " + pet));
                }
            });
        }
    }

    private void findOwnerById() {
        System.out.print("\nВведите ID владельца: ");
        Long id = readLongInput();
        if (id == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            Owner owner = ownerService.getById(id);
            System.out.println("Найден владелец: " + owner);
            if (!owner.getPets().isEmpty()) {
                System.out.println("  Питомцы:");
                owner.getPets().forEach(pet -> System.out.println("    - " + pet));
            }
        } catch (EntityNotFoundException e) {
            System.out.println("Владелец не найден");
        }
    }

    private void updateOwner() {
        System.out.print("\nВведите ID владельца для обновления: ");
        Long id = readLongInput();
        if (id == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            Owner owner = ownerService.getById(id);
            System.out.println("Текущие данные владельца: " + owner);

            System.out.print("Введите новое имя (Enter для пропуска): ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) {
                owner.setName(name);
            }

            System.out.print("Введите новую дату рождения (yyyy-MM-dd, Enter для пропуска): ");
            String dateStr = scanner.nextLine();
            if (!dateStr.isEmpty()) {
                try {
                    LocalDate birthDate = LocalDate.parse(dateStr);
                    owner.setBirthDate(birthDate);
                } catch (DateTimeParseException e) {
                    System.out.println("Неверный формат даты");
                    return;
                }
            }

            owner = ownerService.update(owner);
            System.out.println("Владелец успешно обновлен: " + owner);
        } catch (EntityNotFoundException e) {
            System.out.println("Владелец не найден");
        } catch (ValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        }
    }

    private void deleteOwner() {
        System.out.print("\nВведите ID владельца для удаления: ");
        Long id = readLongInput();
        if (id == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            ownerService.deleteById(id);
            System.out.println("Владелец успешно удален");
        } catch (EntityNotFoundException e) {
            System.out.println("Владелец не найден");
        }
    }

    private void addPet() {
        System.out.println("\n=== Добавление питомца ===");
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        System.out.print("Введите дату рождения (yyyy-MM-dd): ");
        LocalDate birthDate = readDate();

        System.out.print("Введите породу: ");
        String breed = scanner.nextLine();

        System.out.print("Выберите цвет (BLACK, WHITE, GRAY, BROWN, ORANGE): ");
        PetColor color = readPetColor();

        System.out.print("Введите ID владельца: ");
        Long ownerId = readLongInput();
        if (ownerId == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            Pet pet = new Pet();
            pet.setName(name);
            pet.setBirthDate(birthDate);
            pet.setBreed(breed);
            pet.setColor(color);
            
            Owner owner = new Owner();
            owner.setId(ownerId);
            pet.setOwner(owner);

            pet = petService.save(pet);
            System.out.println("Питомец успешно добавлен: " + pet);
        } catch (ValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showAllPets() {
        System.out.println("\n=== Все питомцы ===");
        List<Pet> pets = petService.getAllWithOwners();
        if (pets.isEmpty()) {
            System.out.println("Питомцев нет");
        } else {
            pets.forEach(pet -> {
                System.out.println(pet);
                if (pet.getOwner() != null) {
                    System.out.println("  Владелец: " + pet.getOwner().getName());
                }
            });
        }
    }

    private void findPetById() {
        System.out.print("\nВведите ID питомца: ");
        Long id = readLongInput();
        if (id == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            Pet pet = petService.getById(id);
            System.out.println("Найден питомец: " + pet);
            if (pet.getOwner() != null) {
                System.out.println("  Владелец: " + pet.getOwner().getName());
            }
        } catch (EntityNotFoundException e) {
            System.out.println("Питомец не найден");
        }
    }

    private void updatePet() {
        System.out.print("\nВведите ID питомца для обновления: ");
        Long id = readLongInput();
        if (id == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            Pet pet = petService.getById(id);
            System.out.println("Текущие данные питомца: " + pet);

            System.out.print("Введите новое имя (Enter для пропуска): ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) {
                pet.setName(name);
            }

            System.out.print("Введите новую дату рождения (yyyy-MM-dd, Enter для пропуска): ");
            String dateStr = scanner.nextLine();
            if (!dateStr.isEmpty()) {
                try {
                    LocalDate birthDate = LocalDate.parse(dateStr);
                    pet.setBirthDate(birthDate);
                } catch (DateTimeParseException e) {
                    System.out.println("Неверный формат даты");
                    return;
                }
            }

            System.out.print("Введите новую породу (Enter для пропуска): ");
            String breed = scanner.nextLine();
            if (!breed.isEmpty()) {
                pet.setBreed(breed);
            }

            System.out.print("Выберите новый цвет (BLACK, WHITE, GRAY, BROWN, ORANGE, Enter для пропуска): ");
            String colorStr = scanner.nextLine();
            if (!colorStr.isEmpty()) {
                try {
                    PetColor color = PetColor.valueOf(colorStr.toUpperCase());
                    pet.setColor(color);
                } catch (IllegalArgumentException e) {
                    System.out.println("Неверный цвет");
                    return;
                }
            }

            System.out.print("Введите новый ID владельца (Enter для пропуска): ");
            String ownerIdStr = scanner.nextLine();
            if (!ownerIdStr.isEmpty()) {
                try {
                    Long ownerId = Long.parseLong(ownerIdStr);
                    Owner owner = ownerService.getById(ownerId);
                    pet.setOwner(owner);
                } catch (NumberFormatException e) {
                    System.out.println("Неверный формат ID");
                    return;
                } catch (EntityNotFoundException e) {
                    System.out.println("Владелец не найден");
                    return;
                }
            }

            pet = petService.update(pet);
            System.out.println("Питомец успешно обновлен: " + pet);
        } catch (EntityNotFoundException e) {
            System.out.println("Питомец не найден");
        } catch (ValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        }
    }

    private void deletePet() {
        System.out.print("\nВведите ID питомца для удаления: ");
        Long id = readLongInput();
        if (id == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            petService.deleteById(id);
            System.out.println("Питомец успешно удален");
        } catch (EntityNotFoundException e) {
            System.out.println("Питомец не найден");
        }
    }

    private void addFriend() {
        System.out.print("\nВведите ID питомца: ");
        Long petId = readLongInput();
        if (petId == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        System.out.print("Введите ID друга: ");
        Long friendId = readLongInput();
        if (friendId == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            petService.addFriend(petId, friendId);
            System.out.println("Друг успешно добавлен");
        } catch (EntityNotFoundException e) {
            System.out.println("Питомец не найден");
        } catch (ValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        }
    }

    private void removeFriend() {
        System.out.print("\nВведите ID питомца: ");
        Long petId = readLongInput();
        if (petId == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        System.out.print("Введите ID друга: ");
        Long friendId = readLongInput();
        if (friendId == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            petService.removeFriend(petId, friendId);
            System.out.println("Друг успешно удален");
        } catch (EntityNotFoundException e) {
            System.out.println("Питомец не найден");
        } catch (ValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        }
    }

    private void showFriends() {
        System.out.print("\nВведите ID питомца: ");
        Long petId = readLongInput();
        if (petId == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            List<Pet> friends = petService.getFriends(petId);
            if (friends.isEmpty()) {
                System.out.println("У питомца нет друзей");
            } else {
                System.out.println("Друзья питомца:");
                friends.forEach(friend -> System.out.println("  - " + friend));
            }
        } catch (EntityNotFoundException e) {
            System.out.println("Питомец не найден");
        }
    }

    private int readIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат числа");
            return -1;
        }
    }

    private Long readLongInput() {
        try {
            return Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate readDate() {
        while (true) {
            try {
                return LocalDate.parse(scanner.nextLine());
            } catch (DateTimeParseException e) {
                System.out.println("Неверный формат даты. Используйте формат yyyy-MM-dd");
            }
        }
    }

    private PetColor readPetColor() {
        while (true) {
            try {
                return PetColor.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Неверный цвет. Выберите один из: BLACK, WHITE, GRAY, BROWN, ORANGE");
            }
        }
    }
} 