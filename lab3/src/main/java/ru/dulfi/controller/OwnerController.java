package ru.dulfi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.dulfi.domain.Owner;
import ru.dulfi.domain.Pet;
import ru.dulfi.dto.OwnerDTO;
import ru.dulfi.service.OwnerService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/owners")
@Tag(name = "Owner Controller", description = "API для управления владельцами")
public class OwnerController {
    private final OwnerService ownerService;

    @Autowired
    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    @Operation(summary = "Получить всех владельцев", description = "Возвращает список всех владельцев с пагинацией")
    public ResponseEntity<Page<OwnerDTO>> getAllOwners(
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(ownerService.getAll(pageable).map(this::convertToDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить владельца по ID", description = "Возвращает владельца по его ID")
    public ResponseEntity<OwnerDTO> getOwnerById(@PathVariable Long id) {
        return ResponseEntity.ok(convertToDTO(ownerService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Создать владельца", description = "Создает нового владельца")
    public ResponseEntity<OwnerDTO> createOwner(@RequestBody OwnerDTO ownerDTO) {
        Owner owner = convertToEntity(ownerDTO);
        return ResponseEntity.ok(convertToDTO(ownerService.save(owner)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить владельца", description = "Обновляет существующего владельца")
    public ResponseEntity<OwnerDTO> updateOwner(@PathVariable Long id, @RequestBody OwnerDTO ownerDTO) {
        Owner owner = convertToEntity(ownerDTO);
        owner.setId(id);
        return ResponseEntity.ok(convertToDTO(ownerService.update(owner)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить владельца", description = "Удаляет владельца по его ID")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long id) {
        Owner owner = new Owner();
        owner.setId(id);
        ownerService.deleteByEntity(owner);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск владельцев", description = "Поиск владельцев по имени с пагинацией")
    public ResponseEntity<Page<OwnerDTO>> searchOwners(
            @Parameter(description = "Имя владельца") @RequestParam(required = false) String name,
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(ownerService.searchByName(name, pageable).map(this::convertToDTO));
    }

    private OwnerDTO convertToDTO(Owner owner) {
        OwnerDTO dto = new OwnerDTO();
        dto.setId(owner.getId());
        dto.setName(owner.getName());
        dto.setBirthDate(owner.getBirthDate());
        if (owner.getPets() != null) {
            dto.setPetIds(owner.getPets().stream()
                    .map(Pet::getId)
                    .collect(Collectors.toList()));
        }
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