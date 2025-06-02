package ru.dulfi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.dulfi.domain.User;
import ru.dulfi.service.UserService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "API для управления аутентификацией и пользователями")
@SecurityRequirement(name = "basicAuth")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя", description = "Создает нового пользователя с ролью USER. Доступно всем.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешная регистрация"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/register/admin")
    @Operation(summary = "Регистрация нового администратора", description = "Создает нового пользователя с ролью ADMIN. Доступно только администраторам.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешная регистрация"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<User> registerAdmin(@RequestBody User user) {
        return ResponseEntity.ok(userService.registerAdmin(user));
    }

    @GetMapping("/current")
    @Operation(summary = "Получить текущего пользователя", description = "Возвращает информацию о текущем авторизованном пользователе. Доступно всем авторизованным пользователям.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация")
    })
    public ResponseEntity<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход из системы", description = "Завершает текущую сессию пользователя. Доступно всем авторизованным пользователям.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный выход"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация")
    })
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
    }
} 