package ru.dulfi.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ru.dulfi.db.EntityManagerUtil;
import ru.dulfi.domain.Pet;
import ru.dulfi.exception.EntityNotFoundException;
import ru.dulfi.exception.ValidationException;

import java.util.List;

public class PetDao extends BaseDao<Pet> {
    public PetDao() {
        super(Pet.class);
    }

    @Override
    public List<Pet> getAll() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Pet p ORDER BY p.id", Pet.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteAll() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery("DELETE FROM pet_friends").executeUpdate();
            em.createQuery("DELETE FROM Pet").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Ошибка при удалении всех питомцев", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteByEntity(Pet pet) {
        if (pet == null || pet.getId() == null) {
            throw new ValidationException("Питомец или его ID не могут быть null");
        }

        EntityManager em = EntityManagerUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Pet managedPet = em.find(Pet.class, pet.getId());
            if (managedPet == null) {
                throw new EntityNotFoundException("Питомец с id " + pet.getId() + " не найден");
            }
            em.createNativeQuery("DELETE FROM pet_friends WHERE pet_id = ? OR friend_id = ?")
                    .setParameter(1, pet.getId())
                    .setParameter(2, pet.getId())
                    .executeUpdate();
            em.remove(managedPet);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            if (e instanceof EntityNotFoundException) {
                throw e;
            }
            throw new RuntimeException("Ошибка при удалении питомца", e);
        } finally {
            em.close();
        }
    }

    public void addFriend(Long petId, Long friendId) {
        if (petId.equals(friendId)) {
            throw new ValidationException("Питомец не может дружить сам с собой");
        }

        EntityManager em = EntityManagerUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Pet pet = em.find(Pet.class, petId);
            Pet friend = em.find(Pet.class, friendId);

            if (pet == null) {
                throw new EntityNotFoundException("Питомец с id " + petId + " не найден");
            }
            if (friend == null) {
                throw new EntityNotFoundException("Питомец-друг с id " + friendId + " не найден");
            }

            if (pet.getFriends().contains(friend)) {
                throw new ValidationException("Питомцы уже дружат");
            }

            pet.getFriends().add(friend);
            friend.getFriends().add(pet);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            if (e instanceof EntityNotFoundException || e instanceof ValidationException) {
                throw e;
            }
            throw new RuntimeException("Ошибка при добавлении друга", e);
        } finally {
            em.close();
        }
    }

    public void removeFriend(Long petId, Long friendId) {
        if (petId.equals(friendId)) {
            throw new ValidationException("Питомец не может удалить себя из друзей");
        }

        EntityManager em = EntityManagerUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Pet pet = em.find(Pet.class, petId);
            Pet friend = em.find(Pet.class, friendId);

            if (pet == null) {
                throw new EntityNotFoundException("Питомец с id " + petId + " не найден");
            }
            if (friend == null) {
                throw new EntityNotFoundException("Питомец-друг с id " + friendId + " не найден");
            }

            if (!pet.getFriends().contains(friend)) {
                throw new ValidationException("Питомцы не дружат");
            }

            pet.getFriends().remove(friend);
            friend.getFriends().remove(pet);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            if (e instanceof EntityNotFoundException || e instanceof ValidationException) {
                throw e;
            }
            throw new RuntimeException("Ошибка при удалении друга", e);
        } finally {
            em.close();
        }
    }

    public List<Pet> getFriends(Long petId) {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            Pet pet = em.find(Pet.class, petId);
            if (pet == null) {
                throw new EntityNotFoundException("Питомец с id " + petId + " не найден");
            }

            return em.createQuery("SELECT DISTINCT f FROM Pet p JOIN p.friends f LEFT JOIN FETCH f.owner WHERE p.id = :petId ORDER BY f.id", Pet.class)
                    .setParameter("petId", petId)
                    .getResultList();
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении списка друзей", e);
        } finally {
            em.close();
        }
    }

    public List<Pet> getAllWithOwners() {
        try (EntityManager em = EntityManagerUtil.getEntityManager()) {
            return em.createQuery("SELECT DISTINCT p FROM Pet p LEFT JOIN FETCH p.owner ORDER BY p.id", Pet.class)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении списка питомцев с владельцами", e);
        }
    }
} 