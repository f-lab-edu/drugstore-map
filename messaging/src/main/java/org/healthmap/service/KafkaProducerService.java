package org.healthmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

// TODO: 제거할지 확인
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducerService {
    private static final String TOPIC = "my-topic";
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
        log.info("Test Sent Message: {}",message);
    }
}
