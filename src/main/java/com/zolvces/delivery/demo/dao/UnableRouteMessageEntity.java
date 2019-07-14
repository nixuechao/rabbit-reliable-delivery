package com.zolvces.delivery.demo.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**无法路由的消息
 * @author niXueChao
 * @date 2019/7/11 16:13.
 */
@Entity
@Table(name = "UNABLE_ROUTE_MESSAGE")
public class UnableRouteMessageEntity {
    @Id
    @Column(length = 100)
    private String id;

    private String exchange;
    private String routingKey;

    @Column(length = 2000)
    private String messageProperties;

    @Column(length = 2000)
    private String messageBody;

    private String createTime;


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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
