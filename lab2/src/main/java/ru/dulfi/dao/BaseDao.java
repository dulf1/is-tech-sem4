package ru.dulfi.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import ru.dulfi.db.EntityManagerUtil;
import ru.dulfi.exception.EntityNotFoundException;
import ru.dulfi.exception.DatabaseException;

import java.util.List;

public abstract class BaseDao<T> {
    protected EntityManager entityManager;
    private final Class<T> entityClass;

    protected BaseDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected EntityManager getEntityManager() {
        return entityManager != null ? entityManager : EntityManagerUtil.getEntityManager();
    }

    protected void closeEntityManager(EntityManager em) {
        if (entityManager == null && em != null) {
            em.close();
        }
    }

    public T save(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T mergedEntity = em.merge(entity);
            tx.commit();
            return mergedEntity;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new DatabaseException("Ошибка при сохранении сущности", e);
        } finally {
            closeEntityManager(em);
        }
    }

    public void deleteById(Long id) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entity = em.find(entityClass, id);
            if (entity == null) {
                throw new EntityNotFoundException("Сущность с id " + id + " не найдена");
            }
            em.remove(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            if (e instanceof EntityNotFoundException) {
                throw e;
            }
            throw new DatabaseException("Ошибка при удалении сущности", e);
        } finally {
            closeEntityManager(em);
        }
    }

    public void deleteByEntity(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T mergedEntity = em.merge(entity);
            em.remove(mergedEntity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new DatabaseException("Ошибка при удалении сущности", e);
        } finally {
            closeEntityManager(em);
        }
    }

    public void deleteAll() {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("DELETE FROM " + entityClass.getSimpleName()).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new DatabaseException("Ошибка при удалении всех сущностей", e);
        } finally {
            closeEntityManager(em);
        }
    }

    public T update(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T mergedEntity = em.merge(entity);
            tx.commit();
            return mergedEntity;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new DatabaseException("Ошибка при обновлении сущности", e);
        } finally {
            closeEntityManager(em);
        }
    }

    public T getById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            closeEntityManager(em);
        }
    }

    public List<T> getAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                    .getResultList();
        } finally {
            closeEntityManager(em);
        }
    }

    public List<T> getAllWithPets() {
        throw new UnsupportedOperationException("Not implemented");
    }
}