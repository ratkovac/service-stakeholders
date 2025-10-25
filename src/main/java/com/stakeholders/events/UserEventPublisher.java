package com.stakeholders.events;

import com.stakeholders.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public UserEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserCreatedEvent(Long userId) {
        String userIdAsString = String.valueOf(userId);
        System.out.println("Slanje UserCreatedEvent sa ID: " + userIdAsString);
        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_EVENTS_EXCHANGE, "user.created", userIdAsString);
    }

    public void publishUserDeletedEvent(Long userId) {
        String userIdAsString = String.valueOf(userId);
        System.out.println("Slanje UserDeletedEvent sa ID: " + userId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_EVENTS_EXCHANGE, "user.deleted", userIdAsString);
    }
}