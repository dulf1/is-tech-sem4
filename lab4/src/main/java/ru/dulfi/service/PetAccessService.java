package ru.dulfi.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.dulfi.domain.Pet;
import ru.dulfi.domain.User;

@Service
public class PetAccessService {

    public void checkAccess(Pet pet) {
        if (pet == null) {
            throw new IllegalArgumentException("Питомец не может быть null");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new AccessDeniedException("Требуется аутентификация");
        }

        if (!(auth.getPrincipal() instanceof User)) {
            throw new AccessDeniedException("Неверный тип пользователя");
        }

        User currentUser = (User) auth.getPrincipal();
        checkAccessForUser(pet, currentUser);
    }

    public void checkAccessForUser(Pet pet, User currentUser) {
        if (pet == null) {
            throw new IllegalArgumentException("Питомец не может быть null");
        }

        if (currentUser == null) {
            throw new AccessDeniedException("Требуется аутентификация");
        }

        if ("ADMIN".equals(currentUser.getRole())) {
            return;
        }

        if (currentUser.getOwner() == null) {
            throw new AccessDeniedException("У пользователя нет привязанного владельца");
        }

        if (pet.getOwner() == null) {
            throw new AccessDeniedException("У питомца нет владельца");
        }

        if (!pet.getOwner().equals(currentUser.getOwner())) {
            throw new AccessDeniedException("Доступ запрещен: вы не являетесь владельцем этого питомца");
        }
    }
} 