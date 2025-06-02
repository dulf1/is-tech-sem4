package ru.dulfi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dulfi.domain.Pet;
import ru.dulfi.domain.PetColor;
import ru.dulfi.repository.PetRepository;
import ru.dulfi.exception.ResourceNotFoundException;
import ru.dulfi.exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PetService implements BaseService<Pet> {
    private final PetRepository petRepository;

    @Autowired
    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public Pet save(Pet pet) {
        validatePet(pet);
        System.out.println("Создаем котика с длиной хвоста: " + pet.getTailLength());
        return petRepository.save(pet);
    }

    @Override
    public Pet getById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Котик с ID " + id + " не найден"));
        System.out.println("Найден котик с длиной хвоста: " + pet.getTailLength());
        return pet;
    }

    @Override
    public List<Pet> getAll() {
        List<Pet> pets = new ArrayList<>();
        petRepository.findAll().forEach(pets::add);
        pets.forEach(pet -> System.out.println("У котика " + pet.getName() + " длина хвоста: " + pet.getTailLength()));
        return pets;
    }

    public Page<Pet> getAll(Pageable pageable) {
        Page<Pet> page = petRepository.findAll(pageable);
        page.getContent().forEach(pet -> System.out.println("У котика " + pet.getName() + " длина хвоста: " + pet.getTailLength()));
        return page;
    }

    public Page<Pet> getPetsByColor(PetColor color, Pageable pageable) {
        Page<Pet> page = petRepository.findByColor(color, pageable);
        page.getContent().forEach(pet -> System.out.println("У котика " + pet.getName() + " цвета " + color + " длина хвоста: " + pet.getTailLength()));
        return page;
    }

    public Page<Pet> getPetsByColorAndTailLength(PetColor color, Double minTailLength, Pageable pageable) {
        System.out.println("Ищем котиков цвета " + color + " с длиной хвоста больше " + minTailLength);
        Page<Pet> page = petRepository.findByColorAndTailLengthGreaterThan(color, minTailLength, pageable);
        page.getContent().forEach(pet -> System.out.println("Найден котик " + pet.getName() + " с длиной хвоста: " + pet.getTailLength()));
        return page;
    }

    public List<Pet> getPetsByColor(PetColor color) {
        return petRepository.findByColor(color);
    }

    public List<Pet> getPetsByColorAndTailLength(PetColor color, Double minTailLength) {
        return petRepository.findByColorAndTailLengthGreaterThan(color, minTailLength);
    }

    @Override
    public Pet update(Pet pet) {
        validatePet(pet);
        if (pet.getId() == null) {
            throw new ValidationException("ID котика не может быть пустым при обновлении");
        }
        getById(pet.getId());
        System.out.println("Обновляем котика, новая длина хвоста: " + pet.getTailLength());
        return petRepository.save(pet);
    }

    @Override
    public void deleteByEntity(Pet pet) {
        if (pet == null || pet.getId() == null) {
            throw new ValidationException("Котик или его ID не могут быть пустыми при удалении");
        }
        System.out.println("Удаляем котика с длиной хвоста: " + pet.getTailLength());
        petRepository.deleteById(pet.getId());
    }

    @Override
    public void deleteAll() {
        System.out.println("Удаляем всех котиков :(");
        petRepository.deleteAll();
    }

    private void validatePet(Pet pet) {
        if (pet == null) {
            throw new ValidationException("Котик не может быть пустым");
        }
        if (pet.getName() == null || pet.getName().trim().isEmpty()) {
            throw new ValidationException("Имя котика не может быть пустым");
        }
        if (pet.getBirthDate() == null) {
            throw new ValidationException("Дата рождения котика не может быть пустой");
        }
        if (pet.getBirthDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (pet.getBreed() == null || pet.getBreed().trim().isEmpty()) {
            throw new ValidationException("Порода котика не может быть пустой");
        }
        if (pet.getColor() == null) {
            throw new ValidationException("Цвет котика не может быть пустым");
        }
        if (pet.getOwner() == null) {
            throw new ValidationException("У котика должен быть хозяин");
        }
        if (pet.getTailLength() == null || pet.getTailLength() <= 0) {
            throw new ValidationException("Длина хвоста должна быть положительным числом");
        }
    }
} 