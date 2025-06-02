package ru.dulfi.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.dulfi.domain.Owner;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerDaoTest {
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityTransaction transaction;
    @Mock
    private Query query;

    private OwnerDao ownerDao;
    private Owner testOwner;

    @BeforeEach
    void setUp() {
        reset(entityManager, transaction, query);
        
        when(entityManager.getTransaction()).thenReturn(transaction);
        doNothing().when(transaction).begin();
        doNothing().when(transaction).commit();
        
        ownerDao = new OwnerDao();
        ownerDao.setEntityManager(entityManager);
        
        testOwner = createTestOwner();
    }

    @Test
    void testSave() {
        when(entityManager.merge(any(Owner.class))).thenReturn(testOwner);

        Owner savedOwner = ownerDao.save(testOwner);

        assertNotNull(savedOwner);
        verify(transaction).begin();
        verify(entityManager).merge(any(Owner.class));
        verify(transaction).commit();
    }

    @Test
    void testUpdate() {
        when(entityManager.merge(any(Owner.class))).thenReturn(testOwner);

        Owner updatedOwner = ownerDao.update(testOwner);

        assertNotNull(updatedOwner);
        verify(transaction).begin();
        verify(entityManager).merge(any(Owner.class));
        verify(transaction).commit();
    }

    @Test
    void testDeleteById() {
        when(entityManager.find(Owner.class, 1L)).thenReturn(testOwner);
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        ownerDao.deleteById(1L);

        verify(transaction).begin();
        verify(entityManager).find(Owner.class, 1L);
        verify(entityManager).createQuery("UPDATE Pet p SET p.owner = NULL WHERE p.owner.id = :ownerId");
        verify(query).setParameter("ownerId", 1L);
        verify(query).executeUpdate();
        verify(entityManager).remove(testOwner);
        verify(transaction).commit();
    }

    @Test
    void testDeleteByEntity() {
        when(entityManager.merge(any(Owner.class))).thenReturn(testOwner);

        ownerDao.deleteByEntity(testOwner);

        verify(transaction).begin();
        verify(entityManager).merge(testOwner);
        verify(entityManager).remove(testOwner);
        verify(transaction).commit();
    }

    @Test
    void testDeleteAll() {
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        ownerDao.deleteAll();

        verify(transaction).begin();
        verify(entityManager).createQuery("DELETE FROM Owner");
        verify(query).executeUpdate();
        verify(transaction).commit();
    }

    private Owner createTestOwner() {
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setName("Test Owner");
        owner.setBirthDate(LocalDate.now());
        owner.setPets(new ArrayList<>());
        return owner;
    }
} 