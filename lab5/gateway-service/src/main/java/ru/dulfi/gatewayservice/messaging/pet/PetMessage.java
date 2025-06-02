package ru.dulfi.gatewayservice.messaging.pet;

import lombok.Data;
import ru.dulfi.gatewayservice.dto.PetDTO;

@Data
public class PetMessage {
    private String correlationId;
    private PetMessageAction action;
    private Long petId;
    private String searchName;
    private PetDTO pet;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
} 