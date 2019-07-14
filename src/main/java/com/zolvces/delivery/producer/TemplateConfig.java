package com.zolvces.delivery.producer;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * @author niXueChao
 * @date 2019/7/10 13:22.
 */
@EnableRabbit
@EnableScheduling
@Configuration
public class TemplateConfig {

    @Bean
    public ReliableRabbitTemplate rabbitJsonTemplate(ConnectionFactory connectionFactory, MessageProcess messageProcess) {
        ReliableRabbitTemplate template = new ReliableRabbitTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        template.setMandatory(true);
        template.setMessageProcess(messageProcess);

        RetryTemplate retryTemplate = new RetryTemplate();

        TimeoutRetryPolicy policy = new TimeoutRetryPolicy();
        policy.setTimeout(15000);
        retryTemplate.setRetryPolicy(Optional.ofNullable(messageProcess.getRetryPolicy()).orElse(policy));
        template.setRetryTemplate(retryTemplate);

        //确认回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            Assert.notNull(correlationData, "correlationData require not null ");
            ExtCorrelationData extData = (ExtCorrelationData) correlationData;

            MessageInfo messageInfo = extData.getMessageInfo();
            messageInfo.setFailReason(Optional.ofNullable(cause).orElse(""));

            if (ack) {
                messageProcess.getMessageAck().accept(messageInfo);
            } else {
                messageProcess.getMessageNack().accept(messageInfo);
            }
        });

        //找不到路由回调
        template.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String messageBody = template.getBodyStr(message);
            String messageProperties = template.getPropertiesStr(message);
            messageProcess.getMessageRouteFail().accept(messageProperties, messageBody, exchange, routingKey);
        });

        return template;
    }


}
