package ru.dulfi.gatewayservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.dulfi.gatewayservice.dto.OwnerDTO;
import ru.dulfi.gatewayservice.dto.PetDTO;
import ru.dulfi.gatewayservice.service.GatewayService;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
@Tag(name = "Gateway API", description = "API для взаимодействия с микросервисами")
public class GatewayController {

    private final GatewayService gatewayService;

    @Autowired
    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @GetMapping("/owners")
    @Operation(summary = "Получить всех владельцев с их питомцами", description = "Возвращает список всех владельцев с их питомцами.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка")
    })
    public ResponseEntity<List<OwnerDTO>> getAllOwners(
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле для сортировки") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Направление сортировки") @RequestParam(defaultValue = "asc") String sortDirection)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(gatewayService.getAllOwnersWithPets(page, size, sortBy, sortDirection));
    }

    @GetMapping("/owners/{id}")
    @Operation(summary = "Получить владельца по ID", description = "Возвращает владельца с его питомцами по ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение"),
            @ApiResponse(responseCode = "404", description = "Владелец не найден")
    })
    public ResponseEntity<OwnerDTO> getOwnerById(@PathVariable Long id) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(gatewayService.getOwnerWithPets(id));
    }

    @PostMapping("/owners")
    @Operation(summary = "Создать владельца", description = "Создает нового владельца и его питомцев.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное создание")
    })
    public ResponseEntity<OwnerDTO> createOwner(@RequestBody OwnerDTO ownerDTO) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(gatewayService.createOwnerWithPets(ownerDTO));
    }

    @PutMapping("/owners/{id}")
    @Operation(summary = "Обновить владельца", description = "Обновляет существующего владельца.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное обновление"),
            @ApiResponse(responseCode = "404", description = "Владелец не найден")
    })
    public ResponseEntity<OwnerDTO> updateOwner(@PathVariable Long id, @RequestBody OwnerDTO ownerDTO)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(gatewayService.updateOwner(id, ownerDTO));
    }

    @DeleteMapping("/owners/{id}")
    @Operation(summary = "Удалить владельца", description = "Удаляет владельца и всех его питомцев.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление"),
            @ApiResponse(responseCode = "404", description = "Владелец не найден")
    })
    public ResponseEntity<Void> deleteOwner(@PathVariable Long id) throws ExecutionException, InterruptedException {
        gatewayService.deleteOwner(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pets")
    @Operation(summary = "Получить всех питомцев с их владельцами", description = "Возвращает список всех питомцев с информацией о владельцах.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка")
    })
    public ResponseEntity<List<PetDTO>> getAllPets(
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле для сортировки") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Направление сортировки") @RequestParam(defaultValue = "asc") String sortDirection)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(gatewayService.getAllPetsWithOwners(page, size, sortBy, sortDirection));
    }

    @GetMapping("/pets/{id}")
    @Operation(summary = "Получить питомца по ID", description = "Возвращает питомца с информацией о владельце по ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение"),
            @ApiResponse(responseCode = "404", description = "Питомец не найден")
    })
    public ResponseEntity<PetDTO> getPetById(@PathVariable Long id) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(gatewayService.getPetWithOwner(id));
    }

    @GetMapping("/pets/search")
    @Operation(summary = "Поиск питомцев по имени", description = "Возвращает список питомцев, имя которых содержит указанную подстроку.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка")
    })
    public ResponseEntity<List<PetDTO>> searchPetsByName(
            @Parameter(description = "Подстрока для поиска в имени") @RequestParam String name,
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Поле для сортировки") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Направление сортировки") @RequestParam(defaultValue = "asc") String sortDirection)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(gatewayService.searchPetsByName(name, page, size, sortBy, sortDirection));
    }

    @PostMapping("/pets")
    @Operation(summary = "Создать питомца", description = "Создает нового питомца.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное создание")
    })
    public ResponseEntity<PetDTO> createPet(@RequestBody PetDTO petDTO) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(gatewayService.createPet(petDTO));
    }

    @PutMapping("/pets/{id}")
    @Operation(summary = "Обновить питомца", description = "Обновляет существующего питомца.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное обновление"),
            @ApiResponse(responseCode = "404", description = "Питомец не найден")
    })
    public ResponseEntity<PetDTO> updatePet(@PathVariable Long id, @RequestBody PetDTO petDTO)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(gatewayService.updatePet(id, petDTO));
    }

    @DeleteMapping("/pets/{id}")
    @Operation(summary = "Удалить питомца", description = "Удаляет питомца.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное удаление"),
            @ApiResponse(responseCode = "404", description = "Питомец не найден")
    })
    public ResponseEntity<Void> deletePet(@PathVariable Long id) throws ExecutionException, InterruptedException {
        gatewayService.deletePet(id);
        return ResponseEntity.ok().build();
    }
} 