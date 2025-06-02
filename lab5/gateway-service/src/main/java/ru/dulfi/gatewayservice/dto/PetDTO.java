package ru.dulfi.gatewayservice.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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
    
    @JsonBackReference
    private OwnerDTO owner;
} 