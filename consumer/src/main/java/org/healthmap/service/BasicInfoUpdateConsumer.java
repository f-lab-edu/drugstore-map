package org.healthmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.config.KafkaProperties;
import org.healthmap.db.medicalfacility.MedicalFacilityEntity;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.dto.BasicInfoDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicInfoUpdateConsumer {
    private final MedicalFacilityRepository medicalFacilityRepository;
    private final KafkaTemplate<String, BasicInfoDto> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final TransactionTemplate transactionTemplate;
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private AtomicInteger count = new AtomicInteger(0);     // 동작 확인용
    private AtomicInteger realCount = new AtomicInteger(0); // 동작 확인용

    @KafkaListener(topics = "${kafka-config.consumer.basic-topic}",
            groupId = "${kafka-config.consumer.groupId}",
            containerFactory = "basicInfoKafkaListenerContainerFactory")
    public void updateBasicInfo(BasicInfoDto dto) {
        CompletableFuture.supplyAsync(() -> {
                    transactionTemplate.execute(status -> {
                        try {
                            updateMedicalFacility(dto);
                            realCount.incrementAndGet();
                            if (realCount.get() % 5000 == 0) {
                                log.info("updated count : {}", realCount.get());
                            }
                        } catch (Exception e) {
                            log.error("Updating medical facility error: {}", e.getMessage(), e);
                            status.setRollbackOnly();
                        }
                        return null;
                    });
                    return dto;
                }, executorService)
                .thenAccept(basicInfoDto -> {
                    if (basicInfoDto != null) {
                        kafkaTemplate.send(kafkaProperties.getUpdateTopic(), basicInfoDto);
                    }
                });
    }

    private void updateMedicalFacility(BasicInfoDto dto) {
        MedicalFacilityEntity entity = medicalFacilityRepository.findById(dto.getCode()).orElse(null);
        if (entity != null) {
            medicalFacilityRepository.updateBasicInfo(entity.getId(), entity.getName(), entity.getAddress(), entity.getPhoneNumber(),
                    entity.getUrl(), entity.getType(), entity.getState(), entity.getCity(), entity.getTown(), entity.getPostNumber(),
                    entity.getCoordinate());
        } else {
            count.incrementAndGet();
            if(count.get() != 0 && count.get() % 1000 == 0) {
                log.info("update 개수 : {}", count.get());
            }
        }
    }
}
