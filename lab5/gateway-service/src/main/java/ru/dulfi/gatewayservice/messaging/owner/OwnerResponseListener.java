package ru.dulfi.gatewayservice.messaging.owner;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.dulfi.gatewayservice.service.OwnerMessagingService;

@Component
public class OwnerResponseListener {

    private final OwnerMessagingService ownerMessagingService;

    @Autowired
    public OwnerResponseListener(OwnerMessagingService ownerMessagingService) {
        this.ownerMessagingService = ownerMessagingService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.owner.response}")
    public void handleOwnerResponse(OwnerMessageResponse response) {
        ownerMessagingService.handleResponse(response);
    }
} 