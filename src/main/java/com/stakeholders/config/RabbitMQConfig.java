package com.stakeholders.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_EVENTS_EXCHANGE = "user-events-exchange";
    
    // RPC Exchange i Queue-ovi
    public static final String STAKEHOLDERS_RPC_EXCHANGE = "stakeholders-rpc-exchange";
    public static final String RPC_CHECK_USER_EXISTS_QUEUE = "rpc.check.user.exists.queue";
    public static final String RPC_CHECK_USER_BLOCKED_QUEUE = "rpc.check.user.blocked.queue";
    public static final String RPC_GET_USER_ROLE_QUEUE = "rpc.get.user.role.queue";

    @Bean
    public FanoutExchange userEventsExchange() {
        return new FanoutExchange(USER_EVENTS_EXCHANGE);
    }
    
    // RPC Exchange (Direct)
    @Bean
    public DirectExchange stakeholdersRPCExchange() {
        return new DirectExchange(STAKEHOLDERS_RPC_EXCHANGE);
    }
    
    // RPC Queues
    @Bean
    public Queue rpcCheckUserExistsQueue() {
        return new Queue(RPC_CHECK_USER_EXISTS_QUEUE, false);
    }
    
    @Bean
    public Queue rpcCheckUserBlockedQueue() {
        return new Queue(RPC_CHECK_USER_BLOCKED_QUEUE, false);
    }
    
    @Bean
    public Queue rpcGetUserRoleQueue() {
        return new Queue(RPC_GET_USER_ROLE_QUEUE, false);
    }
    
    // Bindings
    @Bean
    public Binding bindingCheckUserExists() {
        return BindingBuilder
            .bind(rpcCheckUserExistsQueue())
            .to(stakeholdersRPCExchange())
            .with("check.user.exists");
    }
    
    @Bean
    public Binding bindingCheckUserBlocked() {
        return BindingBuilder
            .bind(rpcCheckUserBlockedQueue())
            .to(stakeholdersRPCExchange())
            .with("check.user.blocked");
    }
    
    @Bean
    public Binding bindingGetUserRole() {
        return BindingBuilder
            .bind(rpcGetUserRoleQueue())
            .to(stakeholdersRPCExchange())
            .with("get.user.role");
    }
    
    // Message Converter
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}