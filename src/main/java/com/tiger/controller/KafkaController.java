package com.tiger.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类描述...
 *
 * @author Tiger Chen
 * created on 2023/8/12 20:05
 */

@RestController
@RequiredArgsConstructor
@Slf4j
public class KafkaController {

//    private final KafkaProducer<String, String> kafkaProducer;
//
//
//    @RequestMapping("/send")
//    public void send() {
//        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("test", "key", "value");
//        kafkaProducer.send(producerRecord, (metadata, exception) -> {
//            if (exception != null) {
//                exception.printStackTrace();
//            }
//            log.info("进入kafka生产者回调函数");
//        });
//    }

}
