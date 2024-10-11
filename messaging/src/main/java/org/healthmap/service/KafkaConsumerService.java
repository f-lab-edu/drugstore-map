package org.healthmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// TODO: 제거
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {
    //테스트 log 찍기
/*    @KafkaListener(topics = "${kafka-config.consumer.topic}", groupId = "${kafka-config.consumer.groupId}")
    public void listen(String message) {
      log.info(message);
    }*/
}
