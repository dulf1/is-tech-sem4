package ru.dulfi.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ru.dulfi.domain.Owner;
import ru.dulfi.exception.EntityNotFoundException;
import ru.dulfi.exception.DatabaseException;

import java.util.List;

public class OwnerDao extends BaseDao<Owner> {
    public OwnerDao() {
        super(Owner.class);
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Owner owner = em.find(Owner.class, id);
            if (owner == null) {
                throw new EntityNotFoundException("Владелец с id " + id + " не найден");
            }
            em.createQuery("UPDATE Pet p SET p.owner = NULL WHERE p.owner.id = :ownerId")
                    .setParameter("ownerId", id)
                    .executeUpdate();
            em.remove(owner);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            if (e instanceof EntityNotFoundException) {
                throw e;
            }
            throw new DatabaseException("Ошибка при удалении владельца", e);
        } finally {
            closeEntityManager(em);
        }
    }

    @Override
    public List<Owner> getAllWithPets() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT DISTINCT o FROM Owner o LEFT JOIN FETCH o.pets ORDER BY o.id",
                Owner.class
            ).getResultList();
        } finally {
            closeEntityManager(em);
        }
    }
} 