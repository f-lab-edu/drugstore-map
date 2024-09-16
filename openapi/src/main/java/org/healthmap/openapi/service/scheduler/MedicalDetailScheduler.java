package org.healthmap.openapi.service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.openapi.service.FacilityDetailApiService;
import org.healthmap.openapi.service.MedicalFacilityApiService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
@RequiredArgsConstructor
public class MedicalDetailScheduler {
    private final FacilityDetailApiService facilityDetailService;
    private final MedicalFacilityApiService medicalFacilityService;

    /**
     * *           *　　　　　　*　　　　　　*　　　　　　*　　　　　　*
     * 초(0-59)   분(0-59)　　시간(0-23)　　일(1-31)　　월(1-12)　　요일(0-7)
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void deleteRemovedMedicalFacilityInfo() {
        String time = getDateTimeNow();
        log.info("deleteRemovedMedicalFacilityInfo start : {}", time);
        medicalFacilityService.deleteMedicalFacilityList();
    }

    @Scheduled(cron = "0 10 1 * * *")
    public void addNewMedicalFacilityInfo() {
        String time = getDateTimeNow();
        log.info("addNewMedicalFacilityInfo start : {}", time);
        medicalFacilityService.addNewMedicalFacility();
    }

    @Scheduled(cron = "0 20 1 * * *")
    public void updateAllMedicalFacilityInfo() {
        String time = getDateTimeNow();
        log.info("updateAllMedicalFacilityInfo start : {}", time);
        medicalFacilityService.updateAllMedicalFacility();
    }

    @Scheduled(cron = "0 30 1 * * *")
    public void updateFacilityDetailInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.now();
        Instant startTime = Instant.now();
        log.info("updateFacilityDetailInfo start : {}", start.format(formatter));
        facilityDetailService.saveFacilityDetail();
        Instant endTime = Instant.now();
        log.info("수행시간: {}", Duration.between(startTime, endTime).toSeconds() + " s");
    }

    private static String getDateTimeNow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.now();
        return start.format(formatter);
    }
}
