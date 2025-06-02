package ru.dulfi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.dulfi.domain.Owner;
import ru.dulfi.service.OwnerService;

@RestController
@RequestMapping("/api/owners")
@Tag(name = "Владельцы", description = "API для управления владельцами питомцев")
@SecurityRequirement(name = "basicAuth")
public class OwnerController {
    private final OwnerService ownerService;

    @Autowired
    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    @Operation(summary = "Получить всех владельцев", description = "Возвращает список всех владельцев с пагинацией. Доступно всем авторизованным пользователям.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение списка"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация")
    })
    public ResponseEntity<Page<Owner>> getAllOwners(
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(ownerService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить владельца по ID", description = "Возвращает владельца по его ID. Доступно всем авторизованным пользователям.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
        @ApiResponse(responseCode = "404", description = "Владелец не найден")
    })
    public ResponseEntity<Owner> getOwnerById(@PathVariable Long id) {
        return ResponseEntity.ok(ownerService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Создать владельца", description = "Создает нового владельца. Доступно только администраторам.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное создание"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<Owner> createOwner(@RequestBody Owner owner) {
        return ResponseEntity.ok(ownerService.save(owner));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить владельца", description = "Обновляет существующего владельца. Доступно только администраторам.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное обновление"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
        @ApiResponse(responseCode = "404", description = "Владелец не найден")
    })
    public ResponseEntity<Owner> updateOwner(@PathVariable Long id, @RequestBody Owner owner) {
        owner.setId(id);
        return ResponseEntity.ok(ownerService.update(owner));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить владельца", description = "Удаляет владельца по его ID. Доступно только администраторам.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное удаление"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
        @ApiResponse(responseCode = "404", description = "Владелец не найден")
    })
    public ResponseEntity<Void> deleteOwner(@PathVariable Long id) {
        ownerService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск владельцев по имени", description = "Возвращает список владельцев, чьи имена содержат указанную подстроку. Доступно всем авторизованным пользователям.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный поиск"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация")
    })
    public ResponseEntity<Page<Owner>> searchOwners(
            @Parameter(description = "Подстрока для поиска") @RequestParam String name,
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(ownerService.searchByName(name, pageable));
    }
} 