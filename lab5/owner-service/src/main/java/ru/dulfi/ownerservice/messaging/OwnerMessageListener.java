package ru.dulfi.ownerservice.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.dulfi.ownerservice.domain.Owner;
import ru.dulfi.ownerservice.dto.OwnerDTO;
import ru.dulfi.ownerservice.exception.ResourceNotFoundException;
import ru.dulfi.ownerservice.service.OwnerService;

import java.util.stream.Collectors;

@Component
public class OwnerMessageListener {

    private final OwnerService ownerService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.owner}")
    private String ownerExchange;

    @Value("${rabbitmq.routing-key.owner.response}")
    private String ownerResponseRoutingKey;

    @Autowired
    public OwnerMessageListener(OwnerService ownerService, RabbitTemplate rabbitTemplate) {
        this.ownerService = ownerService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${rabbitmq.queue.owner.request}")
    public void processMessage(OwnerMessage message) {
        OwnerMessageResponse response = new OwnerMessageResponse();
        response.setCorrelationId(message.getCorrelationId());
        response.setAction(message.getAction());

        try {
            switch (message.getAction()) {
                case GET_ALL:
                    Pageable pageable = createPageable(message);
                    Page<Owner> ownersPage = ownerService.getAll(pageable);
                    response.setOwners(ownersPage.getContent().stream()
                            .map(this::convertToDTO)
                            .collect(Collectors.toList()));
                    response.setTotalElements(ownersPage.getTotalElements());
                    response.setTotalPages(ownersPage.getTotalPages());
                    break;
                case GET_BY_ID:
                    Owner owner = ownerService.getById(message.getOwnerId());
                    response.setOwner(convertToDTO(owner));
                    break;
                case SEARCH_BY_NAME:
                    Pageable searchPageable = createPageable(message);
                    Page<Owner> searchResult = ownerService.findByNameContaining(message.getSearchName(), searchPageable);
                    response.setOwners(searchResult.getContent().stream()
                            .map(this::convertToDTO)
                            .collect(Collectors.toList()));
                    response.setTotalElements(searchResult.getTotalElements());
                    response.setTotalPages(searchResult.getTotalPages());
                    break;
                case CREATE:
                    Owner newOwner = convertToEntity(message.getOwner());
                    Owner savedOwner = ownerService.save(newOwner);
                    response.setOwner(convertToDTO(savedOwner));
                    break;
                case UPDATE:
                    Owner ownerToUpdate = convertToEntity(message.getOwner());
                    Owner updatedOwner = ownerService.update(ownerToUpdate);
                    response.setOwner(convertToDTO(updatedOwner));
                    break;
                case DELETE:
                    Owner ownerToDelete = ownerService.getById(message.getOwnerId());
                    ownerService.deleteByEntity(ownerToDelete);
                    response.setSuccess(true);
                    break;
                default:
                    response.setSuccess(false);
                    response.setErrorMessage("Неизвестное действие: " + message.getAction());
                    return;
            }
            response.setSuccess(true);
        } catch (ResourceNotFoundException e) {
            response.setSuccess(false);
            response.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage("Ошибка обработки: " + e.getMessage());
        }

        rabbitTemplate.convertAndSend(ownerExchange, ownerResponseRoutingKey, response);
    }

    private Pageable createPageable(OwnerMessage message) {
        int page = message.getPage() != null ? message.getPage() : 0;
        int size = message.getSize() != null ? message.getSize() : 10;
        
        if (message.getSortBy() != null && message.getSortDirection() != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(message.getSortDirection()) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(direction, message.getSortBy()));
        }
        
        return PageRequest.of(page, size);
    }

    private OwnerDTO convertToDTO(Owner owner) {
        if (owner == null) return null;
        
        OwnerDTO dto = new OwnerDTO();
        dto.setId(owner.getId());
        dto.setName(owner.getName());
        dto.setBirthDate(owner.getBirthDate());
        return dto;
    }

    private Owner convertToEntity(OwnerDTO dto) {
        if (dto == null) return null;
        
        Owner owner = new Owner();
        owner.setId(dto.getId());
        owner.setName(dto.getName());
        owner.setBirthDate(dto.getBirthDate());
        return owner;
    }
} 