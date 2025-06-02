package ru.dulfi.ownerservice.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Data
@NoArgsConstructor
public class OwnerDTO {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private List<Long> petIds;
} 