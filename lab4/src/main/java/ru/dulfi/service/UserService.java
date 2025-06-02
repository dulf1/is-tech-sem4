package ru.dulfi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.dulfi.domain.User;
import ru.dulfi.domain.Owner;
import ru.dulfi.repository.UserRepository;
import ru.dulfi.repository.OwnerRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, OwnerRepository ownerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.ownerRepository = ownerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        Owner owner = new Owner();
        owner.setName(user.getUsername());
        owner.setBirthDate(user.getBirthDate());
        owner = ownerRepository.save(owner);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setOwner(owner);
        return userRepository.save(user);
    }

    public User registerAdmin(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        Owner owner = new Owner();
        owner.setName(user.getUsername());
        owner.setBirthDate(user.getBirthDate());
        owner = ownerRepository.save(owner);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ADMIN");
        user.setOwner(owner);
        return userRepository.save(user);
    }
} 