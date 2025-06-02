package ru.dulfi.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ru.dulfi.domain.Owner;
import ru.dulfi.domain.Pet;
import ru.dulfi.domain.PetColor;
import ru.dulfi.domain.User;
import ru.dulfi.exception.EntityNotFoundException;
import ru.dulfi.exception.ValidationException;
import ru.dulfi.service.OwnerService;
import ru.dulfi.service.PetService;
import ru.dulfi.service.PetAccessService;
import ru.dulfi.repository.UserRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

@EntityScan("ru.dulfi.domain")
@EnableJpaRepositories("ru.dulfi.repository")
@ComponentScan("ru.dulfi")
@SpringBootApplication
public class ConsoleApp implements CommandLineRunner {

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private PetService petService;

    @Autowired
    private PetAccessService petAccessService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Scanner scanner = new Scanner(System.in);
    private User currentUser;

    @Override
    public void run(String... args) {
        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showAuthMenu() {
        System.out.println("\n=== Меню аутентификации ===");
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.println("0. Выход");

        int choice = readIntInput();
        switch (choice) {
            case 1 -> login();
            case 2 -> register();
            case 0 -> exit(0);
            default -> System.out.println("Неверный выбор");
        }
    }

    private void showMainMenu() {
        System.out.println("\n=== Главное меню ===");
        System.out.println("Текущий пользователь: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        System.out.println("1. Управление владельцами");
        System.out.println("2. Управление питомцами");
        System.out.println("3. Выйти из системы");
        System.out.println("0. Завершить работу");

        int choice = readIntInput();
        switch (choice) {
            case 1 -> showOwnerMenu();
            case 2 -> showPetMenu();
            case 3 -> logout();
            case 0 -> exit(0);
            default -> System.out.println("Неверный выбор");
        }
    }

    private void login() {
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ValidationException("Пользователь не найден"));

            if (!passwordEncoder.matches(password, user.getPassword())) {
                System.out.println("Неверный пароль");
                return;
            }

            currentUser = user;
            System.out.println("Успешный вход в систему");
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
    }

    private void register() {
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        System.out.print("Введите дату рождения (в формате YYYY-MM-DD): ");
        String birthDateStr = scanner.nextLine();
        
        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            
            Owner owner = new Owner();
            owner.setName(username);
            owner.setBirthDate(birthDate);
            owner = ownerService.save(owner);
            
            user.setOwner(owner);
            user.setRole("USER");
            user = userRepository.save(user);
            
            System.out.println("Регистрация успешна");
        } catch (DateTimeParseException e) {
            System.out.println("Ошибка: Неверный формат даты. Используйте формат YYYY-MM-DD");
        }
    }

    private void logout() {
        currentUser = null;
        System.out.println("Выход из системы выполнен");
    }

