package ru.dulfi.gatewayservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.dulfi.gatewayservice.dto.PetDTO;
import ru.dulfi.gatewayservice.messaging.pet.PetMessage;
import ru.dulfi.gatewayservice.messaging.pet.PetMessageAction;
import ru.dulfi.gatewayservice.messaging.pet.PetMessageResponse;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PetMessagingService {

    private final RabbitTemplate rabbitTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<PetMessageResponse>> pendingRequests = new ConcurrentHashMap<>();

    @Value("${rabbitmq.exchange.pet}")
    private String petExchange;

    @Value("${rabbitmq.routing-key.pet.request}")
    private String petRequestRoutingKey;

    @Value("${rabbitmq.routing-key.pet.response}")
    private String petResponseRoutingKey;

    @Autowired
    public PetMessagingService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        
        rabbitTemplate.setReplyTimeout(60000);
        rabbitTemplate.setReceiveTimeout(60000);
    }

    public CompletableFuture<List<PetDTO>> getAllPets(int page, int size, String sortBy, String sortDirection) {
        PetMessage message = new PetMessage();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setAction(PetMessageAction.GET_ALL);
        message.setPage(page);
        message.setSize(size);
        message.setSortBy(sortBy);
        message.setSortDirection(sortDirection);

        CompletableFuture<PetMessageResponse> future = new CompletableFuture<>();
        pendingRequests.put(message.getCorrelationId(), future);

        rabbitTemplate.convertAndSend(petExchange, petRequestRoutingKey, message);

        return future.thenApply(PetMessageResponse::getPets);
    }

    public CompletableFuture<PetDTO> getPetById(Long id) {
        PetMessage message = new PetMessage();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setAction(PetMessageAction.GET_BY_ID);
        message.setPetId(id);

        CompletableFuture<PetMessageResponse> future = new CompletableFuture<>();
        pendingRequests.put(message.getCorrelationId(), future);

        rabbitTemplate.convertAndSend(petExchange, petRequestRoutingKey, message);

        return future.thenApply(PetMessageResponse::getPet);
    }

    public CompletableFuture<List<PetDTO>> searchPetsByName(String name, int page, int size, String sortBy, String sortDirection) {
        PetMessage message = new PetMessage();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setAction(PetMessageAction.SEARCH_BY_NAME);
        message.setSearchName(name);
        message.setPage(page);
        message.setSize(size);
        message.setSortBy(sortBy);
        message.setSortDirection(sortDirection);

        CompletableFuture<PetMessageResponse> future = new CompletableFuture<>();
        pendingRequests.put(message.getCorrelationId(), future);

        rabbitTemplate.convertAndSend(petExchange, petRequestRoutingKey, message);

        return future.thenApply(PetMessageResponse::getPets);
    }

    public CompletableFuture<PetDTO> createPet(PetDTO petDTO) {
        PetMessage message = new PetMessage();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setAction(PetMessageAction.CREATE);
        message.setPet(petDTO);

        CompletableFuture<PetMessageResponse> future = new CompletableFuture<>();
        pendingRequests.put(message.getCorrelationId(), future);

        rabbitTemplate.convertAndSend(petExchange, petRequestRoutingKey, message);

        return future.thenApply(PetMessageResponse::getPet);
    }

    public CompletableFuture<PetDTO> updatePet(PetDTO petDTO) {
        PetMessage message = new PetMessage();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setAction(PetMessageAction.UPDATE);
        message.setPet(petDTO);

        CompletableFuture<PetMessageResponse> future = new CompletableFuture<>();
        pendingRequests.put(message.getCorrelationId(), future);

        rabbitTemplate.convertAndSend(petExchange, petRequestRoutingKey, message);

        return future.thenApply(PetMessageResponse::getPet);
    }

    public CompletableFuture<Boolean> deletePet(Long id) {
        PetMessage message = new PetMessage();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setAction(PetMessageAction.DELETE);
        message.setPetId(id);

        CompletableFuture<PetMessageResponse> future = new CompletableFuture<>();
        pendingRequests.put(message.getCorrelationId(), future);

        rabbitTemplate.convertAndSend(petExchange, petRequestRoutingKey, message);

        return future.thenApply(PetMessageResponse::isSuccess);
    }

    public void handleResponse(PetMessageResponse response) {
        CompletableFuture<PetMessageResponse> future = pendingRequests.remove(response.getCorrelationId());
        if (future != null) {
            if (response.isSuccess()) {
                future.complete(response);
            } else {
                future.completeExceptionally(new RuntimeException(response.getErrorMessage()));
            }
        }
    }
} 