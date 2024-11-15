package org.healthmap.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.healthmap.dto.BasicInfoDto;
import org.healthmap.dto.FacilityIdDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    private final TaskExecutorConfig taskExecutorConfig;
    private final KafkaProperties kafkaProperties;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    public KafkaConsumerConfig(TaskExecutorConfig taskExecutorConfig, KafkaProperties kafkaProperties) {
        this.taskExecutorConfig = taskExecutorConfig;
        this.kafkaProperties = kafkaProperties;
    }

    public ConsumerFactory<String, String> consumerConfig() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        //오프셋 수동 관리
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    public ConsumerFactory<String, BasicInfoDto> basicInfoConsumerConfig() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.KEY_DEFAULT_TYPE, "org.healthmap.dto.BasicInfoDto");
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        //오프셋 수동 관리 (확인 필요)
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    public ConsumerFactory<String, FacilityIdDto> facilityIdConsumerConfig() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.KEY_DEFAULT_TYPE, "org.healthmap.dto.FacilityIdDto");
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> stringKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerConfig());
        factory.setConcurrency(5);  //TODO: partition 개수만큼 차후 변경
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setListenerTaskExecutor(taskExecutorConfig.executor());

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BasicInfoDto> basicInfoKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BasicInfoDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(basicInfoConsumerConfig());
        factory.setConcurrency(10); //TODO: partition 개수만큼
        factory.getContainerProperties().setListenerTaskExecutor(taskExecutorConfig.executor());
        //TODO: ACK를 수동? 자동?

        return factory;
    }   
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BasicInfoDto> saveBasicInfoContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BasicInfoDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(basicInfoConsumerConfig());
        factory.setConcurrency(5);
        factory.getContainerProperties().setListenerTaskExecutor(taskExecutorConfig.saveExecutor());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FacilityIdDto> facilityIdContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FacilityIdDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(facilityIdConsumerConfig());
        factory.setConcurrency(5);
        factory.getContainerProperties().setListenerTaskExecutor(taskExecutorConfig.saveExecutor());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
}
