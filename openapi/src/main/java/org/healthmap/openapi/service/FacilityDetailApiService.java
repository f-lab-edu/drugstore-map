package org.healthmap.openapi.service;

import lombok.extern.slf4j.Slf4j;
import org.healthmap.db.mysql.repository.MedicalFacilityMysqlRepository;
import org.healthmap.openapi.api.FacilityDetailInfoApi;
import org.healthmap.openapi.dto.FacilityDetailDto;
import org.healthmap.openapi.dto.FacilityDetailUpdateDto;
import org.healthmap.openapi.pattern.PatternMatcherManager;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FacilityDetailApiService {
    private final FacilityDetailInfoApi facilityDetailInfoApi;
    private final MedicalFacilityMysqlRepository medicalFacilityRepository;
    private final PatternMatcherManager patternMatcherManager;

    public FacilityDetailApiService(FacilityDetailInfoApi facilityDetailInfoApi, MedicalFacilityMysqlRepository medicalFacilityRepository, PatternMatcherManager patternMatcherManager) {
        this.facilityDetailInfoApi = facilityDetailInfoApi;
        this.medicalFacilityRepository = medicalFacilityRepository;
        this.patternMatcherManager = patternMatcherManager;
    }


    // 1. API로부터 JsonDTO 가져오기
    // 2. jsonDTO를 updateDTO로 변환
    // 3. repository에 update 진행
    public FacilityDetailUpdateDto getFacilityDetailInfo(String id) {
        FacilityDetailDto facilityDetailDto = facilityDetailInfoApi.getFacilityDetailDtoFromApi(id);

        if (facilityDetailDto != null) {
            try {
                return convertToUpdateDto(facilityDetailDto);
            } catch (Exception e) {
                log.error("진행중에 오류가 발생했습니다. : {}", e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }


    // DTO 변환 로직
    private FacilityDetailUpdateDto convertToUpdateDto(FacilityDetailDto dto) {
        String noTreatmentSun = checkNoTreatment(dto.getNoTrmtSun());
        String noTreatmentHoliday = checkNoTreatment(dto.getNoTrmtSun());
        String treatmentMon = getTreatmentTime(dto.getTrmtMonStart(), dto.getTrmtMonEnd());
        String treatmentTue = getTreatmentTime(dto.getTrmtTueStart(), dto.getTrmtTueEnd());
        String treatmentWed = getTreatmentTime(dto.getTrmtWedStart(), dto.getTrmtWedEnd());
        String treatmentThu = getTreatmentTime(dto.getTrmtThuStart(), dto.getTrmtThuEnd());
        String treatmentFri = getTreatmentTime(dto.getTrmtFriStart(), dto.getTrmtFriEnd());
        String treatmentSat = getTreatmentTime(dto.getTrmtSatStart(), dto.getTrmtSatEnd());
        String treatmentSun = getSundayTreatment(noTreatmentSun, dto.getTrmtSunStart(), dto.getTrmtSunEnd());
        String receiveWeek = changeTimeFormat(dto.getRcvWeek());
        String receiveSat = changeTimeFormat(dto.getRcvSat());
        String lunchWeek = changeLunchTime(dto.getLunchWeek());
        String lunchSat = changeLunchTime(dto.getLunchSat());

        return FacilityDetailUpdateDto.of(dto.getCode(),  dto.getParkXpnsYn(),
                dto.getParkEtc(), treatmentMon, treatmentTue, treatmentWed, treatmentThu, treatmentFri, treatmentSat,
                treatmentSun, receiveWeek, receiveSat, lunchWeek, lunchSat, noTreatmentSun, noTreatmentHoliday,
                dto.getEmyDayYn(), dto.getEmyNgtYn());
    }

    private String getSundayTreatment(String noTreatmentSun, String treatmentStart, String treatmentEnd) {
        String treatmentN = "휴진";
        if (noTreatmentSun != null && noTreatmentSun.equals(treatmentN)) {
            return treatmentN;
        } else {
            return getTreatmentTime(treatmentStart, treatmentEnd);
        }
    }

    private String changeLunchTime(String lunchTime) {
        String nothing = "없음";
        Set<String> noLunch = new HashSet<>(List.of("공란", "휴진", "없음", "휴무", "전체휴진", "오전진료", "무", "점심시간없음"));
        if (lunchTime == null) {
            return null;
        }

        String spaceRemoved = lunchTime.replaceAll(" ", "");
        if (noLunch.contains(spaceRemoved)) {
            return nothing;
        } else {
            return changeTimeFormat(lunchTime);
        }
    }

    private String checkNoTreatment(String time) {
        String treatmentY = "휴진없음";
        String treatmentN = "휴진";
        Set<String> treatmentYes = new HashSet<>(List.of("정상근무", "정규진료", "진료", "휴진없음"));
        Set<String> treatmentNo = new HashSet<>(
                List.of("전부휴진", "모두휴진", "휴진", "휴무", "전부휴일", "전부휴무",
                        "전체휴진", "매주휴진", "종일휴진", "휴뮤", "휴진입니다.")
        );
        if (time == null) {
            return null;
        }
        String spaceRemoved = time.replaceAll(" ", "");
        if (treatmentYes.contains(spaceRemoved)) {
            return treatmentY;
        } else if (treatmentNo.contains(spaceRemoved)) {
            return treatmentN;
        }
        return time;
    }

    private String changeTimeFormat(String time) {
        return patternMatcherManager.matchAndFormat(time);
    }

    private String getTreatmentTime(String start, String end) {
        String result = null;
        if ((start != null && start.length() == 4)
                && (end != null && end.length() == 4)) {
            String startTime = String.format("%s:%s", start.substring(0, 2), start.substring(2, 4));
            String endTime = String.format("%s:%s", end.substring(0, 2), end.substring(2, 4));
            result = String.format("%s ~ %s", startTime, endTime);
        }
        return result;
    }

    // DTO로 update하는 메서드
    private void updateFacilityDetail(FacilityDetailUpdateDto dto) {
        medicalFacilityRepository.updateDetail(
                dto.getCode(), dto.getParking(), dto.getParkingEtc(), dto.getTreatmentMon(), dto.getTreatmentTue(), dto.getTreatmentWed(),
                dto.getTreatmentThu(), dto.getTreatmentFri(), dto.getTreatmentSat(), dto.getTreatmentSun(), dto.getReceiveWeek(),
                dto.getReceiveSat(), dto.getLunchWeek(), dto.getLunchSat(), dto.getNoTreatmentSun(), dto.getNoTreatmentHoliday(),
                dto.getEmergencyDay(), dto.getEmergencyNight()
        );
    }

    // DB의 id 리스트 반환하는 메서드
    private List<String> getAllIdList() {
        return medicalFacilityRepository.findAllId();
    }

}
