package org.healthmap.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
