package ru.dulfi.gatewayservice.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.dulfi.gatewayservice.domain.User;

@Service
@RequiredArgsConstructor
public class OwnerCreationMessagingService {

    private static final String OWNER_CREATION_QUEUE = "owner-creation-queue";
    
    private final RabbitTemplate rabbitTemplate;

    public void sendOwnerCreationMessage(User user) {
        OwnerCreationMessage message = new OwnerCreationMessage();
        message.setUsername(user.getUsername());
        message.setBirthDate(user.getBirthDate());
        message.setUserId(user.getId());
        
        rabbitTemplate.convertAndSend(OWNER_CREATION_QUEUE, message);
    }
} 