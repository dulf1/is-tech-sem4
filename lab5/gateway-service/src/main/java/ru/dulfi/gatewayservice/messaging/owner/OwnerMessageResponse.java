package ru.dulfi.gatewayservice.messaging.owner;

import lombok.Data;
import ru.dulfi.gatewayservice.dto.OwnerDTO;

import java.util.List;

@Data
public class OwnerMessageResponse {
    private String correlationId;
    private OwnerMessageAction action;
    private boolean success;
    private String errorMessage;
    private OwnerDTO owner;
    private List<OwnerDTO> owners;
    private long totalElements;
    private int totalPages;
} 