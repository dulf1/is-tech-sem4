package ru.dulfi.ownerservice.messaging;

import lombok.Data;
import ru.dulfi.ownerservice.dto.OwnerDTO;

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