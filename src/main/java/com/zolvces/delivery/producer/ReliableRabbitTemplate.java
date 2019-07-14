package com.zolvces.delivery.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author niXueChao
 * @date 2019/7/10 15:59.
 */
public class ReliableRabbitTemplate extends RabbitTemplate {

    private MessageProcess messageProcess;

    @Override
    public void send(String exchange, String routingKey, Message message, CorrelationData correlationData) throws AmqpException {
        try {
            String id = UUID.randomUUID().toString();
            String messageBody = getBodyStr(message);
            String messageProperties = getPropertiesStr(message);

            MessageInfo messageInfo = new MessageInfo.Builder()
                    .id(id)
                    .exchange(exchange)
                    .routingKey(routingKey)
                    .messageProperties(messageProperties)
                    .messageBody(messageBody)
                    .failReason("")
                    .build();

            messageProcess.getPreSend().accept(messageInfo);


            ExtCorrelationData extData = new ExtCorrelationData();
            if (correlationData != null) {
                extData.setId(correlationData.getId());
                extData.setReturnedMessage(correlationData.getReturnedMessage());
            }
            extData.setMessageInfo(messageInfo);


            try {
                super.send(exchange, routingKey, message, extData);
            } catch (Exception e) {
                //极有可能是AmqpConnectException,长时间(和retryTemplate有关)连接不上
                messageInfo.setFailReason(e.getMessage());
                messageProcess.getMessageNack().accept(messageInfo);
            }

        } catch (Exception e) {
            //出现异常,有极有可能是保存消息出现异常
            throw new AmqpException(e.getMessage());
        }


    }

    void reSend(MessageInfo messageInfo) throws IOException {

        messageProcess.getPreResend().accept(messageInfo.getId());
        byte[] body = messageInfo.getMessageBody().getBytes(StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        MessageProperties messageProperties = mapper.readValue(messageInfo.getMessageProperties(), MessageProperties.class);
        Message message = new Message(body, messageProperties);

        ExtCorrelationData extData = new ExtCorrelationData();
        extData.setMessageInfo(messageInfo);


        try {
            super.send(messageInfo.getExchange(), messageInfo.getRoutingKey(), message, extData);
        } catch (Exception e) {
            messageInfo.setFailReason(e.getMessage());
            messageProcess.getMessageNack().accept(messageInfo);
        }
    }

    /**
     * template 的MessageConverter 势必要 转json
     */
    String getBodyStr(Message message) {
        try {
            return new String(message.getBody(), Charset.defaultCharset().name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    String getPropertiesStr(Message message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(message.getMessageProperties());
        } catch (JsonProcessingException e) {
            return "";
        }
    }


    public MessageProcess getMessageProcess() {
        return messageProcess;
    }

    public void setMessageProcess(MessageProcess messageProcess) {
        this.messageProcess = messageProcess;
    }
}
