package org.healthmap.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "kafka-config.consumer")
public class KafkaProperties {
    private final String detailTopic;
    private final String basicTopic;
    private final String updateTopic;
    private final String deleteTopic;
    private final String groupId;
    private final String saveGroupId;

    public KafkaProperties(String detailTopic, String basicTopic, String updateTopic, String deleteTopic, String groupId, String saveGroupId) {
        this.detailTopic = detailTopic;
        this.basicTopic = basicTopic;
        this.updateTopic = updateTopic;
        this.deleteTopic = deleteTopic;
        this.groupId = groupId;
        this.saveGroupId = saveGroupId;
    }
}
