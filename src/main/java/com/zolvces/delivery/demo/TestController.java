package com.zolvces.delivery.demo;

import com.zolvces.delivery.demo.listener.TestMessage;
import com.zolvces.delivery.producer.ReliableRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author niXueChao
 * @date 2019/7/11 20:39.
 */
@RestController
public class TestController {

    @Autowired
    private ReliableRabbitTemplate template;

    @GetMapping("/send")
    public String send() {

        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= 30; i++) {
            int j = i;
            threads.add(new Thread(() -> this.sendMessage("线程:" + j)));
        }

        threads.forEach(Thread::start);
        return "sending...";
    }

    private void sendMessage(String treadName) {
        for (int i = 1; i <= 1000; i++) {
            template.convertAndSend("testExchange", "mine.test", new TestMessage(treadName, i));
        }
    }

}
