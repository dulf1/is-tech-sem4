package ru.dulfi.ownerservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerCreationMessage implements Serializable {
    private String username;
    private LocalDate birthDate;
    private Long userId;
} 