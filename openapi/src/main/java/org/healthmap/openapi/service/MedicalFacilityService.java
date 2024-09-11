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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        medicalFacilityRepository.customSaveAll(entityList);
        return entityList.size();
    }

    // 없어진 병원, 약국 삭제
    @Transactional
    public int deleteMedicalFacilityList() {
        List<String> removedIdList = getRemovedMedicalFacilityList();
        medicalFacilityRepository.deleteAllById(removedIdList);
        return removedIdList.size();
    }

    // 기본정보 갱신
    @Transactional
    public int updateAllMedicalFacility() {
        List<MedicalFacilityDto> medicalFacilityDtoList = getAllMedicalFacility();
        List<String> idList = medicalFacilityRepository.findAllId();
        List<MedicalFacilityDto> updateDtoList = getUpdateDtoList(idList, medicalFacilityDtoList);

        return updateMedicalFacilityList(updateDtoList);
    }

    // 새로 추가된 기본 정보 저장
    @Transactional
    public int addNewMedicalFacility() {
        List<MedicalFacilityDto> medicalFacilityDtoList = getAllMedicalFacility();
        List<String> idList = medicalFacilityRepository.findAllId();
        List<MedicalFacilityDto> newDtoList = getNewMedicalFacilityList(idList, medicalFacilityDtoList);
        List<MedicalFacilityEntity> entityList = MedicalFacilityConverter.toEntityList(newDtoList);
        medicalFacilityRepository.customSaveAll(entityList);
        return entityList.size();
    }


    // dto 리스트를 통해 기본정보 update
    private int updateMedicalFacilityList(List<MedicalFacilityDto> medicalFacilityDtoList) {
        for (MedicalFacilityDto dto : medicalFacilityDtoList) {
            medicalFacilityRepository.updateFacilityInfo(
                    dto.getCode(), dto.getName(), dto.getAddress(), dto.getPhoneNumber(), dto.getPageUrl(),
                    dto.getType(), dto.getState(), dto.getCity(), dto.getTown(), dto.getPostNumber(), dto.getCoordinate()
            );
        }
        return medicalFacilityDtoList.size();
    }

    //삭제할 병원, 약국리스트 반환
    private List<String> getRemovedMedicalFacilityList() {
        List<MedicalFacilityDto> allDtoList = getAllMedicalFacility();
        List<String> dbIdList = medicalFacilityRepository.findAllId();
        List<String> apiIdList = allDtoList.stream().map(MedicalFacilityDto::getCode).toList();
        return getDeleteIdList(dbIdList, apiIdList);
    }

    // API에서 가져온 데이터 중 새로 추가된 데이터의 리스트를 반환하는 메서드
    private List<MedicalFacilityDto> getNewMedicalFacilityList(List<String> idList, List<MedicalFacilityDto> dtoList) {
        Set<String> dbIdSet = new HashSet<>(idList);
        List<MedicalFacilityDto> newDtoList = dtoList.stream()
                .filter(dto -> !dbIdSet.contains(dto.getCode()))
                .collect(Collectors.toList());
        log.info("newDtoList size: {}", newDtoList.size());
        return newDtoList;
    }

    // DB에 포함되어 있는 데이터 중 API에도 있는 데이터의 리스트를 반환 (update)
    private List<MedicalFacilityDto> getUpdateDtoList(List<String> dbIdList, List<MedicalFacilityDto> apiDtoList) {
        Set<String> dbIdSet = new HashSet<>(dbIdList);
        List<MedicalFacilityDto> updateIdList = apiDtoList.stream()
                .filter(dto -> dbIdSet.contains(dto.getCode()))
                .collect(Collectors.toList());
        log.info("updateList size: {}", updateIdList.size());
        return updateIdList;
    }

    // API에서 가져온 데이터에서 DB에 없는 목록을 찾아 반환 (delete)
    private List<String> getDeleteIdList(List<String> dbIdList, List<String> apiIdList) {
        Set<String> apiIdSet = new HashSet<>(apiIdList);
        List<String> deleteIdList = dbIdList.stream()
                .filter(x -> !apiIdSet.contains(x))
                .toList();
        log.info("deleteList size : {}", deleteIdList.size());
        return deleteIdList;
    }

    // 병원, 약국의 기본정보 데이터를 가져오는 메서드
    private List<MedicalFacilityDto> getAllMedicalFacility() {
        List<MedicalFacilityDto> drugstoreDtoList = getAllDrugstoreInfo();
        List<MedicalFacilityDto> hospitalDtoList = getHospitalInfo();
        drugstoreDtoList.addAll(hospitalDtoList);

        log.info("total size : {}", drugstoreDtoList.size());
        return drugstoreDtoList;
    }

    // 약국의 기본정보 데이터를 가져오는 메서드
    private List<MedicalFacilityDto> getAllDrugstoreInfo() {
        List<MedicalFacilityDto> allDrugstoreList = new ArrayList<>();
        String drugstoreUrl = urlProperties.getDrugstoreUrl();
        int pageSize = medicalFacilityApi.getPageSize(drugstoreUrl);

        for (int i = 1; i <= pageSize; i++) {
            List<MedicalFacilityDto> medicalFacilityInfo = medicalFacilityApi.getMedicalFacilityInfo(drugstoreUrl, i);
            allDrugstoreList.addAll(medicalFacilityInfo);
            if (i == pageSize)
                log.info("i : {}, size: {}", i, medicalFacilityInfo.size());
        }

        log.info("allDrugstoreList : {}", allDrugstoreList.size());
        return allDrugstoreList;
    }

    // 병원의 기본정보 데이터를 가져오는 메서드
    private List<MedicalFacilityDto> getHospitalInfo() {
        List<MedicalFacilityDto> allHospitalDtoList = new ArrayList<>();
        String hospitalUrl = urlProperties.getHospitalUrl();
        int pageSize = medicalFacilityApi.getPageSize(hospitalUrl);

        for (int i = 1; i <= pageSize; i++) {
            List<MedicalFacilityDto> hospitalDtoList = medicalFacilityApi.getMedicalFacilityInfo(hospitalUrl, i);
            allHospitalDtoList.addAll(hospitalDtoList);
            if (i == pageSize)
                log.info("i : {}, size: {}", i, hospitalDtoList.size());
        }

        log.info("allHospitalDtoList : {}", allHospitalDtoList.size());
        return allHospitalDtoList;
    }
}
