package org.healthmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.config.KafkaProperties;
import org.healthmap.db.medicalfacility.MedicalFacilityEntity;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.dto.BasicInfoDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicInfoSaveConsumer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TransactionTemplate transactionTemplate;
    private final KafkaProperties kafkaProperties;
    private final MedicalFacilityRepository medicalFacilityRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private AtomicInteger count = new AtomicInteger(0); // 동작 확인용

    @KafkaListener(topics = "${kafka-config.consumer.update-topic}",
            groupId = "${kafka-config.consumer.save-groupId}",
            containerFactory = "saveBasicInfoContainerFactory")
    public void saveBasicInfo(BasicInfoDto dto, Acknowledgment ack) {
        CompletableFuture.supplyAsync(() -> {
                    Boolean transaction = transactionTemplate.execute(status -> {
                        try {
                            MedicalFacilityEntity findEntity = medicalFacilityRepository.findById(dto.getCode()).orElse(null);
                            saveMedicalFacility(dto, findEntity);
                            return true;
                        } catch (Exception e) {
                            log.error("Save new medical facility error: {}", e.getMessage(), e);
                            status.setRollbackOnly();
                            return false;
                        }
                    });
                    if(transaction != null && transaction) {
                        return dto;
                    } else {
                        ack.nack(Duration.ofMillis(500));
                        return null;
                    }
                }, executorService)
                .thenAccept(basicInfoDto -> {
                    if (basicInfoDto != null) {
                        kafkaTemplate.send(kafkaProperties.getDetailTopic(), dto.getCode());
                        ack.acknowledge();
                    }
                });

    }

    private void saveMedicalFacility(BasicInfoDto dto, MedicalFacilityEntity entity) {
        if (entity == null) {
            MedicalFacilityEntity saveEntity = convertDtoToEntity(dto);
            medicalFacilityRepository.save(saveEntity);
            count.incrementAndGet();
            log.info("save count : {}", count.get());
        }
    }

    private MedicalFacilityEntity convertDtoToEntity(BasicInfoDto dto) {
        return MedicalFacilityEntity.of(dto.getCode(), dto.getName(), dto.getAddress(), dto.getPhoneNumber(), dto.getPageUrl(),
                dto.getType(), dto.getState(), dto.getCity(), dto.getTown(), dto.getPostNumber(), dto.getCoordinate(), null,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null, null,
                null, null);
    }
}
