package com.tiger.config.huadong;

import com.tiger.service.HuaDongConsumer;
import com.tiger.service.KafkaCommonService;
import com.tiger.util.KafkaUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

/**
 * 类描述...
 *
 * @author Tiger Chen
 * created on 2023/8/12 20:17
 */

@Configuration
@ConditionalOnProperty(prefix = "spring.east-china.kafka", name = "enabled", havingValue = "true")
public class HuaDongConsumerConfig {

    /**
     * 从配置文件读取kafka的配置信息
     * new对象的时候，属性是空的，但是后面springboot 会根据prefix 自动给字段填充对应的属性
     *
     * @return KafkaProperties
     */

    @ConfigurationProperties(prefix = "spring.east-china.kafka")
    @Bean(name = "huaDongKafkaProperties")
    public KafkaProperties huaDongKafkaProperties() {
        return new KafkaProperties();
    }


    /**
     * 从配置文件读取kafka的配置信息，并自定义一个 DefaultKafkaConsumerFactory
     *
     * @param huaDongKafkaProperties huaDongKafkaProperties
     * @return ConsumerFactory
     */
    @Bean(name = "huaDongConsumerFactory")
    public ConsumerFactory<?, ?> huaDongConsumerFactory(@Qualifier("huaDongKafkaProperties") KafkaProperties huaDongKafkaProperties) {
        return KafkaUtil.getConsumerFactory(huaDongKafkaProperties);
    }

    @Bean(name = "huaDongKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<?, ?> huaDongKafkaListenerContainerFactory(
            @Qualifier("huaDongKafkaProperties") KafkaProperties huaDongKafkaProperties,
            @Qualifier("huaDongConsumerFactory") ConsumerFactory<Object, Object> huaDongConsumerFactory) {
        return KafkaUtil.kafkaListenerContainerFactory(huaDongKafkaProperties, huaDongConsumerFactory);
    }

    /**
     * 实例化华东kafka，并放入spring容器
     *
     * @param kafkaCommonService
     * @return
     */
    @Bean(name = "huaDongConsumer")
    public HuaDongConsumer huaDongConsumer(KafkaCommonService kafkaCommonService) {
        return new HuaDongConsumer(kafkaCommonService);
    }



}
