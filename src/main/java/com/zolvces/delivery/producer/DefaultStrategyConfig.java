package com.zolvces.delivery.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 默认策略
 *
 * @author niXueChao
 * @date 2019/7/11 10:57.
 */
@Configuration
public class DefaultStrategyConfig {
    private final Logger LOGGER = LoggerFactory.getLogger(DefaultStrategyConfig.class);

    @Bean
    @ConditionalOnMissingBean(MessageProcess.class)
    public MessageProcess defaultStrategy() {
        return new MessageProcess.Builder()
                .preSend(messageInfo -> LOGGER.debug("第一次发送前.."))

                .messageAck(messageInfo -> LOGGER.debug("消息投递成功.."))

                .messageNack(messageInfo -> LOGGER.debug("消息投递失败"))

                .pickResendMessages(() -> {
                    LOGGER.debug("定时任务: 查找需要重发的消息..");
                    return null;
                })

                .preResend(id -> LOGGER.debug("消息重发前.."))

                .messageRouteFail((messageProperties, messageBody, exchange, routingKey) ->
                        LOGGER.debug("路由失败的消息"))

                .build();
    }


}
