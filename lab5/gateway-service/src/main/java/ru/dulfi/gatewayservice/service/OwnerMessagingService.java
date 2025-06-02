package ru.dulfi.gatewayservice.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.dulfi.gatewayservice.dto.OwnerDTO;
import ru.dulfi.gatewayservice.messaging.owner.OwnerMessage;
import ru.dulfi.gatewayservice.messaging.owner.OwnerMessageAction;
import ru.dulfi.gatewayservice.messaging.owner.OwnerMessageResponse;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OwnerMessagingService {

    private final RabbitTemplate rabbitTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<OwnerMessageResponse>> pendingRequests = new ConcurrentHashMap<>();

    @Value("${rabbitmq.exchange.owner}")
    private String ownerExchange;

    @Value("${rabbitmq.routing-key.owner.request}")
    private String ownerRequestRoutingKey;

    @Value("${rabbitmq.routing-key.owner.response}")
    private String ownerResponseRoutingKey;

    @Autowired
    public OwnerMessagingService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        
        rabbitTemplate.setReplyTimeout(60000);
        rabbitTemplate.setReceiveTimeout(60000);
    }

    public CompletableFuture<List<OwnerDTO>> getAllOwners(int page, int size, String sortBy, String sortDirection) {
        OwnerMessage message = new OwnerMessage();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setAction(OwnerMessageAction.GET_ALL);
        message.setPage(page);
        message.setSize(size);
        message.setSortBy(sortBy);
        message.setSortDirection(sortDirection);

        CompletableFuture<OwnerMessageResponse> future = new CompletableFuture<>();
        pendingRequests.put(message.getCorrelationId(), future);

        rabbitTemplate.convertAndSend(ownerExchange, ownerRequestRoutingKey, message);

        return future.thenApply(OwnerMessageResponse::getOwners);
    }

    public CompletableFuture<OwnerDTO> getOwnerById(Long id) {
        OwnerMessage message = new OwnerMessage();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setAction(OwnerMessageAction.GET_BY_ID);
        message.setOwnerId(id);

        CompletableFuture<OwnerMessageResponse> future = new CompletableFuture<>();
        pendingRequests.put(message.getCorrelationId(), future);

        rabbitTemplate.convertAndSend(ownerExchange, ownerRequestRoutingKey, message);

        return future.thenApply(OwnerMessageResponse::getOwner);
    }

    public CompletableFuture<OwnerDTO> createOwner(OwnerDTO ownerDTO) {
        OwnerMessage message = new OwnerMessage();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setAction(OwnerMessageAction.CREATE);
        message.setOwner(ownerDTO);

        CompletableFuture<OwnerMessageResponse> future = new CompletableFuture<>();
        pendingRequests.put(message.getCorrelationId(), future);

        rabbitTemplate.convertAndSend(ownerExchange, ownerRequestRoutingKey, message);

        return future.thenApply(OwnerMessageResponse::getOwner);
    }

    public CompletableFuture<OwnerDTO> updateOwner(OwnerDTO ownerDTO) {
        OwnerMessage message = new OwnerMessage();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setAction(OwnerMessageAction.UPDATE);
        message.setOwner(ownerDTO);

        CompletableFuture<OwnerMessageResponse> future = new CompletableFuture<>();
        pendingRequests.put(message.getCorrelationId(), future);

        rabbitTemplate.convertAndSend(ownerExchange, ownerRequestRoutingKey, message);

        return future.thenApply(OwnerMessageResponse::getOwner);
    }

    public CompletableFuture<Boolean> deleteOwner(Long id) {
        OwnerMessage message = new OwnerMessage();
        message.setCorrelationId(UUID.randomUUID().toString());
        message.setAction(OwnerMessageAction.DELETE);
        message.setOwnerId(id);

        CompletableFuture<OwnerMessageResponse> future = new CompletableFuture<>();
        pendingRequests.put(message.getCorrelationId(), future);

        rabbitTemplate.convertAndSend(ownerExchange, ownerRequestRoutingKey, message);

        return future.thenApply(OwnerMessageResponse::isSuccess);
    }

    public void handleResponse(OwnerMessageResponse response) {
        CompletableFuture<OwnerMessageResponse> future = pendingRequests.remove(response.getCorrelationId());
        if (future != null) {
            if (response.isSuccess()) {
                future.complete(response);
            } else {
                future.completeExceptionally(new RuntimeException(response.getErrorMessage()));
            }
        }
    }
}