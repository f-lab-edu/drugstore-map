package org.healthmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.dto.BasicInfoDto;
import org.healthmap.openapi.dto.FacilityDetailUpdateDto;
import org.healthmap.openapi.error.OpenApiErrorCode;
import org.healthmap.openapi.exception.OpenApiProblemException;
import org.healthmap.openapi.service.FacilityDetailApiService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetailInfoUpdateConsumer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final TransactionTemplate transactionTemplate;
    private final MedicalFacilityRepository medicalFacilityRepository;
    private final FacilityDetailApiService facilityDetailApiService;
    private final AtomicInteger count = new AtomicInteger(0);     // 동작 확인용

    @KafkaListener(topics = "${kafka-config.consumer.detail-topic}",
            groupId = "${kafka-config.consumer.detail-groupId}",
            containerFactory = "saveBasicInfoContainerFactory")
    public void updateDetailInfo(ConsumerRecord<String, BasicInfoDto> record, Acknowledgment ack) {
        String id = record.value().getCode();
        try {
            FacilityDetailUpdateDto detailUpdateDto = facilityDetailApiService.getFacilityDetailInfo(id);
            if (detailUpdateDto != null) {
                Boolean transaction = transactionTemplate.execute(status -> {
                    try {
                        updateFacilityDetail(detailUpdateDto);
                        count.incrementAndGet();
                        if (count.get() != 0 && count.get() % 100 == 0) {
                            log.info("updated detail count : {}", count.get());
                        }
                        return true;
                    } catch (Exception e) {
                        log.error("update detail error : {}", e.getMessage(), e);
                        status.setRollbackOnly();
                        return false;
                    }
                });
                if (transaction != null && !transaction) {
                    ack.nack(Duration.ofMillis(500));
                    return;
                }
            }
            // 개수 확인용
            kafkaTemplate.send("check", String.valueOf(count.get()));
            ack.acknowledge();
        } catch (OpenApiProblemException oe) {
            if (oe.getOpenApiErrorCode() == OpenApiErrorCode.TOO_MANY_TRY) {
                log.error("too many try : {}", oe.getMessage(), oe);
                ack.nack(Duration.ofMillis(500));
            } else {
                int retryCount = getRetryCount(record) + 1;
                if (retryCount < 5) {
                    addRetryCount(record, retryCount);
                    ack.nack(Duration.ofMillis(500));
                } else {
                    kafkaTemplate.send("error-check", "-"); // 차후 제거
                    ack.acknowledge();
                }
            }
        } catch (Exception e) {
            log.error("update detail error : {}", e.getMessage(), e);
            ack.nack(Duration.ofMillis(500));
        }
    }

    //TODO: 사용할지 말지 결정
    private int getRetryCount(ConsumerRecord<String, BasicInfoDto> record) {
        return record.headers().lastHeader("retry-count") != null
                ? Integer.parseInt(new String(record.headers().lastHeader("retry-count").value()))
                : 0;
    }

    //TODO: 사용할지 말지 결정
    private void addRetryCount(ConsumerRecord<String, BasicInfoDto> record, int count) {
        record.headers().add("retry-count", Integer.toString(count).getBytes());
    }


    private void updateFacilityDetail(FacilityDetailUpdateDto dto) {
        medicalFacilityRepository.updateDetail(
                dto.getCode(), dto.getParking(), dto.getParkingEtc(), dto.getTreatmentMon(), dto.getTreatmentTue(), dto.getTreatmentWed(),
                dto.getTreatmentThu(), dto.getTreatmentFri(), dto.getTreatmentSat(), dto.getTreatmentSun(), dto.getReceiveWeek(),
                dto.getReceiveSat(), dto.getLunchWeek(), dto.getLunchSat(), dto.getNoTreatmentSun(), dto.getNoTreatmentHoliday(),
                dto.getEmergencyDay(), dto.getEmergencyNight()
        );
    }
}
