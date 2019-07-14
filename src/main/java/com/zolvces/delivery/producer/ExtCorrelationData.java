package com.zolvces.delivery.producer;

import org.springframework.amqp.rabbit.connection.CorrelationData;

/**
 * @author niXueChao
 * @date 2019/7/12 11:17.
 */
class ExtCorrelationData extends CorrelationData {
    private MessageInfo messageInfo;


    MessageInfo getMessageInfo() {
        return messageInfo;
    }

    void setMessageInfo(MessageInfo messageInfo) {
        this.messageInfo = messageInfo;
    }
}
