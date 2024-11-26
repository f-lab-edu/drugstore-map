package org.healthmap.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.healthmap.config.KafkaProperties;
import org.healthmap.db.mongodb.model.MedicalFacility;
import org.healthmap.db.mongodb.repository.MedicalFacilityRepository;
import org.healthmap.dto.BasicInfoDto;
import org.healthmap.openapi.service.MapApiService;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class BasicInfoSaveConsumer {
    private final KafkaTemplate<String, BasicInfoDto> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final MedicalFacilityRepository medicalFacilityRepository;
    private final MapApiService mapApiService;
    private final AtomicInteger count = new AtomicInteger(0); // 동작 확인용

    public BasicInfoSaveConsumer(KafkaTemplate<String, BasicInfoDto> kafkaTemplate, KafkaProperties kafkaProperties, MedicalFacilityRepository medicalFacilityMongoRepository, MapApiService mapApiService) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
        this.medicalFacilityRepository = medicalFacilityMongoRepository;
        this.mapApiService = mapApiService;
    }

    // 기본 정보 저장
    @KafkaListener(
            topics = "${kafka-config.consumer.update-topic}",   //basic-info-updated
            groupId = "${kafka-config.consumer.save-groupId}",
            containerFactory = "saveBasicInfoContainerFactory"
    )
    @Transactional
    public void saveBasicInfo(ConsumerRecord<String, BasicInfoDto> record, Acknowledgment ack, Consumer<?, ?> consumer) {
        BasicInfoDto dto = record.value();
        try {
            MedicalFacility findDocument = medicalFacilityRepository.findById(dto.getCode()).orElse(null);
            saveMedicalFacility(dto, findDocument);
            kafkaTemplate.send(kafkaProperties.getDetailTopic(), dto);      //detail-info
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Save new medical facility error: {}", e.getMessage(), e);
            consumer.seek(new TopicPartition(record.topic(), record.partition()), record.offset());
        }
    }

    private void saveMedicalFacility(BasicInfoDto dto, MedicalFacility doc) {
        if (doc == null) {
            BasicInfoDto basicInfoDto = checkCoordinate(dto);
            MedicalFacility saveDoc = convertDtoToDocument(basicInfoDto);
            medicalFacilityRepository.save(saveDoc);
            count.incrementAndGet();
            if (count.get() != 0 && count.get() % 500 == 0) {
                log.info("save count : {}", count.get());
            }
        }
    }

    private BasicInfoDto checkCoordinate(BasicInfoDto dto) {
        GeoJsonPoint coordinate = dto.getCoordinate();
        if (coordinate != null && coordinate.getX() == 0.0 && coordinate.getY() == 0.0) {
            return mapApiService.getCoordinate(dto);
        }
        return dto;
    }

    private MedicalFacility convertDtoToDocument(BasicInfoDto dto) {
        return MedicalFacility.of(dto.getCode(), dto.getName(), dto.getAddress(), dto.getPhoneNumber(), dto.getPageUrl(),
                dto.getType(), dto.getState(), dto.getCity(), dto.getTown(), dto.getPostNumber(), dto.getCoordinate(), null,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null, null,
                null, null);
    }
}
