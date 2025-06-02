package ru.dulfi.service;

import ru.dulfi.dao.OwnerDao;
import ru.dulfi.domain.Owner;
import ru.dulfi.exception.EntityNotFoundException;
import ru.dulfi.exception.ValidationException;

import java.util.List;

public class OwnerService {
    private final OwnerDao ownerDao;

    public OwnerService(OwnerDao ownerDao) {
        this.ownerDao = ownerDao;
    }

    public Owner save(Owner owner) {
        validateOwner(owner);
        return ownerDao.save(owner);
    }

    public Owner getById(Long id) {
        Owner owner = ownerDao.getById(id);
        if (owner == null) {
            throw new EntityNotFoundException("Владелец с id " + id + " не найден");
        }
        return owner;
    }

    public Owner update(Owner owner) {
        validateOwner(owner);
        if (owner.getId() == null) {
            throw new ValidationException("ID владельца не может быть null при обновлении");
        }
        if (ownerDao.getById(owner.getId()) == null) {
            throw new EntityNotFoundException("Владелец с id " + owner.getId() + " не найден");
        }
        return ownerDao.update(owner);
    }

    public void deleteById(Long id) {
        if (ownerDao.getById(id) == null) {
            throw new EntityNotFoundException("Владелец с id " + id + " не найден");
        }
        ownerDao.deleteById(id);
    }

    public void deleteByEntity(Owner owner) {
        if (owner == null || owner.getId() == null) {
            throw new ValidationException("Владелец или его ID не могут быть null");
        }
        deleteById(owner.getId());
    }

    public void deleteAll() {
        ownerDao.deleteAll();
    }

    public List<Owner> getAll() {
        return ownerDao.getAll();
    }

    public List<Owner> getAllWithPets() {
        return ownerDao.getAllWithPets();
    }

    private void validateOwner(Owner owner) {
        if (owner == null) {
            throw new ValidationException("Владелец не может быть null");
        }
        if (owner.getName() == null || owner.getName().trim().isEmpty()) {
            throw new ValidationException("Имя владельца не может быть пустым");
        }
        if (owner.getBirthDate() == null) {
            throw new ValidationException("Дата рождения владельца не может быть null");
        }
    }
} 