package org.healthmap.openapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.openapi.api.FacilityDetailInfoApi;
import org.healthmap.openapi.dto.FacilityDetailDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacilityDetailService {
    private final FacilityDetailInfoApi facilityDetailInfoApi;
    private final MedicalFacilityRepository medicalFacilityRepository;

    // 세부정보 저장
    @Transactional
    public int saveFacilityDetail() {
        List<FacilityDetailDto> facilityDetailList = getFacilityDetailList();
        int updateSize = updateFacilityDetail(facilityDetailList);
        log.info("update end: {}", updateSize);
        return updateSize;
    }

    // 세부정보 데이터 가져오는 메서드
    private List<FacilityDetailDto> getFacilityDetailList() {
        List<FacilityDetailDto> facilityDetailDtoList = new ArrayList<>();
        List<String> allIdList = getAllIdList();
        for (String id : allIdList) {
            FacilityDetailDto facilityDetailInfo = facilityDetailInfoApi.getFacilityDetailInfo(id);
            if (facilityDetailInfo != null) {
                facilityDetailDtoList.add(facilityDetailInfo);
            }
        }

        log.info("facilityDetailDtoList: {}", facilityDetailDtoList.size());
        return facilityDetailDtoList;
    }

    // DTO 리스트로 update하는 메서드
    private int updateFacilityDetail(List<FacilityDetailDto> facilityDetailDtoList) {
        for (FacilityDetailDto dto : facilityDetailDtoList) {
            medicalFacilityRepository.updateDetail(
                    dto.getCode(), dto.getParking(), dto.getParkingEtc(), dto.getTreatmentMon(), dto.getTreatmentTue(), dto.getTreatmentWed(),
                    dto.getTreatmentThu(), dto.getTreatmentFri(), dto.getTreatmentSat(), dto.getTreatmentSun(), dto.getReceiveWeek(),
                    dto.getReceiveSat(), dto.getLunchWeek(), dto.getLunchSat(), dto.getNoTreatmentSun(), dto.getNoTreatmentHoliday(),
                    dto.getEmergencyDay(), dto.getEmergencyNight()
            );
        }
        return facilityDetailDtoList.size();
    }

    // DB의 id 리스트 반환하는 메서드
    private List<String> getAllIdList() {
        return medicalFacilityRepository.findAllId();
    }

}
