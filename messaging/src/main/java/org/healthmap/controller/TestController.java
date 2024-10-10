package org.healthmap.controller;

import lombok.RequiredArgsConstructor;
import org.healthmap.service.KafkaConsumerService;
import org.healthmap.service.KafkaProducerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 동작 확인 테스트용 controller
@RestController
@RequiredArgsConstructor
public class TestController {
    private final KafkaProducerService kafkaProducer;
    private final KafkaConsumerService kafkaConsumer;

    @GetMapping("/send")
    public String sendMessage(@RequestParam("message") String message) {
        kafkaProducer.sendMessage(message);
        return "Message sent successfully:"+message;
    }

    @GetMapping("/receive")
    public String receiveMessage() {
        return kafkaConsumer.receiveMessage("my-topic");
    }

}
