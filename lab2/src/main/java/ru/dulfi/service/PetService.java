package ru.dulfi.service;

import ru.dulfi.dao.PetDao;
import ru.dulfi.domain.Owner;
import ru.dulfi.domain.Pet;
import ru.dulfi.exception.EntityNotFoundException;
import ru.dulfi.exception.ValidationException;
import ru.dulfi.dao.OwnerDao;

import java.util.List;

public class PetService {
    private final PetDao petDao;
    private final OwnerDao ownerDao;

    public PetService(PetDao petDao, OwnerDao ownerDao) {
        this.petDao = petDao;
        this.ownerDao = ownerDao;
    }

    public Pet save(Pet pet) {
        validatePet(pet);
        try {
            if (pet.getOwner() != null && pet.getOwner().getId() != null) {
                Owner owner = ownerDao.getById(pet.getOwner().getId());
                if (owner == null) {
                    throw new EntityNotFoundException("Владелец с id " + pet.getOwner().getId() + " не найден");
                }
                pet.setOwner(owner);
            }

            return petDao.save(pet);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении питомца: " + e.getMessage(), e);
        }
    }

    public void deleteById(Long id) {
        if (petDao.getById(id) == null) {
            throw new EntityNotFoundException("Питомец с id " + id + " не найден");
        }
        petDao.deleteById(id);
    }

    public void deleteByEntity(Pet pet) {
        if (pet == null || pet.getId() == null) {
            throw new ValidationException("Питомец или его ID не могут быть null");
        }
        deleteById(pet.getId());
    }

    public void deleteAll() {
        petDao.deleteAll();
    }

    public Pet update(Pet pet) {
        validatePet(pet);
        if (pet.getId() == null) {
            throw new ValidationException("ID питомца не может быть null при обновлении");
        }
        if (petDao.getById(pet.getId()) == null) {
            throw new EntityNotFoundException("Питомец с id " + pet.getId() + " не найден");
        }
        try {
            if (pet.getOwner() != null && pet.getOwner().getId() != null) {
                Owner owner = ownerDao.getById(pet.getOwner().getId());
                if (owner == null) {
                    throw new EntityNotFoundException("Владелец с id " + pet.getOwner().getId() + " не найден");
                }
                pet.setOwner(owner);
            }
            return petDao.update(pet);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении питомца: " + e.getMessage(), e);
        }
    }

    public Pet getById(Long id) {
        Pet pet = petDao.getById(id);
        if (pet == null) {
            throw new EntityNotFoundException("Питомец с id " + id + " не найден");
        }
        return pet;
    }

    public List<Pet> getAll() {
        return petDao.getAll();
    }

    public List<Pet> getAllWithOwners() {
        return petDao.getAllWithOwners();
    }

    public void addFriend(Long petId, Long friendId) {
        if (petId == null || friendId == null) {
            throw new ValidationException("ID питомцев не могут быть null");
        }
        try {
            petDao.addFriend(petId, friendId);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении друга: " + e.getMessage(), e);
        }
    }

    public void removeFriend(Long petId, Long friendId) {
        if (petId == null || friendId == null) {
            throw new ValidationException("ID питомцев не могут быть null");
        }
        try {
            petDao.removeFriend(petId, friendId);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении друга: " + e.getMessage(), e);
        }
    }

    public List<Pet> getFriends(Long petId) {
        if (petId == null) {
            throw new ValidationException("ID питомца не может быть null");
        }
        try {
            return petDao.getFriends(petId);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении списка друзей: " + e.getMessage(), e);
        }
    }

    private void validatePet(Pet pet) {
        if (pet == null) {
            throw new ValidationException("Питомец не может быть null");
        }
        if (pet.getName() == null || pet.getName().trim().isEmpty()) {
            throw new ValidationException("Имя питомца не может быть пустым");
        }
        if (pet.getBirthDate() == null) {
            throw new ValidationException("Дата рождения питомца не может быть null");
        }
        if (pet.getBreed() == null || pet.getBreed().trim().isEmpty()) {
            throw new ValidationException("Порода питомца не может быть пустой");
        }
        if (pet.getColor() == null) {
            throw new ValidationException("Цвет питомца не может быть null");
        }
    }
} 