package ru.dulfi.petservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        return converter;
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
} 