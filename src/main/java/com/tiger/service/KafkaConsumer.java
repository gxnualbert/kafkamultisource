package com.tiger.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * 类描述...
 *
 * @author Tiger Chen
 * created on 2023/8/12 20:11
 */


@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "test",containerFactory = "kafkaListenerContainerFactory")
    public void consumer(String message) {
        log.info("进入kafka消费者，消费消息：{}", message);
    }

}
