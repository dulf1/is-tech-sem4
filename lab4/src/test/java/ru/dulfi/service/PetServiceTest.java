package ru.dulfi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.dulfi.domain.Owner;
import ru.dulfi.domain.Pet;
import ru.dulfi.domain.PetColor;
import ru.dulfi.exception.ResourceNotFoundException;
import ru.dulfi.exception.ValidationException;
import ru.dulfi.repository.PetRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    @Test
    public void testGetAll() {
        Pet pet = createTestPet();
        Page<Pet> page = new PageImpl<>(Collections.singletonList(pet));
        when(petRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Pet> result = petService.getAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(pet, result.getContent().get(0));
        verify(petRepository).findAll(PageRequest.of(0, 10));
    }

    @Test
    public void testGetById() {
        Pet pet = createTestPet();
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        Pet result = petService.getById(1L);

        assertNotNull(result);
        assertEquals(pet, result);
        verify(petRepository).findById(1L);
    }

    @Test
    public void testGetByIdNotFound() {
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> petService.getById(1L));
        verify(petRepository).findById(1L);
    }

    @Test
    public void testSave() {
        Pet pet = createTestPet();
        when(petRepository.save(any())).thenReturn(pet);

        Pet result = petService.save(pet);

        assertNotNull(result);
        assertEquals(pet, result);
        verify(petRepository).save(pet);
    }

    @Test
    public void testSaveWithoutOwner() {
        Pet pet = createTestPet();
        pet.setOwner(null);

        assertThrows(ValidationException.class, () -> petService.save(pet));
        verify(petRepository, never()).save(any());
    }

    @Test
    public void testUpdate() {
        Pet pet = createTestPet();
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(petRepository.save(any())).thenReturn(pet);

        Pet result = petService.update(pet);

        assertNotNull(result);
        assertEquals(pet, result);
        verify(petRepository).save(pet);
    }

    @Test
    public void testUpdateNotFound() {
        Pet pet = createTestPet();
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> petService.update(pet));
        verify(petRepository, never()).save(any());
    }

    @Test
    public void testDeleteByEntity() {
        Pet pet = createTestPet();
        doNothing().when(petRepository).deleteById(any());

        petService.deleteByEntity(pet);

        verify(petRepository).deleteById(pet.getId());
    }

    @Test
    public void testGetPetsByColor() {
        Pet pet = createTestPet();
        Page<Pet> page = new PageImpl<>(Collections.singletonList(pet));
        when(petRepository.findByColor(any(), any())).thenReturn(page);

        Page<Pet> result = petService.getPetsByColor(PetColor.BLACK, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        Pet resultPet = result.getContent().get(0);
        assertEquals(pet.getId(), resultPet.getId());
        assertEquals(pet.getName(), resultPet.getName());
        assertEquals(pet.getBreed(), resultPet.getBreed());
        assertEquals(pet.getColor(), resultPet.getColor());
        assertEquals(pet.getTailLength(), resultPet.getTailLength());
        assertEquals(pet.getOwner().getId(), resultPet.getOwner().getId());
        verify(petRepository).findByColor(PetColor.BLACK, PageRequest.of(0, 10));
    }

    @Test
    public void testGetPetsByColorAndTailLength() {
        Pet pet = createTestPet();
        Page<Pet> page = new PageImpl<>(Collections.singletonList(pet));
        when(petRepository.findByColorAndTailLengthGreaterThan(any(), any(), any())).thenReturn(page);

        Page<Pet> result = petService.getPetsByColorAndTailLength(PetColor.BLACK, 25.0, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        Pet resultPet = result.getContent().get(0);
        assertEquals(pet.getId(), resultPet.getId());
        assertEquals(pet.getName(), resultPet.getName());
        assertEquals(pet.getBreed(), resultPet.getBreed());
        assertEquals(pet.getColor(), resultPet.getColor());
        assertEquals(pet.getTailLength(), resultPet.getTailLength());
        assertEquals(pet.getOwner().getId(), resultPet.getOwner().getId());
        verify(petRepository).findByColorAndTailLengthGreaterThan(PetColor.BLACK, 25.0, PageRequest.of(0, 10));
    }

    private Pet createTestPet() {
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("Мурзик");
        pet.setBirthDate(LocalDate.now());
        pet.setBreed("Британский");
        pet.setColor(PetColor.BLACK);
        pet.setTailLength(25.0);
        
        Owner owner = new Owner();
        owner.setId(1L);
        pet.setOwner(owner);
        
        return pet;
    }
} 