package org.healthmap.app.controller;

import lombok.RequiredArgsConstructor;
import org.healthmap.app.dto.HealthMapRequestDto;
import org.healthmap.app.dto.HealthMapResponseDto;
import org.healthmap.app.service.HealthMapService;
import org.healthmap.db.mongodb.model.MedicalFacility;
import org.healthmap.db.mongodb.repository.MedicalFacilityMongoRepository;
import org.healthmap.db.mysql.model.MedicalFacilityEntity;
import org.healthmap.db.mysql.repository.MedicalFacilityMysqlRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// mongoDB 적용 되는지 확인용 controller
@RestController
@RequiredArgsConstructor
public class TestController {
    private final MedicalFacilityMongoRepository medicalFacilityMongoRepository;
    private final MedicalFacilityMysqlRepository medicalFacilityMysqlRepository;
    private final HealthMapService healthMapService;

    @GetMapping("/mongo/test2")
    public void contentTest2() {
        MedicalFacilityEntity testEntity = medicalFacilityMysqlRepository.findById("JDQ4MTAxMiM1MSMkMiMkMCMkMDAkMzgxMTkxIzExIyQxIyQ3IyQ5MiQzNjEwMDIjNTEjJDEjJDIjJDgz").orElse(null);
        MedicalFacility medicalFacility = MedicalFacility.of(testEntity.getId(), testEntity.getName(), testEntity.getAddress(), testEntity.getPhoneNumber(), testEntity.getUrl(),
                testEntity.getType(), testEntity.getState(), testEntity.getCity(), testEntity.getTown(), testEntity.getPostNumber(), testEntity.getCoordinate(),
                testEntity.getParking(), testEntity.getParkingEtc(), testEntity.getTreatmentMon(), testEntity.getTreatmentTue(), testEntity.getTreatmentWed(),
                testEntity.getTreatmentThu(), testEntity.getTreatmentFri(), testEntity.getTreatmentSat(), testEntity.getTreatmentSun(), testEntity.getReceiveWeek(),
                testEntity.getReceiveSat(), testEntity.getLunchWeek(), testEntity.getLunchSat(), testEntity.getNoTreatmentSun(), testEntity.getNoTreatmentHoliday(),
                testEntity.getEmergencyDay(), testEntity.getEmergencyNight(), testEntity.getCreatedAt(), testEntity.getUpdatedAt());
        medicalFacilityMongoRepository.save(medicalFacility);
    }

    @GetMapping("/aggregation/test")
    public List<HealthMapResponseDto> aggregationTest(@ModelAttribute HealthMapRequestDto mapRequestDto) {
        return healthMapService.getNearByMedicalFacilityMongo(mapRequestDto);
    }
}
