package ru.dulfi.ownerservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.dulfi.ownerservice.domain.Owner;
import ru.dulfi.ownerservice.dto.OwnerDTO;
import ru.dulfi.ownerservice.service.OwnerService;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/owners")
@Tag(name = "Владельцы", description = "API для управления владельцами")
public class OwnerController {
    private final OwnerService ownerService;

    @Autowired
    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    @Operation(summary = "Получить всех владельцев", description = "Возвращает список всех владельцев с пагинацией.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение списка")
    })
    public ResponseEntity<Page<OwnerDTO>> getAllOwners(
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(ownerService.getAll(pageable).map(this::convertToDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить владельца по ID", description = "Возвращает владельца по его ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение"),
        @ApiResponse(responseCode = "404", description = "Владелец не найден")
    })
    public ResponseEntity<OwnerDTO> getOwnerById(@PathVariable Long id) {
        Owner owner = ownerService.getById(id);
        return ResponseEntity.ok(convertToDTO(owner));
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск владельцев по имени", description = "Возвращает список владельцев, имя которых содержит указанную подстроку.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение списка")
    })
    public ResponseEntity<Page<OwnerDTO>> searchOwnersByName(
            @Parameter(description = "Подстрока для поиска в имени") @RequestParam String name,
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(ownerService.findByNameContaining(name, pageable).map(this::convertToDTO));
    }

    @PostMapping
    @Operation(summary = "Создать владельца", description = "Создает нового владельца.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное создание")
    })
    public ResponseEntity<OwnerDTO> createOwner(@RequestBody OwnerDTO ownerDTO) {
        Owner owner = convertToEntity(ownerDTO);
        return ResponseEntity.ok(convertToDTO(ownerService.save(owner)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить владельца", description = "Обновляет существующего владельца.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное обновление"),
        @ApiResponse(responseCode = "404", description = "Владелец не найден")
    })
    public ResponseEntity<OwnerDTO> updateOwner(@PathVariable Long id, @RequestBody OwnerDTO ownerDTO) {
        Owner owner = ownerService.getById(id);
        
        Owner updatedOwner = convertToEntity(ownerDTO);
        updatedOwner.setId(id);
        return ResponseEntity.ok(convertToDTO(ownerService.update(updatedOwner)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить владельца", description = "Удаляет владельца по его ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное удаление"),
        @ApiResponse(responseCode = "404", description = "Владелец не найден")
    })
    public ResponseEntity<Void> deleteOwner(@PathVariable Long id) {
        Owner owner = ownerService.getById(id);
        
        ownerService.deleteByEntity(owner);
        return ResponseEntity.ok().build();
    }

    private OwnerDTO convertToDTO(Owner owner) {
        OwnerDTO dto = new OwnerDTO();
        dto.setId(owner.getId());
        dto.setName(owner.getName());
        dto.setBirthDate(owner.getBirthDate());
        dto.setPetIds(new ArrayList<>());
        return dto;
    }

    private Owner convertToEntity(OwnerDTO dto) {
        Owner owner = new Owner();
        owner.setId(dto.getId());
        owner.setName(dto.getName());
        owner.setBirthDate(dto.getBirthDate());
        return owner;
    }
} 