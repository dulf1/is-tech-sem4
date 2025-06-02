package ru.dulfi.petservice.messaging;

import lombok.Data;
import ru.dulfi.petservice.dto.PetDTO;

import java.util.List;

@Data
public class PetMessageResponse {
    private String correlationId;
    private PetMessageAction action;
    private boolean success;
    private String errorMessage;
    private PetDTO pet;
    private List<PetDTO> pets;
    private int totalPages;
    private long totalElements;
} 