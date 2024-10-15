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
import org.healthmap.openapi.dto.MedicalFacilityXmlDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalFacilityApiService {
    private final UrlProperties urlProperties;
    private final MedicalFacilityRepository medicalFacilityRepository;
    private final MedicalFacilityApi medicalFacilityApi;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

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

        log.info("update count : {}", updateDtoList.size());
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

        log.info("addNewMedicalFacilityList size : {}", entityList.size());
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

    // 병원, 약국의 기본정보 데이터를 가져오는 메서드 (Batch에서 사용)
    public List<MedicalFacilityDto> getAllMedicalFacility() {
        List<MedicalFacilityDto> drugstoreDtoList = getAllFacilityInfoAsync(urlProperties.getDrugstoreUrl());
        List<MedicalFacilityDto> hospitalDtoList = getAllFacilityInfoAsync(urlProperties.getHospitalUrl());
        drugstoreDtoList.addAll(hospitalDtoList);

        log.info("Total size : {}", drugstoreDtoList.size());
        return drugstoreDtoList;
    }

/*    // 약국의 기본정보 데이터를 가져오는 메서드
    private List<MedicalFacilityDto> getAllFacilityInfo(String url) {
        List<MedicalFacilityDto> allFacilityList = new ArrayList<>();
        int pageSize = medicalFacilityApi.getPageSize(url);

        for (int i = 1; i <= pageSize; i++) {
            List<MedicalFacilityDto> medicalFacilityInfo = medicalFacilityApi.getMedicalFacilityInfo(url, i);
            allFacilityList.addAll(medicalFacilityInfo);
        }

        log.info("allDrugstoreList : {}", allFacilityList.size());
        return allFacilityList;
    }*/

    private List<MedicalFacilityDto> getAllFacilityInfoAsync(String url) {
        int pageSize = medicalFacilityApi.getPageSize(url);
        List<CompletableFuture<List<MedicalFacilityDto>>> futureList = new ArrayList<>();

        for (int i = 1; i <= pageSize; i++) {
            int finalI = i;
            CompletableFuture<List<MedicalFacilityDto>> future = CompletableFuture.supplyAsync(() -> {
                        List<MedicalFacilityXmlDto> xmlDtoList = medicalFacilityApi.getMedicalFacilityInfoAsync(url, finalI).join();
                        if (!xmlDtoList.isEmpty()) {
                            return convertToFacilityDtoList(xmlDtoList);
                        } else {
                            return new ArrayList<MedicalFacilityDto>();
                        }
                    }, executorService)
                    .exceptionally(ex -> {
                        log.error("진행 중에 오류가 발생했습니다. : {}", ex.getMessage());
                        return new ArrayList<>();
                    });
            futureList.add(future);
        }

        CompletableFuture<Void> allFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
        CompletableFuture<List<MedicalFacilityDto>> listFuture = allFuture.thenApply(x -> {
            return futureList.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        });
        List<MedicalFacilityDto> medicalFacilityDtoList = listFuture.join();
        log.info("전체 크기 : {}", medicalFacilityDtoList.size());
        return medicalFacilityDtoList;
    }

    // XmlDto -> Dto
    private List<MedicalFacilityDto> convertToFacilityDtoList(List<MedicalFacilityXmlDto> medicalFacilityXmlDtoList) {
        List<MedicalFacilityDto> medicalFacilityDtoList = new ArrayList<>();
        for (MedicalFacilityXmlDto dto : medicalFacilityXmlDtoList) {
            String phoneNumber = checkPhoneNumber(dto.getPhoneNumber());
            String pageUrl = checkPageUrl(dto.getPageUrl());
            Point coordinate = getPointFromXYPos(dto.getXPos(), dto.getYPos());

            MedicalFacilityDto medicalFacilityDto = new MedicalFacilityDto(dto.getCode(), dto.getName(), dto.getAddress(), phoneNumber, pageUrl, dto.getPostNumber(),
                    dto.getType(), dto.getState(), dto.getCity(), dto.getTown(), coordinate);
            medicalFacilityDtoList.add(medicalFacilityDto);
        }
        return medicalFacilityDtoList;
    }

    // 전화번호 형식에 맞는지 확인
    private String checkPhoneNumber(String phoneNumber) {
        String phoneNumberPattern1 = "^\\d{2,3}-\\d{3,4}-\\d{4}$";
        String phoneNumberPattern2 = "^\\d{3,4}-\\d{4}$";
        Pattern pattern1 = Pattern.compile(phoneNumberPattern1);
        Pattern pattern2 = Pattern.compile(phoneNumberPattern2);

        if (phoneNumber != null) {
            if (pattern1.matcher(phoneNumber).matches() || pattern2.matcher(phoneNumber).matches()) {
                return phoneNumber;
            } else {
                return null;
            }
        } else {
            return phoneNumber;
        }
    }

    // pageUrl 확인
    private String checkPageUrl(String pageUrl) {
        if (pageUrl != null && pageUrl.equals("http://")) {
            return null;
        } else {
            return pageUrl;
        }
    }

    // x,y 좌표를 통해 Point 형식으로 변경
    private Point getPointFromXYPos(String xPos, String yPos) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = null;
        try {
            if (xPos == null || yPos == null) {
                point = geometryFactory.createPoint(new Coordinate(0, 0));
            } else {
                double x = Double.parseDouble(xPos);
                double y = Double.parseDouble(yPos);
                point = geometryFactory.createPoint(new Coordinate(x, y));
            }
            point.setSRID(4326);
        } catch (NumberFormatException e) {
            point = geometryFactory.createPoint(new Coordinate(0, 0));
        }
        return point;
    }
}
