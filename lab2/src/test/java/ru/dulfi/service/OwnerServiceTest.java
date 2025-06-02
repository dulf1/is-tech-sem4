package ru.dulfi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.dulfi.dao.OwnerDao;
import ru.dulfi.domain.Owner;
import ru.dulfi.exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {
    @Mock
    private OwnerDao ownerDao;
    
    private OwnerService ownerService;

    @BeforeEach
    void setUp() {
        ownerService = new OwnerService(ownerDao);
    }

    @Test
    void testSave_ValidOwner_ShouldSaveSuccessfully() {
        Owner owner = createTestOwner();
        when(ownerDao.save(owner)).thenReturn(owner);

        Owner savedOwner = ownerService.save(owner);
        
        assertNotNull(savedOwner);
        verify(ownerDao).save(owner);
    }

    @Test
    void testSave_NullOwner_ShouldThrowValidationException() {
        assertThrows(ValidationException.class, () -> ownerService.save(null));
    }

    @Test
    void testSave_EmptyName_ShouldThrowValidationException() {
        Owner owner = createTestOwner();
        owner.setName("");
        assertThrows(ValidationException.class, () -> ownerService.save(owner));
    }

    @Test
    void testSave_NullBirthDate_ShouldThrowValidationException() {
        Owner owner = createTestOwner();
        owner.setBirthDate(null);
        assertThrows(ValidationException.class, () -> ownerService.save(owner));
    }

    @Test
    void testGetById_ExistingOwner_ShouldReturnOwner() {
        Long id = 1L;
        Owner owner = createTestOwner();
        when(ownerDao.getById(id)).thenReturn(owner);

        Owner foundOwner = ownerService.getById(id);
        
        assertNotNull(foundOwner);
        assertEquals(owner, foundOwner);
        verify(ownerDao).getById(id);
    }

    @Test
    void testUpdate_ValidOwner_ShouldUpdateSuccessfully() {
        Owner owner = createTestOwner();
        when(ownerDao.getById(owner.getId())).thenReturn(owner);
        when(ownerDao.update(owner)).thenReturn(owner);

        Owner updatedOwner = ownerService.update(owner);

        assertNotNull(updatedOwner);
        verify(ownerDao).getById(owner.getId());
        verify(ownerDao).update(owner);
    }

    @Test
    void testUpdate_NullOwner_ShouldThrowValidationException() {
        assertThrows(ValidationException.class, () -> ownerService.update(null));
    }

    @Test
    void testUpdate_NullId_ShouldThrowValidationException() {
        Owner owner = createTestOwner();
        owner.setId(null);
        assertThrows(ValidationException.class, () -> ownerService.update(owner));
    }

    @Test
    void testDeleteByEntity_NullOwner_ShouldThrowValidationException() {
        assertThrows(ValidationException.class, () -> ownerService.deleteByEntity(null));
    }

    @Test
    void testDeleteByEntity_NullId_ShouldThrowValidationException() {
        Owner owner = createTestOwner();
        owner.setId(null);
        assertThrows(ValidationException.class, () -> ownerService.deleteByEntity(owner));
    }

    @Test
    void testDeleteAll_ShouldDeleteAllOwners() {
        ownerService.deleteAll();
        verify(ownerDao).deleteAll();
    }

    @Test
    void testGetAll_ShouldReturnAllOwners() {
        List<Owner> owners = new ArrayList<>();
        owners.add(createTestOwner());
        when(ownerDao.getAll()).thenReturn(owners);

        List<Owner> allOwners = ownerService.getAll();
        
        assertNotNull(allOwners);
        assertEquals(1, allOwners.size());
        verify(ownerDao).getAll();
    }

    @Test
    void testGetAllWithPets_ShouldReturnAllOwnersWithPets() {
        List<Owner> owners = new ArrayList<>();
        owners.add(createTestOwner());
        when(ownerDao.getAllWithPets()).thenReturn(owners);

        List<Owner> allOwners = ownerService.getAllWithPets();
        
        assertNotNull(allOwners);
        assertEquals(1, allOwners.size());
        verify(ownerDao).getAllWithPets();
    }

    private Owner createTestOwner() {
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setName("Oleg Kudrin");
        owner.setBirthDate(LocalDate.now());
        owner.setPets(new ArrayList<>());
        return owner;
    }
} 