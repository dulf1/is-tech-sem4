package ru.dulfi.petservice.controller;

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
import ru.dulfi.petservice.domain.Pet;
import ru.dulfi.petservice.domain.PetColor;
import ru.dulfi.petservice.dto.PetDTO;
import ru.dulfi.petservice.service.PetService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pets")
@Tag(name = "Питомцы", description = "API для управления питомцами")
public class PetController {
    private final PetService petService;

    @Autowired
    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    @Operation(summary = "Получить всех питомцев", description = "Возвращает список всех питомцев с пагинацией.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение списка")
    })
    public ResponseEntity<Page<PetDTO>> getAllPets(
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(petService.getAll(pageable).map(this::convertToDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить питомца по ID", description = "Возвращает питомца по его ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение"),
        @ApiResponse(responseCode = "404", description = "Питомец не найден")
    })
    public ResponseEntity<PetDTO> getPetById(@PathVariable Long id) {
        Pet pet = petService.getById(id);
        return ResponseEntity.ok(convertToDTO(pet));
    }

    @PostMapping
    @Operation(summary = "Создать питомца", description = "Создает нового питомца.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное создание")
    })
    public ResponseEntity<PetDTO> createPet(@RequestBody PetDTO petDTO) {
        Pet pet = convertToEntity(petDTO);
        return ResponseEntity.ok(convertToDTO(petService.save(pet)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить питомца", description = "Обновляет существующего питомца.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное обновление"),
        @ApiResponse(responseCode = "404", description = "Питомец не найден")
    })
    public ResponseEntity<PetDTO> updatePet(@PathVariable Long id, @RequestBody PetDTO petDTO) {
        Pet pet = petService.getById(id);
        
        Pet updatedPet = convertToEntity(petDTO);
        updatedPet.setId(id);
        return ResponseEntity.ok(convertToDTO(petService.update(updatedPet)));
    }

    @PutMapping("/{id}/tail-length")
    @Operation(summary = "Установить длину хвоста питомца", description = "Устанавливает длину хвоста для питомца")
    public ResponseEntity<PetDTO> setTailLength(
            @Parameter(description = "ID питомца") @PathVariable Long id,
            @Parameter(description = "Длина хвоста") @RequestParam Double tailLength) {
        Pet pet = petService.getById(id);
        
        pet.setTailLength(tailLength);
        return ResponseEntity.ok(convertToDTO(petService.update(pet)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить питомца", description = "Удаляет питомца по его ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное удаление"),
        @ApiResponse(responseCode = "404", description = "Питомец не найден")
    })
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        Pet pet = petService.getById(id);
        
        petService.deleteByEntity(pet);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-color/{color}")
    @Operation(summary = "Получить питомцев по цвету", description = "Возвращает список питомцев указанного цвета с пагинацией.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение")
    })
    public ResponseEntity<Page<PetDTO>> getPetsByColor(
            @Parameter(description = "Цвет питомца") @PathVariable PetColor color,
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(petService.getPetsByColor(color, pageable).map(this::convertToDTO));
    }

    @GetMapping("/by-color-and-tail")
    @Operation(summary = "Получить питомцев по цвету и минимальной длине хвоста",
              description = "Возвращает список питомцев указанного цвета с длиной хвоста больше минимальной, с пагинацией.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение")
    })
    public ResponseEntity<Page<PetDTO>> getPetsByColorAndTailLength(
            @Parameter(description = "Цвет питомца") @RequestParam PetColor color,
            @Parameter(description = "Минимальная длина хвоста") @RequestParam Double minTailLength,
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(petService.getPetsByColorAndTailLength(color, minTailLength, pageable).map(this::convertToDTO));
    }
    
    @GetMapping("/by-owner/{ownerId}")
    @Operation(summary = "Получить питомцев по ID владельца", description = "Возвращает список питомцев, принадлежащих указанному владельцу.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное получение")
    })
    public ResponseEntity<List<PetDTO>> getPetsByOwnerId(@PathVariable Long ownerId) {
        List<Pet> pets = petService.getPetsByOwnerId(ownerId);
        List<PetDTO> petDTOs = pets.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(petDTOs);
    }

    private PetDTO convertToDTO(Pet pet) {
        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setBirthDate(pet.getBirthDate());
        dto.setBreed(pet.getBreed());
        dto.setColor(pet.getColor());
        dto.setTailLength(pet.getTailLength());
        dto.setOwnerId(pet.getOwnerId());
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
        pet.setOwnerId(dto.getOwnerId());
        return pet;
    }
} 