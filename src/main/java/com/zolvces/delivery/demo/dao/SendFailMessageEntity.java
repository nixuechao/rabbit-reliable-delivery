package com.zolvces.delivery.demo.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**发送失败的消息
 * @author niXueChao
 * @date 2019/7/11 16:12.
 */
@Entity
@Table(name = "SEND_FAIL_MESSAGE")
public class SendFailMessageEntity {
    @Id
    @Column(length = 100)
    private String id;

    private String exchange;

    private String routingKey;

    @Column(length = 2000)
    private String messageProperties;

    @Column(length = 2000)
    private String messageBody;

    private String failReason;




    private int resendTimes = 0;

    private Long firstSendTime;

    private Long lastSendTime;


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

    public int getResendTimes() {
        return resendTimes;
    }

    public void setResendTimes(int resendTimes) {
        this.resendTimes = resendTimes;
    }

    public Long getFirstSendTime() {
        return firstSendTime;
    }

    public void setFirstSendTime(Long firstSendTime) {
        this.firstSendTime = firstSendTime;
    }

    public Long getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(Long lastSendTime) {
        this.lastSendTime = lastSendTime;
    }
}
