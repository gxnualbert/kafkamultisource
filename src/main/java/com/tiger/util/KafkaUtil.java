package com.tiger.util;

import com.fasterxml.jackson.databind.JsonSerializer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.plain.PlainLoginModule;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 类描述...
 *
 * @author Tiger Chen
 * created on 2023/8/12 21:11
 */

public class KafkaUtil {
    /**
     * 自定义消费者工厂
     *
     * @param kafkaProperties kafkaProperties
     * @return ConsumerFactory<?, ?>
     */
    public static ConsumerFactory<?, ?> getConsumerFactory(KafkaProperties kafkaProperties) {

        Map<String, Object> props = getCommonClientConfigs(kafkaProperties);
        // consumer 配置
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getConsumer().getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getConsumer().getValueDeserializer());
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, ConsumerConfig.DEFAULT_MAX_PARTITION_FETCH_BYTES * 5);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaProperties.getConsumer().getMaxPollRecords());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());

        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * 自定义生产者工厂
     *
     * @param kafkaProperties kafkaProperties
     * @return ProducerFactory<Object, Object>
     */
    public static ProducerFactory<Object, Object> getProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = getCommonClientConfigs(kafkaProperties);
        // producer 配置
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    public static ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            KafkaProperties kafkaProperties,
            ConsumerFactory<Object, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        // 设置并发数量
        if (KafkaProperties.Listener.Type.BATCH == kafkaProperties.getListener().getType()) {
            factory.setBatchListener(true);
            factory.setConcurrency(kafkaProperties.getListener().getConcurrency());
        }
        return factory;
    }

    /**
     * 抽取公用的kafka配置
     *
     * @param kafkaProperties kafkaProperties
     * @return Map<String, Object>
     */
    public static Map<String, Object> getCommonClientConfigs(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>();
        // bootstrap 配置
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        //jaas配置
        props.put(SaslConfigs.SASL_JAAS_CONFIG, String.format("%s required username=\"%s\" password=\"%s\";",
                PlainLoginModule.class.getName(), kafkaProperties.getJaas().getOptions().get("username"), kafkaProperties.getJaas().getOptions().get("password")));
        //SSL 配置
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, kafkaProperties.getSsl().getTrustStorePassword());
        props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, kafkaProperties.getSsl().getTrustStoreLocation());
        //properties 配置
        props.put(SaslConfigs.SASL_MECHANISM, kafkaProperties.getProperties().get("sasl.mechanism"));
        props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, kafkaProperties.getProperties().get("security.protocol"));
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, kafkaProperties.getProperties().get("ssl.endpoint-identification-algorithm"));

        return props;
    }
}
