package org.healthmap.service;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.config.KafkaProperties;
import org.healthmap.db.mongodb.model.MedicalFacility;
import org.healthmap.dto.BasicInfoDto;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicInfoUpdateConsumer {
    private final KafkaTemplate<String, BasicInfoDto> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final MongoTemplate mongoTemplate;
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private final AtomicInteger notFoundCount = new AtomicInteger(0);   // 동작 확인용
    private final AtomicInteger realCount = new AtomicInteger(0);       // 동작 확인용

    // 기본 정보 갱신
    @KafkaListener(topics = "${kafka-config.consumer.basic-topic}",
            groupId = "${kafka-config.consumer.groupId}",
            containerFactory = "basicInfoKafkaListenerContainerFactory")
    public void updateBasicInfo(BasicInfoDto dto) {
        CompletableFuture.supplyAsync(() -> {
                    try {
                        updateMedicalFacility(dto);
                        realCount.incrementAndGet();
                        if (realCount.get() % 5000 == 0) {
                            log.info("Basic info updated count : {}", realCount.get());
                        }
                    } catch (Exception e) {
                        log.error("Updating medical facility error: {}", e.getMessage(), e);
                        return null;
                    }
                    return dto;
                }, executorService)
                .thenAccept(basicInfoDto -> {
                    if (basicInfoDto != null) {
                        kafkaTemplate.send(kafkaProperties.getUpdateTopic(), basicInfoDto);
                    }
                });
    }

    private void updateMedicalFacility(BasicInfoDto dto) {
        Query query = Query.query(Criteria.where("_id").is(dto.getCode()));
        Update update = new Update();
        if (dto.getName() != null) {
            update.set("name", dto.getName());
        }
        if (dto.getAddress() != null) {
            update.set("address", dto.getAddress());
        }
        if (dto.getPhoneNumber() != null) {
            update.set("phoneNumber", dto.getPhoneNumber());
        }
        if (dto.getType() != null) {
            update.set("type", dto.getType());
        }
        if (dto.getState() != null) {
            update.set("state", dto.getState());
        }
        if (dto.getCity() != null) {
            update.set("city", dto.getCity());
        }
        if (dto.getTown() != null) {
            update.set("town", dto.getTown());
        }
        if (dto.getPostNumber() != null) {
            update.set("postNumber", dto.getPostNumber());
        }
        if (dto.getCoordinate() != null) {
            GeoJsonPoint geoJsonPoint = new GeoJsonPoint(dto.getCoordinate().getX(), dto.getCoordinate().getY());
            update.set("coordinate", geoJsonPoint);
        }
        if (!update.getUpdateObject().isEmpty()) {
            UpdateResult updateResult = mongoTemplate.updateFirst(query, update, MedicalFacility.class);
            if (updateResult.getMatchedCount() == 0) {
                notFoundCount.incrementAndGet();
                if (notFoundCount.get() != 0 && notFoundCount.get() % 1000 == 0) {
                    log.info("새로운 데이터 개수 : {}", notFoundCount.get());
                }
            }
        }
    }
}
