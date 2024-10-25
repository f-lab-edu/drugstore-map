package org.healthmap.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.healthmap.config.KafkaProperties;
import org.healthmap.db.medicalfacility.MedicalFacilityEntity;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.dto.BasicInfoDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
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
    private final GeometryFactory geometryFactory;
    private final Point dummyPoint;
    private AtomicInteger count = new AtomicInteger(0); // 동작 확인용

    public BasicInfoSaveConsumer(KafkaTemplate<String, BasicInfoDto> kafkaTemplate, KafkaProperties kafkaProperties, MedicalFacilityRepository medicalFacilityRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
        this.medicalFacilityRepository = medicalFacilityRepository;
        this.geometryFactory = new GeometryFactory();
        this.dummyPoint = geometryFactory.createPoint(new Coordinate(0, 0));
        this.dummyPoint.setSRID(4326);
    }

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
            checkPointZero(dto);
            MedicalFacilityEntity saveEntity = convertDtoToEntity(dto);
            medicalFacilityRepository.save(saveEntity);
            count.incrementAndGet();
            log.info("save count : {}", count.get());
        }
    }

    private void checkPointZero(BasicInfoDto dto) {
        if (dto.getCoordinate().equalsExact(dummyPoint)) {

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
