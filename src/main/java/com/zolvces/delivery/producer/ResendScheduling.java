package com.zolvces.delivery.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author niXueChao
 * @date 2019/7/11 13:51.
 */
@Component
public class ResendScheduling {

    private final MessageProcess messageProcess;

    private final ReliableRabbitTemplate template;

    @Autowired
    public ResendScheduling(MessageProcess messageProcess, ReliableRabbitTemplate template) {
        this.messageProcess = messageProcess;
        this.template = template;
    }


    /** 每十秒重试一次
     *  理论上频率越快,消息重复率越高,
     *  最好的情况是,在十秒内刚好能够将需要重发的消息发送完毕,
     *  可以考虑整合Quartz 动态改变重发频率
     * @throws IOException
     */
    @Scheduled(cron = "*/10 * * * * ?")
    public void resend() throws IOException {
        List<MessageInfo> resendMessages = messageProcess.getPickResendMessages().accept();
//        if (resendMessages.size() > Integer.MAX_VALUE) {
//            callWarming("如果需要重发的消息是巨量的,说明rabbitMQ已瘫痪/断连")
//        }

        if (resendMessages == null) {
            return;
        }
        for (MessageInfo messageInfo : resendMessages) {
            //有可能出现转换异常,毕竟是从其它地方(如数据库)获取到的 MessageInfo
            template.reSend(messageInfo);
        }
    }
}
