package ru.dulfi.gatewayservice.messaging.pet;

import lombok.Data;
import ru.dulfi.gatewayservice.dto.PetDTO;

import java.util.List;

@Data
public class PetMessageResponse {
    private String correlationId;
    private PetMessageAction action;
    private boolean success;
    private String errorMessage;
    private PetDTO pet;
    private List<PetDTO> pets;
    private long totalElements;
    private int totalPages;
} 