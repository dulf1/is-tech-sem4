package ru.dulfi.gatewayservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dulfi.gatewayservice.domain.User;
import ru.dulfi.gatewayservice.messaging.OwnerCreationMessagingService;
import ru.dulfi.gatewayservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OwnerCreationMessagingService ownerCreationMessagingService;

    @Transactional
    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        
        User savedUser = userRepository.save(user);
        
        ownerCreationMessagingService.sendOwnerCreationMessage(savedUser);
        
        return savedUser;
    }

    @Transactional
    public User registerAdmin(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ADMIN");
        
        User savedUser = userRepository.save(user);
        
        ownerCreationMessagingService.sendOwnerCreationMessage(savedUser);
        
        return savedUser;
    }
} 