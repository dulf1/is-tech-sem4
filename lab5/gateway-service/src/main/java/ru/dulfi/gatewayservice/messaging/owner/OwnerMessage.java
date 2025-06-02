package ru.dulfi.gatewayservice.messaging.owner;

import lombok.Data;
import ru.dulfi.gatewayservice.dto.OwnerDTO;

@Data
public class OwnerMessage {
    private String correlationId;
    private OwnerMessageAction action;
    private Long ownerId;
    private String searchName;
    private OwnerDTO owner;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
} 