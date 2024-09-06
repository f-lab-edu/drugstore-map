package org.healthmap.openapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.db.medicalfacility.MedicalFacilityEntity;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.openapi.api.MedicalFacilityApi;
import org.healthmap.openapi.config.UrlProperties;
import org.healthmap.openapi.converter.MedicalFacilityConverter;
import org.healthmap.openapi.dto.MedicalFacilityDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalFacilityService {
    private final UrlProperties urlProperties;
    private final MedicalFacilityRepository medicalFacilityRepository;
    private final MedicalFacilityApi medicalFacilityApi;

    // 기본 병원, 약국 정보 저장
    @Transactional
    public int saveAllMedicalFacility() {
        List<MedicalFacilityDto> allDtoList = getAllMedicalFacility();
        List<MedicalFacilityEntity> entityList = MedicalFacilityConverter.toEntityList(allDtoList);
        medicalFacilityRepository.saveAll(entityList);
//        medicalFacilityRepository.customSaveAll(entityList);
        return entityList.size();
    }

    private List<MedicalFacilityDto> getAllMedicalFacility() {
        List<MedicalFacilityDto> drugstoreDtoList = getAllDrugstoreInfo();
        List<MedicalFacilityDto> hospitalDtoList = getHospitalInfo();
        drugstoreDtoList.addAll(hospitalDtoList);

        log.info("total size : {}", drugstoreDtoList.size());
        return drugstoreDtoList;
    }

    public List<MedicalFacilityDto> getAllDrugstoreInfo(){
        List<MedicalFacilityDto> allDrugstoreList = new ArrayList<>();
        String drugstoreUrl = urlProperties.getDrugstoreUrl();
        int pageSize = medicalFacilityApi.getPageSize(drugstoreUrl);

        for(int i = 1; i <= pageSize; i++){
            List<MedicalFacilityDto> medicalFacilityInfo = medicalFacilityApi.getMedicalFacilityInfo(drugstoreUrl, i);
            allDrugstoreList.addAll(medicalFacilityInfo);
            if(i == pageSize)
                log.info("i : {}, size: {}", i, medicalFacilityInfo.size());
        }

        log.info("allDrugstoreList : {}", allDrugstoreList.size());
        return allDrugstoreList;
    }


    public List<MedicalFacilityDto> getHospitalInfo() {
        List<MedicalFacilityDto> allHospitalDtoList = new ArrayList<>();
        String hospitalUrl = urlProperties.getHospitalUrl();
        int pageSize = medicalFacilityApi.getPageSize(hospitalUrl);

        for(int i = 1; i <= pageSize; i++){
            List<MedicalFacilityDto> hospitalDtoList = medicalFacilityApi.getMedicalFacilityInfo(hospitalUrl, i);
            allHospitalDtoList.addAll(hospitalDtoList);
            if(i == pageSize)
                log.info("i : {}, size: {}", i, hospitalDtoList.size());
        }

        log.info("allHospitalDtoList : {}", allHospitalDtoList.size());
        return allHospitalDtoList;
    }
}
