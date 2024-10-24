package org.healthmap.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.healthmap.config.KafkaProperties;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.dto.BasicInfoDto;
import org.healthmap.openapi.dto.FacilityDetailUpdateDto;
import org.healthmap.openapi.service.FacilityDetailApiService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetailInfoUpdateConsumer {
    private final KafkaProperties kafkaProperties;
    private final TransactionTemplate transactionTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MedicalFacilityRepository medicalFacilityRepository;
    private final FacilityDetailApiService facilityDetailApiService;
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private AtomicInteger count = new AtomicInteger(0);     // 동작 확인용
    private AtomicInteger updateCount = new AtomicInteger(0);

    @KafkaListener(topics = "${kafka-config.consumer.detail-topic}",
            groupId = "${kafka-config.consumer.detail-groupId}",
            containerFactory = "saveBasicInfoContainerFactory")
    public void updateDetailInfo(ConsumerRecord<String, BasicInfoDto> record, Acknowledgment ack, Consumer<?, ?> consumer) {
        String id = record.value().getCode();
        facilityDetailApiService.getFacilityDetailInfo(id)
                .thenAccept(dto -> {
                    if (dto != null) {
                        Boolean transaction = transactionTemplate.execute(status -> {
                            try {
                                updateFacilityDetail(dto);
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
                        handleTransaction(ack, record, consumer, transaction);
                    }
                })
                .thenRun(() -> {
                    // 개수 확인용
                    kafkaTemplate.send("check", String.valueOf(count.get()));
                })
                .exceptionally(ex -> {
                    log.error("update detail error in exceptionally: {}", ex.getMessage(), ex);
                    handleException(ack, record, consumer);
                    return null;
                });
    }

    private static void handleTransaction(Acknowledgment ack, ConsumerRecord<String, BasicInfoDto> record, Consumer<?, ?> consumer, Boolean transaction) {
        if (transaction != null && transaction) {
            ack.acknowledge();
        } else {
            handleException(ack, record, consumer);
        }
    }

    private static void handleException(Acknowledgment ack, ConsumerRecord<String, BasicInfoDto> record, Consumer<?, ?> consumer) {
        try {
            consumer.seek(new TopicPartition(record.topic(), record.partition()), record.offset());
        } catch (Exception e) {
            log.error("nack failed: {}", e.getMessage(), e);
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
