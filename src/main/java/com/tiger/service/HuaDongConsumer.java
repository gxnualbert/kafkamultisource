package com.tiger.service;

import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;

/**
 * 类描述...
 *
 * @author Tiger Chen
 * created on 2023/8/12 21:34
 */

public class HuaDongConsumer {

    private final KafkaCommonService kafkaCommonService;

    public HuaDongConsumer(KafkaCommonService kafkaCommonService) {
        this.kafkaCommonService = kafkaCommonService;
    }

    @KafkaListener(topics = "test",containerFactory = "huaDongKafkaListenerContainerFactory")
    public void consumeMessage(List<String> msgList){
            kafkaCommonService.handleMessage(msgList);
    }

}
