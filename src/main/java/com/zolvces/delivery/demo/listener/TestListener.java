package com.zolvces.delivery.demo.listener;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author niXueChao
 * @date 2019/7/10 13:45.
 */
@Component
@RabbitListener(
        containerFactory = "jsonContainerFactory",
        bindings = @QueueBinding(value = @Queue("testQueue"), exchange = @Exchange(value = "testExchange"), key = "mine.test"))
public class TestListener {
    int i = 1;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    /**
     * 最后看i与投递数量的对比就能知道重复了多少
     *
     * @param testMessage
     */
    @RabbitHandler
    private void onMessage(TestMessage testMessage) {
        try {
            System.out.println(i);
            i++;
            jdbcTemplate.update("insert into test_message(thread_name, order_number) values(:threadName,:orderNumber)",
                    new BeanPropertySqlParameterSource(testMessage));

        } catch (Exception e) {
            System.out.println("重复数据");
        }
    }

}
