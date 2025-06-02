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
import ru.dulfi.domain.Pet;
import ru.dulfi.domain.PetColor;
import ru.dulfi.dto.PetDTO;
import ru.dulfi.service.OwnerService;
import ru.dulfi.service.PetService;
import ru.dulfi.service.PetAccessService;

@RestController
@RequestMapping("/api/pets")
@Tag(name = "Китики", description = "API для управления котиками")
@SecurityRequirement(name = "basicAuth")
public class PetController {
    private final PetService petService;
    private final OwnerService ownerService;
    private final PetAccessService petAccessService;

    @Autowired
    public PetController(PetService petService, OwnerService ownerService, PetAccessService petAccessService) {
        this.petService = petService;
        this.ownerService = ownerService;
        this.petAccessService = petAccessService;
    }

    @GetMapping
    @Operation(summary = "Получить всех котиков", description = "Возвращает список всех котиков с пагинацией. Доступно всем авторизованным пользователям.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение списка"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация")
    })
    public ResponseEntity<Page<PetDTO>> getAllPets(
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(petService.getAll(pageable).map(this::convertToDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить котика по ID", description = "Возвращает котика по его ID. Доступно всем авторизованным пользователям.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
        @ApiResponse(responseCode = "404", description = "Котик не найден")
    })
    public ResponseEntity<PetDTO> getPetById(@PathVariable Long id) {
        Pet pet = petService.getById(id);
        petAccessService.checkAccess(pet);
        return ResponseEntity.ok(convertToDTO(pet));
    }

    @PostMapping
    @Operation(summary = "Создать котика", description = "Создает нового котика. Доступно владельцам и администраторам.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное создание"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<PetDTO> createPet(@RequestBody PetDTO petDTO) {
        Pet pet = convertToEntity(petDTO);
        return ResponseEntity.ok(convertToDTO(petService.save(pet)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить котика", description = "Обновляет существующего котика. Доступно только владельцу котика и администраторам.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное обновление"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
        @ApiResponse(responseCode = "404", description = "Котик не найден")
    })
    public ResponseEntity<PetDTO> updatePet(@PathVariable Long id, @RequestBody PetDTO petDTO) {
        Pet pet = petService.getById(id);
        petAccessService.checkAccess(pet);
        
        Pet updatedPet = convertToEntity(petDTO);
        updatedPet.setId(id);
        return ResponseEntity.ok(convertToDTO(petService.update(updatedPet)));
    }

    @PutMapping("/{id}/tail-length")
    @Operation(summary = "Установить длину хвоста котика", description = "Устанавливает длину хвоста для котика")
    public ResponseEntity<PetDTO> setTailLength(
            @Parameter(description = "ID котика") @PathVariable Long id,
            @Parameter(description = "Длина хвоста") @RequestParam Double tailLength) {
        Pet pet = petService.getById(id);
        petAccessService.checkAccess(pet);
        
        pet.setTailLength(tailLength);
        return ResponseEntity.ok(convertToDTO(petService.update(pet)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить котика", description = "Удаляет котика по его ID. Доступно только владельцу котика и администраторам.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное удаление"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
        @ApiResponse(responseCode = "404", description = "Котик не найден")
    })
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        Pet pet = petService.getById(id);
        petAccessService.checkAccess(pet);
        
        petService.deleteByEntity(pet);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-color/{color}")
    @Operation(summary = "Получить котиков по цвету", description = "Возвращает список котиков указанного цвета с пагинацией. Доступно всем авторизованным пользователям.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация")
    })
    public ResponseEntity<Page<PetDTO>> getPetsByColor(
            @Parameter(description = "Цвет котика") @PathVariable PetColor color,
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(petService.getPetsByColor(color, pageable).map(this::convertToDTO));
    }

    @GetMapping("/by-color-and-tail")
    @Operation(summary = "Получить котиков по цвету и минимальной длине хвоста",
              description = "Возвращает список котиков указанного цвета с длиной хвоста больше минимальной, с пагинацией. Доступно всем авторизованным пользователям.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение"),
        @ApiResponse(responseCode = "401", description = "Требуется аутентификация")
    })
    public ResponseEntity<Page<PetDTO>> getPetsByColorAndTailLength(
            @Parameter(description = "Цвет котика") @RequestParam PetColor color,
            @Parameter(description = "Минимальная длина хвоста") @RequestParam Double minTailLength,
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(petService.getPetsByColorAndTailLength(color, minTailLength, pageable).map(this::convertToDTO));
    }

    private PetDTO convertToDTO(Pet pet) {
        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setBirthDate(pet.getBirthDate());
        dto.setBreed(pet.getBreed());
        dto.setColor(pet.getColor());
        dto.setTailLength(pet.getTailLength());
        if (pet.getOwner() != null) {
            dto.setOwnerId(pet.getOwner().getId());
        }
        return dto;
    }

    private Pet convertToEntity(PetDTO dto) {
        Pet pet = new Pet();
        pet.setId(dto.getId());
        pet.setName(dto.getName());
        pet.setBirthDate(dto.getBirthDate());
        pet.setBreed(dto.getBreed());
        pet.setColor(dto.getColor());
        pet.setTailLength(dto.getTailLength());
        if (dto.getOwnerId() != null) {
            Owner owner = ownerService.getById(dto.getOwnerId());
            pet.setOwner(owner);
        }
        return pet;
    }
} 