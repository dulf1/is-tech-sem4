package ru.dulfi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.dulfi.domain.Owner;
import ru.dulfi.exception.ResourceNotFoundException;
import ru.dulfi.exception.ValidationException;
import ru.dulfi.repository.OwnerRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class OwnerService implements BaseService<Owner> {
    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public Owner save(Owner owner) {
        validateOwner(owner);
        return ownerRepository.save(owner);
    }

    @Override
    public Owner getById(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Владелец с ID " + id + " не найден"));
    }

    @Override
    public List<Owner> getAll() {
        List<Owner> owners = new ArrayList<>();
        ownerRepository.findAll().forEach(owners::add);
        owners.forEach(Owner -> System.out.println());
        return owners;
    }

    public Page<Owner> getAll(Pageable pageable) {
        return ownerRepository.findAll(pageable);
    }

    public Page<Owner> searchByName(String name, Pageable pageable) {
        if (name == null || name.trim().isEmpty()) {
            return ownerRepository.findAll(pageable);
        }
        return ownerRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Owner update(Owner owner) {
        validateOwner(owner);
        if (owner.getId() == null) {
            throw new ValidationException("ID владельца не может быть пустым при обновлении");
        }
        getById(owner.getId());
        return ownerRepository.save(owner);
    }

    @Override
    public void deleteByEntity(Owner owner) {
        if (owner == null || owner.getId() == null) {
            throw new ValidationException("Владелец или его ID не могут быть пустыми при удалении");
        }
        ownerRepository.deleteById(owner.getId());
    }

    @Override
    public void deleteAll() {
        ownerRepository.deleteAll();
    }

    private void validateOwner(Owner owner) {
        if (owner == null) {
            throw new ValidationException("Владелец не может быть пустым");
        }
        if (owner.getName() == null || owner.getName().trim().isEmpty()) {
            throw new ValidationException("Имя владельца не может быть пустым");
        }
        if (owner.getBirthDate() == null) {
            throw new ValidationException("Дата рождения владельца не может быть пустой");
        }
    }
} 