package ru.dulfi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.dulfi.domain.Owner;
import ru.dulfi.exception.ResourceNotFoundException;
import ru.dulfi.repository.OwnerRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    @Test
    public void testGetAll() {
        Owner owner = createTestOwner();
        when(ownerRepository.findAll()).thenReturn(Collections.singletonList(owner));

        List<Owner> result = ownerService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(owner, result.get(0));
        verify(ownerRepository).findAll();
    }

    @Test
    public void testGetById() {
        Owner owner = createTestOwner();
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));

        Owner result = ownerService.getById(1L);

        assertNotNull(result);
        assertEquals(owner, result);
        verify(ownerRepository).findById(1L);
    }

    @Test
    public void testGetByIdNotFound() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ownerService.getById(1L));
        verify(ownerRepository).findById(1L);
    }

    @Test
    public void testSave() {
        Owner owner = createTestOwner();
        when(ownerRepository.save(any())).thenReturn(owner);

        Owner result = ownerService.save(owner);

        assertNotNull(result);
        assertEquals(owner, result);
        verify(ownerRepository).save(owner);
    }

    @Test
    public void testUpdate() {
        Owner owner = createTestOwner();
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(ownerRepository.save(any())).thenReturn(owner);

        Owner result = ownerService.update(owner);

        assertNotNull(result);
        assertEquals(owner, result);
        verify(ownerRepository).save(owner);
    }

    @Test
    public void testUpdateNotFound() {
        Owner owner = createTestOwner();
        when(ownerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ownerService.update(owner));
        verify(ownerRepository, never()).save(any());
    }

    @Test
    public void testDeleteByEntity() {
        Owner owner = createTestOwner();
        doNothing().when(ownerRepository).deleteById(any());

        ownerService.deleteByEntity(owner);

        verify(ownerRepository).deleteById(owner.getId());
    }

    private Owner createTestOwner() {
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setName("Олег");
        owner.setBirthDate(LocalDate.of(2005, 8, 27));
        return owner;
    }
} 