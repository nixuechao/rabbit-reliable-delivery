package com.zolvces.delivery.producer;

import org.springframework.retry.RetryPolicy;

import java.util.List;
import java.util.function.Consumer;

/**
 * 把各个操作聚合在一起,构成一个"策略" QAQ
 *
 * @author niXueChao
 * @date 2019/7/10 21:15.
 */
public class MessageProcess {


    /**
     * 触发:
     * 第一次发送消息前
     */
    private Consumer<MessageInfo> preSend;

    /**
     * 触发:
     * 收到confirm回调的ACK
     */
    private Consumer<MessageInfo> messageAck;

    /**
     * 触发:
     * 收到confirm回调的NACK
     * 或
     * rabbitTemplate.send() 方法异常(如长时间连不上mq)
     */
    private Consumer<MessageInfo> messageNack;

    /**
     * 定时任务
     * 由这个方法提供需要重发的消息
     *
     * @see ResendScheduling#resend
     */
    private PickResendMessagesConsumer pickResendMessages;

    /**
     * 触发:
     * 消息无法路由到队列
     */
    private messageRouteFailConsumer messageRouteFail;

    /**
     * 触发:
     * 消息重发前
     */
    private Consumer<String> preResend;


    /**
     * 重试策略,避免只要断开就马上报ConnectException
     * 默认重试15秒
     */
    private RetryPolicy retryPolicy;


    private MessageProcess(Builder builder) {
        setPreSend(builder.preSend);
        setMessageAck(builder.messageAck);
        setMessageNack(builder.messageNack);
        setPickResendMessages(builder.pickResendMessages);
        setMessageRouteFail(builder.messageRouteFail);
        setPreResend(builder.preResend);
        setRetryPolicy(builder.retryPolicy);
    }


    @FunctionalInterface
    public interface PickResendMessagesConsumer {
        List<MessageInfo> accept();
    }

    @FunctionalInterface
    public interface messageRouteFailConsumer {
        void accept(String messageProperties, String messageBody, String exchange, String routingKey);
    }


    public Consumer<MessageInfo> getPreSend() {
        return preSend;
    }

    public void setPreSend(Consumer<MessageInfo> preSend) {
        this.preSend = preSend;
    }

    public Consumer<MessageInfo> getMessageAck() {
        return messageAck;
    }

    public void setMessageAck(Consumer<MessageInfo> messageAck) {
        this.messageAck = messageAck;
    }

    public Consumer<MessageInfo> getMessageNack() {
        return messageNack;
    }

    public void setMessageNack(Consumer<MessageInfo> messageNack) {
        this.messageNack = messageNack;
    }

    public PickResendMessagesConsumer getPickResendMessages() {
        return pickResendMessages;
    }

    public void setPickResendMessages(PickResendMessagesConsumer pickResendMessages) {
        this.pickResendMessages = pickResendMessages;
    }

    public messageRouteFailConsumer getMessageRouteFail() {
        return messageRouteFail;
    }

    public void setMessageRouteFail(messageRouteFailConsumer messageRouteFail) {
        this.messageRouteFail = messageRouteFail;
    }

    public Consumer<String> getPreResend() {
        return preResend;
    }

    public void setPreResend(Consumer<String> preResend) {
        this.preResend = preResend;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public static final class Builder {
        private Consumer<MessageInfo> preSend;
        private Consumer<MessageInfo> messageAck;
        private Consumer<MessageInfo> messageNack;
        private PickResendMessagesConsumer pickResendMessages;
        private messageRouteFailConsumer messageRouteFail;
        private Consumer<String> preResend;
        private RetryPolicy retryPolicy;

        public Builder() {
        }

        public Builder preSend(Consumer<MessageInfo> val) {
            preSend = val;
            return this;
        }

        public Builder messageAck(Consumer<MessageInfo> val) {
            messageAck = val;
            return this;
        }

        public Builder messageNack(Consumer<MessageInfo> val) {
            messageNack = val;
            return this;
        }

        public Builder pickResendMessages(PickResendMessagesConsumer val) {
            pickResendMessages = val;
            return this;
        }

        public Builder messageRouteFail(messageRouteFailConsumer val) {
            messageRouteFail = val;
            return this;
        }

        public Builder preResend(Consumer<String> val) {
            preResend = val;
            return this;
        }

        public Builder retryPolicy(RetryPolicy val) {
            retryPolicy = val;
            return this;
        }

        public MessageProcess build() {
            return new MessageProcess(this);
        }
    }
}
