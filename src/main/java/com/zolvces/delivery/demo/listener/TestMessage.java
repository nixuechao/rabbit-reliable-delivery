package com.zolvces.delivery.demo.listener;

import javax.persistence.*;

/**
 * @author niXueChao
 * @date 2019/7/12 15:29.
 */
@Entity
@Table(name = "test_message",
        uniqueConstraints = @UniqueConstraint(columnNames = {"orderNumber", "threadName"}))
public class TestMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String threadName;

    @Column
    private int orderNumber;

    public TestMessage() {
    }


    public TestMessage(String threadName, int orderNumber) {
        this.threadName = threadName;
        this.orderNumber = orderNumber;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
}
