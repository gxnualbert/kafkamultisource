package com.tiger.config.huadong;

import com.tiger.util.KafkaUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * 类描述...
 *
 * @author Tiger Chen
 * created on 2023/8/13 8:00
 */

@Configuration
@ConditionalOnProperty(prefix = "spring.east-china.kafka", name = "enabled", havingValue = "true")
public class HuaDongProducerConfig {

    @Bean(name = "huaDongKafkaTemplate")
    public KafkaTemplate<Object, Object> huaDongKafkaTemplate(@Qualifier("huaDongKafkaProperties") KafkaProperties huaDongKafkaProperties) {
        ProducerFactory<Object, Object> producerFactory = KafkaUtil.getProducerFactory(huaDongKafkaProperties);
        return new KafkaTemplate<>(producerFactory);
    }


}
