package ru.dulfi.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.dulfi.domain.PetColor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Data
@NoArgsConstructor
public class PetDTO {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private String breed;
    private PetColor color;
    private Double tailLength;
    private Long ownerId;
    private Set<PetDTO> friends = new HashSet<>();
}