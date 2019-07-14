package com.zolvces.delivery.demo.listener;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author niXueChao
 * @date 2019/7/12 15:52.
 */
@Configuration
public class TestListenerConfig {

    @Bean("jsonContainerFactory")
    public RabbitListenerContainerFactory jsonConvertFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory containerFactory= new SimpleRabbitListenerContainerFactory();
        containerFactory.setConnectionFactory(connectionFactory);
        containerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        containerFactory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        containerFactory.setConcurrentConsumers(5);
        containerFactory.setMaxConcurrentConsumers(30);


        return containerFactory;
    }
}
