package ru.dulfi.gatewayservice.messaging.pet;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.dulfi.gatewayservice.service.PetMessagingService;

@Component
public class PetResponseListener {

    private final PetMessagingService petMessagingService;

    @Autowired
    public PetResponseListener(PetMessagingService petMessagingService) {
        this.petMessagingService = petMessagingService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.pet.response}")
    public void handlePetResponse(PetMessageResponse response) {
        System.out.println("Получен ответ: " + response.getCorrelationId() + ", успех: " + response.isSuccess());
        if (!response.isSuccess()) {
            System.out.println("Ошибка в ответе: " + response.getErrorMessage());
        }
        petMessagingService.handleResponse(response);
    }
} 