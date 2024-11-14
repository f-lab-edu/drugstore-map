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
    private final String migrationTopic;
    private final String groupId;
    private final String saveGroupId;
    private final String detailGroupId;
    private final String migrationGroupId;

    public KafkaProperties(String detailTopic, String basicTopic, String updateTopic, String deleteTopic, String migrationTopic, String groupId, String saveGroupId, String detailGroupId, String migrationGroupId) {
        this.detailTopic = detailTopic;
        this.basicTopic = basicTopic;
        this.updateTopic = updateTopic;
        this.deleteTopic = deleteTopic;
        this.migrationTopic = migrationTopic;
        this.groupId = groupId;
        this.saveGroupId = saveGroupId;
        this.detailGroupId = detailGroupId;
        this.migrationGroupId = migrationGroupId;
    }
}
