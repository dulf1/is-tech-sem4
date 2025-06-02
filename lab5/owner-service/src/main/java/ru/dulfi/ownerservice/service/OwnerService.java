package ru.dulfi.ownerservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dulfi.ownerservice.domain.Owner;
import ru.dulfi.ownerservice.repository.OwnerRepository;
import ru.dulfi.ownerservice.exception.ResourceNotFoundException;
import ru.dulfi.ownerservice.exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OwnerService implements BaseService<Owner> {
    private final OwnerRepository ownerRepository;

    @Autowired
    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    public Owner createOwner(Owner owner) {
        validateOwner(owner);
        System.out.println("Создаем владельца через сообщение: " + owner.getName());
        return ownerRepository.save(owner);
    }

    @Override
    public Owner save(Owner owner) {
        validateOwner(owner);
        System.out.println("Создаем владельца: " + owner.getName());
        return ownerRepository.save(owner);
    }

    @Override
    public Owner getById(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Владелец с ID " + id + " не найден"));
        System.out.println("Найден владелец: " + owner.getName());
        return owner;
    }

    @Override
    public List<Owner> getAll() {
        List<Owner> owners = new ArrayList<>();
        ownerRepository.findAll().forEach(owners::add);
        owners.forEach(owner -> System.out.println("Владелец: " + owner.getName()));
        return owners;
    }

    public Page<Owner> getAll(Pageable pageable) {
        Page<Owner> page = ownerRepository.findAll(pageable);
        page.getContent().forEach(owner -> System.out.println("Владелец: " + owner.getName()));
        return page;
    }

    public Page<Owner> findByNameContaining(String name, Pageable pageable) {
        return ownerRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Owner update(Owner owner) {
        validateOwner(owner);
        if (owner.getId() == null) {
            throw new ValidationException("ID владельца не может быть пустым при обновлении");
        }
        getById(owner.getId());
        System.out.println("Обновляем владельца: " + owner.getName());
        return ownerRepository.save(owner);
    }

    @Override
    public void deleteByEntity(Owner owner) {
        if (owner == null || owner.getId() == null) {
            throw new ValidationException("Владелец или его ID не могут быть пустыми при удалении");
        }
        System.out.println("Удаляем владельца: " + owner.getName());
        ownerRepository.deleteById(owner.getId());
    }

    @Override
    public void deleteAll() {
        System.out.println("Удаляем всех владельцев");
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
        if (owner.getBirthDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
} 