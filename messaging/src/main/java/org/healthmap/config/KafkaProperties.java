package org.healthmap.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "kafka-config.consumer")
public class KafkaProperties {
    private final String detailTopic;
    private final String basicTopic;
    private final String updateTopic;
    private final String groupId;

    public KafkaProperties(String detailTopic, String basicTopic, String updateTopic, String groupId) {
        this.detailTopic = detailTopic;
        this.basicTopic = basicTopic;
        this.updateTopic = updateTopic;
        this.groupId = groupId;
    }
}