    private void showOwnerMenu() {
        while (true) {
            System.out.println("\n=== Управление владельцами ===");
            System.out.println("1. Показать всех владельцев");
            System.out.println("2. Найти владельца по ID");
            System.out.println("3. Добавить владельца");
            System.out.println("4. Обновить владельца");
            System.out.println("5. Удалить владельца");
            System.out.println("0. Назад");

            int choice = readIntInput();
            switch (choice) {
                case 1 -> showAllOwners();
                case 2 -> findOwnerById();
                case 3 -> addOwner();
                case 4 -> updateOwner();
                case 5 -> deleteOwner();
                case 0 -> { return; }
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    private void showPetMenu() {
        while (true) {
            System.out.println("\n=== Управление питомцами ===");
            System.out.println("1. Показать всех питомцев");
            System.out.println("2. Найти питомца по ID");
            System.out.println("3. Добавить питомца");
            System.out.println("4. Обновить питомца");
            System.out.println("5. Удалить питомца");
            System.out.println("6. Найти питомцев по цвету");
            System.out.println("7. Найти питомцев по цвету и длине хвоста");
            System.out.println("0. Назад");

            int choice = readIntInput();
            switch (choice) {
                case 1 -> showAllPets();
                case 2 -> findPetById();
                case 3 -> addPet();
                case 4 -> updatePet();
                case 5 -> deletePet();
                case 6 -> findPetsByColor();
                case 7 -> findPetsByColorAndTailLength();
                case 0 -> { return; }
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    @Transactional
    public void showAllOwners() {
        System.out.println("\n=== Все владельцы ===");
        List<Owner> owners = ownerService.getAll();
        for (Owner owner : owners) {
            System.out.println(owner);
        }
    }

    @Transactional
    public void findOwnerById() {
        System.out.print("\nВведите ID владельца: ");
        Long id = readLongInput();
        if (id == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            Owner owner = ownerService.getById(id);
            System.out.println("Найден владелец: " + owner);
        } catch (EntityNotFoundException e) {
            System.out.println("Владелец не найден");
        }
    }

    @Transactional
    public void addOwner() {
        System.out.println("\n=== Добавление владельца ===");
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        System.out.print("Введите дату рождения (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine();

        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            System.out.println("Неверный формат даты");
            return;
        }

        try {
            Owner owner = new Owner();
            owner.setName(name);
            owner.setBirthDate(birthDate);
            owner = ownerService.save(owner);

            if (currentUser.getOwner() == null) {
                currentUser.setOwner(owner);
                userRepository.save(currentUser);
            }

            System.out.println("Владелец успешно добавлен: " + owner);
        } catch (ValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Transactional
    public void updateOwner() {
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
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteOwner() {
        System.out.print("\nВведите ID владельца для удаления: ");
        Long id = readLongInput();
        if (id == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            Owner owner = ownerService.getById(id);
            ownerService.deleteByEntity(owner);
            System.out.println("Владелец успешно удален");
        } catch (EntityNotFoundException e) {
            System.out.println("Владелец не найден");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Transactional
    public void showAllPets() {
        System.out.println("\n=== Все котики ===");
        List<Owner> owners = ownerService.getAll();
        for (Owner owner : owners) {
            System.out.println("\nВладелец: " + owner.getName() + " (ID: " + owner.getId() + ")");
            System.out.println("Питомцы:");
            for (Pet pet : owner.getPets()) {
                System.out.println("  - " + pet.getName() + 
                    " (ID: " + pet.getId() + 
                    ", порода: " + pet.getBreed() + 
                    ", цвет: " + pet.getColor() + 
                    ", длина хвоста: " + pet.getTailLength() + ")");
            }
        }
    }

    @Transactional
    public void findPetById() {
        System.out.print("\nВведите ID котика: ");
        Long id = readLongInput();
        if (id == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            Pet pet = petService.getById(id);
            System.out.println("Найден котик: " + pet);
            if (pet.getOwner() != null) {
                System.out.println("  Владелец: " + pet.getOwner().getName());
            }
        } catch (EntityNotFoundException e) {
            System.out.println("Котик не найден");
        }
    }

    @Transactional
    public void addPet() {
        System.out.println("\n=== Добавление котика ===");
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        System.out.print("Введите дату рождения (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine();
        System.out.print("Введите породу: ");
        String breed = scanner.nextLine();
        System.out.print("Выберите цвет (BLACK, WHITE, GRAY, BROWN, ORANGE): ");
        String colorStr = scanner.nextLine();
        System.out.print("Введите длину хвоста: ");
        String tailLengthStr = scanner.nextLine();

        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            System.out.println("Неверный формат даты");
            return;
        }

        PetColor color;
        try {
            color = PetColor.valueOf(colorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный цвет");
            return;
        }

        Double tailLength;
        try {
            tailLength = Double.parseDouble(tailLengthStr);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат длины хвоста");
            return;
        }

        try {
            Pet pet = new Pet();
            pet.setName(name);
            pet.setBirthDate(birthDate);
            pet.setBreed(breed);
            pet.setColor(color);
            pet.setTailLength(tailLength);

            if ("ADMIN".equals(currentUser.getRole())) {
                System.out.print("Введите ID владельца: ");
                Long ownerId = readLongInput();
                if (ownerId == null) {
                    System.out.println("Неверный формат ID владельца");
                    return;
                }
                Owner owner = ownerService.getById(ownerId);
                pet.setOwner(owner);
            } else {
                if (currentUser.getOwner() == null) {
                    System.out.println("У вас нет привязанного владельца");
                    return;
                }
                pet.setOwner(currentUser.getOwner());
            }

            pet = petService.save(pet);
            System.out.println("Питомец успешно добавлен: " + pet);
        } catch (ValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Transactional
    public void updatePet() {
        System.out.print("\nВведите ID питомца для обновления: ");
        Long id = readLongInput();
        if (id == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            Pet pet = petService.getById(id);
            try {
                petAccessService.checkAccessForUser(pet, currentUser);
            } catch (Exception e) {
                System.out.println("Ошибка доступа: " + e.getMessage());
                return;
            }

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

            System.out.print("Введите новую длину хвоста (Enter для пропуска): ");
            String tailLengthStr = scanner.nextLine();
            if (!tailLengthStr.isEmpty()) {
                try {
                    Double tailLength = Double.parseDouble(tailLengthStr);
                    pet.setTailLength(tailLength);
                } catch (NumberFormatException e) {
                    System.out.println("Неверный формат длины хвоста");
                    return;
                }
            }

            if ("ADMIN".equals(currentUser.getRole())) {
                System.out.print("Введите новый ID владельца (Enter для пропуска): ");
                String ownerIdStr = scanner.nextLine();
                if (!ownerIdStr.isEmpty()) {
                    try {
                        Long ownerId = Long.parseLong(ownerIdStr);
                        Owner newOwner = ownerService.getById(ownerId);
                        pet.setOwner(newOwner);
                    } catch (NumberFormatException e) {
                        System.out.println("Неверный формат ID владельца");
                        return;
                    } catch (EntityNotFoundException e) {
                        System.out.println("Владелец не найден");
                        return;
                    }
                }
            }

            pet = petService.update(pet);
            System.out.println("Питомец успешно обновлен: " + pet);
        } catch (EntityNotFoundException e) {
            System.out.println("Питомец не найден");
        } catch (ValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Transactional
    public void deletePet() {
        System.out.print("\nВведите ID питомца для удаления: ");
        Long id = readLongInput();
        if (id == null) {
            System.out.println("Неверный формат ID");
            return;
        }

        try {
            Pet pet = petService.getById(id);
            try {
                petAccessService.checkAccessForUser(pet, currentUser);
            } catch (Exception e) {
                System.out.println("Ошибка доступа: " + e.getMessage());
                return;
            }

            petService.deleteByEntity(pet);
            System.out.println("Питомец успешно удален");
        } catch (EntityNotFoundException e) {
            System.out.println("Питомец не найден");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Transactional
    public void findPetsByColor() {
        System.out.print("\nВыберите цвет (BLACK, WHITE, GRAY, BROWN, ORANGE): ");
        String colorStr = scanner.nextLine();

        PetColor color;
        try {
            color = PetColor.valueOf(colorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный цвет");
            return;
        }

        List<Pet> pets = petService.getPetsByColor(color);
        System.out.println("\n=== Котики цвета " + color + " ===");
        for (Pet pet : pets) {
            System.out.println(pet);
            if (pet.getOwner() != null) {
                System.out.println("  Владелец: " + pet.getOwner().getName());
            }
        }
    }

    @Transactional
    public void findPetsByColorAndTailLength() {
        System.out.print("\nВыберите цвет (BLACK, WHITE, GRAY, BROWN, ORANGE): ");
        String colorStr = scanner.nextLine();
        System.out.print("Введите минимальную длину хвоста: ");
        String tailLengthStr = scanner.nextLine();

        PetColor color;
        try {
            color = PetColor.valueOf(colorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный цвет");
            return;
        }

        Double minTailLength;
        try {
            minTailLength = Double.parseDouble(tailLengthStr);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат длины хвоста");
            return;
        }

        List<Pet> pets = petService.getPetsByColorAndTailLength(color, minTailLength);
        System.out.println("\n=== Котики цвета " + color + " с длиной хвоста > " + minTailLength + " ===");
        for (Pet pet : pets) {
            System.out.println(pet);
            if (pet.getOwner() != null) {
                System.out.println("  Владелец: " + pet.getOwner().getName());
            }
        }
    }

    private Integer readIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long readLongInput() {
        try {
            return Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsoleApp.class, args);
    }
} 