package ru.dulfi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class OwnerDTO {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private List<Long> petIds = new ArrayList<>();
}