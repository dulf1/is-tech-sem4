package ru.dulfi.ownerservice.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.dulfi.ownerservice.config.RabbitMQConfig;
import ru.dulfi.ownerservice.domain.Owner;
import ru.dulfi.ownerservice.service.OwnerService;

@Component
@RequiredArgsConstructor
public class OwnerCreationListener {

    private final OwnerService ownerService;

    @RabbitListener(queues = RabbitMQConfig.OWNER_CREATION_QUEUE)
    public void handleOwnerCreation(OwnerCreationMessage message) {
        System.out.println("Получено сообщение для создания владельца: " + message);
        
        Owner owner = new Owner();
        owner.setName(message.getUsername());
        owner.setBirthDate(message.getBirthDate());
        
        Owner savedOwner = ownerService.createOwner(owner);
        
        System.out.println("Владелец успешно создан: " + savedOwner);
    }
} 