package com.zolvces.delivery.producer;


/**
 * @author niXueChao
 * @date 2019/7/10 22:18.
 */
public class MessageInfo {

    /**
     * id,自动生成为UUID
     */
    private String id;
    /**
     * 交换机名称,当为默认交换机时为空字符串
     */
    private String exchange;
    /**
     * 路由
     */
    private String routingKey;
    /**
     * 消息配置,包括消息头等,是一个json String
     */
    private String messageProperties;

    /**
     * 消息体,因为ReliableRabbitTemplate配置的MessageConverter为toJson
     * 所以也是一个json
     */
    private String messageBody;

    /**
     * 消息发送失败或是nack才会有,否则为空串
     */
    private String failReason;


    public MessageInfo() {
    }

    private MessageInfo(Builder builder) {
        setId(builder.id);
        setExchange(builder.exchange);
        setRoutingKey(builder.routingKey);
        setMessageProperties(builder.messageProperties);
        setMessageBody(builder.messageBody);
        setFailReason(builder.failReason);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getMessageProperties() {
        return messageProperties;
    }

    public void setMessageProperties(String messageProperties) {
        this.messageProperties = messageProperties;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public static final class Builder {
        private String id;
        private String exchange;
        private String routingKey;
        private String messageProperties;
        private String messageBody;
        private String failReason;

        public Builder() {
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder exchange(String val) {
            exchange = val;
            return this;
        }

        public Builder routingKey(String val) {
            routingKey = val;
            return this;
        }

        public Builder messageProperties(String val) {
            messageProperties = val;
            return this;
        }

        public Builder messageBody(String val) {
            messageBody = val;
            return this;
        }

        public Builder failReason(String val) {
            failReason = val;
            return this;
        }

        public MessageInfo build() {
            return new MessageInfo(this);
        }
    }
}
