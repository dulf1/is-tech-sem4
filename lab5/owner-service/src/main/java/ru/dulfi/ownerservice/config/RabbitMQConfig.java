package ru.dulfi.ownerservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.owner.request}")
    private String ownerRequestQueue;

    @Value("${rabbitmq.queue.owner.response}")
    private String ownerResponseQueue;

    @Value("${rabbitmq.exchange.owner}")
    private String ownerExchange;

    @Value("${rabbitmq.routing-key.owner.request}")
    private String ownerRequestRoutingKey;

    @Value("${rabbitmq.routing-key.owner.response}")
    private String ownerResponseRoutingKey;
    
    public static final String OWNER_CREATION_QUEUE = "owner-creation-queue";

    @Bean
    public Queue ownerRequestQueue() {
        return new Queue(ownerRequestQueue);
    }

    @Bean
    public Queue ownerResponseQueue() {
        return new Queue(ownerResponseQueue);
    }
    
    @Bean
    public Queue ownerCreationQueue() {
        return new Queue(OWNER_CREATION_QUEUE, true);
    }

    @Bean
    public DirectExchange ownerExchange() {
        return new DirectExchange(ownerExchange);
    }

    @Bean
    public Binding ownerRequestBinding() {
        return BindingBuilder
                .bind(ownerRequestQueue())
                .to(ownerExchange())
                .with(ownerRequestRoutingKey);
    }

    @Bean
    public Binding ownerResponseBinding() {
        return BindingBuilder
                .bind(ownerResponseQueue())
                .to(ownerExchange())
                .with(ownerResponseRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
} 