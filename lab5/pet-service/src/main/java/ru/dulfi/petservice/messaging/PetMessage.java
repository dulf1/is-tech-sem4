package ru.dulfi.petservice.messaging;

import lombok.Data;
import ru.dulfi.petservice.dto.PetDTO;

@Data
public class PetMessage {
    private String correlationId;
    private PetMessageAction action;
    private Long petId;
    private Long ownerId;
    private PetDTO pet;
    private String searchName;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
} 