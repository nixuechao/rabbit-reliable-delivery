package com.zolvces.delivery.demo;

import com.zolvces.delivery.demo.dao.MessageService;
import com.zolvces.delivery.producer.MessageProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author niXueChao
 * @date 2019/7/11 15:59.
 */
@Configuration
public class StrategyConfig {

    @Autowired
    private MessageService messageService;


    public MessageProcess myStrategy0() {
        return new MessageProcess.Builder()
                //消息第一次发送前
                .preSend(messageInfo -> {
                    //保存消息
                    messageService.save(messageInfo);
                })

                //消息投递成功(包含路由失败的)
                .messageAck(messageInfo -> {
                    //删除消息
                    messageService.deleteById(messageInfo.getId());
                })

                //消息投递失败
                .messageNack(messageInfo -> {
                    //添加失败原因
                    messageService.addFailReason(messageInfo.getId(), messageInfo.getFailReason());
                })

                //提供需要重发的消息
                .pickResendMessages(() -> {
                    //查找需要重发的消息,满足
                    // 1.重发次数小于3
                    // 2.上一次发送时间为3秒钟之前的(confirmCallback是异步的)
                    // 3.按上一次发送时间顺序排序(先发等待久的)
                    return messageService.selectNeedResendMessage();
                })

                //消息重发前
                .preResend(id -> {
                    //消息重发数+1,修改上一次发送时间为当前时间
                    messageService.updateResendTimesAndLastSendTime(id);
                })

                //消息路由失败
                .messageRouteFail((messageProperties, messageBody, exchange, routingKey) -> {
                    //保存路由失败的消息
                    messageService.saveUnRoutableMessage(messageProperties, messageBody, exchange, routingKey);
                })

                .build();
    }

    @Bean
    public MessageProcess myStrategy1() {
        return new MessageProcess.Builder()
                //消息第一次发送前
                .preSend(messageInfo -> {

                })

                //消息投递成功(包含路由失败的)
                .messageAck(messageInfo -> {
                    messageService.deleteIfExist(messageInfo.getId());
                })

                //消息投递失败
                .messageNack(messageInfo -> {
                    //若消息不存在就保存
                    messageService.saveIfNotExist(messageInfo);
                })

                //提供需要重发的消息
                .pickResendMessages(() -> {
                    //查找需要重发的消息,满足
                    // 1.重发次数小于3
                    // 2.上一次发送时间为3秒钟之前的(confirmCallback是异步的)
                    // 3.按上一次发送时间顺序排序(先发等待久的)
                    return messageService.selectNeedResendMessage();
                })

                //消息重发前
                .preResend(id -> {
                    //消息重发数+1,修改上一次发送时间为当前时间
                    messageService.updateResendTimesAndLastSendTime(id);
                })

                //消息路由失败
                .messageRouteFail((messageProperties, messageBody, exchange, routingKey) -> {
                    //保存路由失败的消息
                    messageService.saveUnRoutableMessage(messageProperties, messageBody, exchange, routingKey);
                })

                .build();
    }


}
