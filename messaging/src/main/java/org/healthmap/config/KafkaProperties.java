package org.healthmap.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@ConfigurationProperties(prefix="kafka-config.consumer")
public class KafkaProperties {
    private final String topic;
    private final String groupId;

    public KafkaProperties(String topic, String groupId) {
        this.topic = topic;
        this.groupId = groupId;
    }
}
