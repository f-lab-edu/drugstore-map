package org.healthmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

// TODO: 제거
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final KafkaConsumer<String, String> kafkaConsumer;

    @KafkaListener(topics = "${kafka-config.consumer.topic}", groupId = "${kafka-config.consumer.groupId}")
    public void listen(String message) {
      log.info(message);
    }

    // 테스트용 consumer
    public String receiveMessage(String topic) {
        kafkaConsumer.subscribe(Collections.singleton(topic));
        ConsumerRecords<String, String> poll = kafkaConsumer.poll(Duration.ofMillis(1000));
        StringBuilder messages = new StringBuilder();
        for(ConsumerRecord<String, String> record : poll) {
            messages.append(record.value());
        }
        kafkaConsumer.commitSync();
        return messages.toString();
    }
}
