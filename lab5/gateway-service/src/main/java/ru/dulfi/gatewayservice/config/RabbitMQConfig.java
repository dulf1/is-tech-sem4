package ru.dulfi.gatewayservice.config;

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

    @Value("${rabbitmq.queue.pet.request}")
    private String petRequestQueue;

    @Value("${rabbitmq.queue.pet.response}")
    private String petResponseQueue;

    @Value("${rabbitmq.exchange.pet}")
    private String petExchange;

    @Value("${rabbitmq.routing-key.pet.request}")
    private String petRequestRoutingKey;

    @Value("${rabbitmq.routing-key.pet.response}")
    private String petResponseRoutingKey;

    @Value("${rabbitmq.queue.owner.request}")
    private String ownerRequestQueue;

    @Value("${rabbitmq.queue.owner.response}")
    private String ownerResponseQueue;

    @Value("${rabbitmq.exchange.owner}")
    private String ownerExchange;

    @Value("${rabbitmq.routing-key.owner.request}")
    private String ownerRequestRoutingKey;

    @Value(" ${rabbitmq.routing-key.owner.response}")
    private String ownerResponseRoutingKey;

    @Bean
    public Queue petRequestQueue() {
        return new Queue(petRequestQueue);
    }

    @Bean
    public Queue petResponseQueue() {
        return new Queue(petResponseQueue);
    }

    @Bean
    public DirectExchange petExchange() {
        return new DirectExchange(petExchange);
    }

    @Bean
    public Binding petRequestBinding() {
        return BindingBuilder
                .bind(petRequestQueue())
                .to(petExchange())
                .with(petRequestRoutingKey);
    }

    @Bean
    public Binding petResponseBinding() {
        return BindingBuilder
                .bind(petResponseQueue())
                .to(petExchange())
                .with(petResponseRoutingKey);
    }

    @Bean
    public Queue ownerRequestQueue() {
        return new Queue(ownerRequestQueue);
    }

    @Bean
    public Queue ownerResponseQueue() {
        return new Queue(ownerResponseQueue);
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