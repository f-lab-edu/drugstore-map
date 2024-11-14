package org.healthmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.healthmap.db.mongodb.model.MedicalFacility;
import org.healthmap.db.mongodb.repository.MedicalFacilityMongoRepository;
import org.healthmap.db.mysql.model.MedicalFacilityEntity;
import org.healthmap.db.mysql.repository.MedicalFacilityMysqlRepository;
import org.healthmap.dto.FacilityIdDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class MySqlToMongoConsumer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MedicalFacilityMysqlRepository medicalFacilityMysqlRepository;
    private final MedicalFacilityMongoRepository medicalFacilityMongoRepository;
    private final AtomicInteger count = new AtomicInteger(0);


    @KafkaListener(topics = "${kafka-config.consumer.migration-topic}",
            groupId = "${kafka-config.consumer.migration-groupId}",
            containerFactory = "facilityIdContainerFactory")
    public void mysqlToMongo(ConsumerRecord<String, FacilityIdDto> consumerRecord, Acknowledgment ack) {
        String id = consumerRecord.value().getId();
        MedicalFacilityEntity medicalFacilityEntity = medicalFacilityMysqlRepository.findById(id).orElse(null);

        if (medicalFacilityEntity != null) {
            MedicalFacility medicalFacility = convertEntityToDocument(medicalFacilityEntity);
            medicalFacilityMongoRepository.save(medicalFacility);
            count.incrementAndGet();
            if (count.get() != 0 && count.get() % 100 == 0) {
                log.info("migration count : {}", count.get());
            }
            ack.acknowledge();
        }
        kafkaTemplate.send("finished", id);
    }

    private MedicalFacility convertEntityToDocument(MedicalFacilityEntity entity) {
        return MedicalFacility.of(entity.getId(), entity.getName(), entity.getAddress(), entity.getPhoneNumber(), entity.getUrl(),
                entity.getType(), entity.getState(), entity.getCity(), entity.getTown(), entity.getPostNumber(), entity.getCoordinate(),
                entity.getParking(), entity.getParkingEtc(), entity.getTreatmentMon(), entity.getTreatmentTue(), entity.getTreatmentWed(),
                entity.getTreatmentThu(), entity.getTreatmentFri(), entity.getTreatmentSat(), entity.getTreatmentSun(),
                entity.getReceiveWeek(), entity.getReceiveSat(), entity.getLunchWeek(), entity.getLunchSat(), entity.getNoTreatmentSun(),
                entity.getNoTreatmentHoliday(), entity.getEmergencyDay(), entity.getEmergencyNight(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
