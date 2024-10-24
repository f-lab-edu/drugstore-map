package org.healthmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.healthmap.config.KafkaProperties;
import org.healthmap.db.medicalfacility.MedicalFacilityEntity;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.dto.BasicInfoDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicInfoSaveConsumer {
    private final KafkaTemplate<String, BasicInfoDto> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final MedicalFacilityRepository medicalFacilityRepository;
    private AtomicInteger count = new AtomicInteger(0); // 동작 확인용

    @KafkaListener(
            topics = "${kafka-config.consumer.update-topic}",
            groupId = "${kafka-config.consumer.save-groupId}",
            containerFactory = "saveBasicInfoContainerFactory"
    )
    @Transactional
    public void saveBasicInfo(ConsumerRecord<String, BasicInfoDto> record, Acknowledgment ack, Consumer<?, ?> consumer) {
        BasicInfoDto dto = record.value();
        try {
            MedicalFacilityEntity findEntity = medicalFacilityRepository.findById(dto.getCode()).orElse(null);
            saveMedicalFacility(dto, findEntity);
            kafkaTemplate.send(kafkaProperties.getDetailTopic(), dto);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Save new medical facility error: {}", e.getMessage(), e);
            consumer.seek(new TopicPartition(record.topic(), record.partition()), record.offset());
        }
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
