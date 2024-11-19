package org.healthmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.healthmap.db.mongodb.model.MedicalFacility;
import org.healthmap.dto.BasicInfoDto;
import org.healthmap.dto.FacilityIdDto;
import org.healthmap.openapi.dto.FacilityDetailUpdateDto;
import org.healthmap.openapi.error.OpenApiErrorCode;
import org.healthmap.openapi.exception.OpenApiProblemException;
import org.healthmap.openapi.service.FacilityDetailApiService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetailInfoUpdateConsumer {
    private final KafkaTemplate<String, FacilityIdDto> kafkaTemplate;
    private final FacilityDetailApiService facilityDetailApiService;
    private final MongoTemplate mongoTemplate;
    private final AtomicInteger detailUpdateCount = new AtomicInteger(0);     // 동작 확인용

    // 세부 정보 저장 (DB 데이터 갱신)
    @KafkaListener(topics = "${kafka-config.consumer.detail-topic}",
            groupId = "${kafka-config.consumer.detail-groupId}",
            containerFactory = "saveBasicInfoContainerFactory")
    public void updateDetailInfo(ConsumerRecord<String, BasicInfoDto> record, Acknowledgment ack) {
        String id = record.value().getCode();
        try {
            FacilityDetailUpdateDto detailUpdateDto = facilityDetailApiService.getFacilityDetailInfo(id);
            if (detailUpdateDto != null) {
                updateFacilityDetail(detailUpdateDto);
                detailUpdateCount.incrementAndGet();
                if (detailUpdateCount.get() != 0 && detailUpdateCount.get() % 100 == 0) {
                    log.info("Detail info update count : {}", detailUpdateCount.get());
                }
            }
            kafkaTemplate.send("finished", new FacilityIdDto(id));
            ack.acknowledge();
        } catch (OpenApiProblemException oe) {
            if (oe.getOpenApiErrorCode() == OpenApiErrorCode.TOO_MANY_TRY) {
                log.error("too many try : {}", oe.getMessage(), oe);
                ack.nack(Duration.ofMillis(1000));
            } else {
                int retryCount = getRetryCount(record) + 1;
                if (retryCount < 5) {
                    addRetryCount(record, retryCount);
                    ack.nack(Duration.ofMillis(500));
                } else {
                    log.warn("retry count is over: {}", retryCount);
                    ack.acknowledge();
                }
            }
        } catch (Exception e) {
            log.error("update detail error : {}", e.getMessage(), e);
            ack.nack(Duration.ofMillis(500));
        }
    }

    private int getRetryCount(ConsumerRecord<String, BasicInfoDto> record) {
        return record.headers().lastHeader("retry-count") != null
                ? Integer.parseInt(new String(record.headers().lastHeader("retry-count").value()))
                : 0;
    }

    private void addRetryCount(ConsumerRecord<String, BasicInfoDto> record, int count) {
        record.headers().add("retry-count", Integer.toString(count).getBytes());
    }

    private void updateFacilityDetail(FacilityDetailUpdateDto dto) {
        Query query = Query.query(Criteria.where("_id").is(dto.getCode()));
        Update update = new Update();
        if (dto.getParking() != null) update.set("parking", dto.getParking());
        if (dto.getParkingEtc() != null) update.set("parkingEtc", dto.getParkingEtc());
        if (dto.getTreatmentMon() != null) update.set("treatmentMon", dto.getTreatmentMon());
        if (dto.getTreatmentTue() != null) update.set("treatmentTue", dto.getTreatmentTue());
        if (dto.getTreatmentWed() != null) update.set("treatmentWed", dto.getTreatmentWed());
        if (dto.getTreatmentThu() != null) update.set("treatmentThu", dto.getTreatmentThu());
        if (dto.getTreatmentFri() != null) update.set("treatmentFri", dto.getTreatmentFri());
        if (dto.getTreatmentSat() != null) update.set("treatmentSat", dto.getTreatmentSat());
        if (dto.getTreatmentSun() != null) update.set("treatmentSun", dto.getTreatmentSun());
        if (dto.getReceiveWeek() != null) update.set("receiveWeek", dto.getReceiveWeek());
        if (dto.getReceiveSat() != null) update.set("receiveSat", dto.getReceiveSat());
        if (dto.getLunchWeek() != null) update.set("lunchWeek", dto.getLunchWeek());
        if (dto.getLunchSat() != null) update.set("lunchSat", dto.getLunchSat());
        if (dto.getNoTreatmentSun() != null) update.set("noTreatmentSun", dto.getNoTreatmentSun());
        if (dto.getNoTreatmentHoliday() != null) update.set("noTreatmentHoliday", dto.getNoTreatmentHoliday());
        if (dto.getEmergencyDay() != null) update.set("emergencyDay", dto.getEmergencyDay());
        if (dto.getEmergencyNight() != null) update.set("emergencyNight", dto.getEmergencyNight());

        if (!update.getUpdateObject().isEmpty()) {
            update.set("updatedAt", LocalDateTime.now());
            mongoTemplate.updateFirst(query, update, MedicalFacility.class);
        }
    }
}
