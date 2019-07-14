# rabbit-reliable-delivery
RabbitMQ消息可靠投递-消息入库

消息可靠投递有很多策略,但几乎所有策略都是从,发送前,confirmCallback,retuenCallback等点切入,本项目把这些点都提了出来,可以很方便的构建你自己的策略,当然demo里也提供了策略供测试

## 运行demo

1. 配置rabbitMQ以及mysql的连接

2. 启动项目,访问127.0.0.1:8080/send

3. 如何验证消息可靠: 数据库将会生成一个 test_message表,它中的消息即是发送的消息

4. demo/StrategyConfig 类中写好了两种策略,把@Bean打在哪个方法上就使用哪个策略

```java
    public MessageProcess myStrategy0() {
        ...
    }

    @Bean
    public MessageProcess myStrategy1() {
        ...
    }
```

   

## 集成

1. 把 producer 包复制到你的项目,确保能被spring扫描到,并且已依赖 spring-boot-starter-amqp 或 (spring-rabbit及spring-messaging)

2. 使用自己的策略: 构造一个 MessageProcess 放到spring容器中,用来提供策略

```java
    @Bean
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
```

3．集成成功后只要是通过 ReliableRabbitTemplate 发送的消息都使用了你定义的策略