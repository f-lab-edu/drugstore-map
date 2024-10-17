package org.healthmap.openapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.db.medicalfacility.MedicalFacilityEntity;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.dto.BasicInfoDto;
import org.healthmap.openapi.api.MedicalFacilityApi;
import org.healthmap.openapi.config.UrlProperties;
import org.healthmap.openapi.dto.MedicalFacilityXmlDto;
import org.healthmap.openapi.error.OpenApiErrorCode;
import org.healthmap.openapi.exception.OpenApiProblemException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    public int saveAllBasicInfo() {
        List<BasicInfoDto> allDtoList = getAllBasicInfo();
        List<MedicalFacilityEntity> entityList = toMedicalFacilityEntityList(allDtoList);
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
        List<BasicInfoDto> basicInfoDtoList = getAllBasicInfo();
        List<String> idList = medicalFacilityRepository.findAllId();
        List<BasicInfoDto> updateDtoList = getUpdateDtoList(idList, basicInfoDtoList);

        log.info("update count : {}", updateDtoList.size());
        return updateMedicalFacilityList(updateDtoList);
    }

    // 새로 추가된 기본 정보 저장
    @Transactional
    public int addNewMedicalFacility() {
        List<BasicInfoDto> basicInfoDtoList = getAllBasicInfo();
        List<String> idList = medicalFacilityRepository.findAllId();
        List<BasicInfoDto> newDtoList = getNewBasicInfoList(idList, basicInfoDtoList);
        List<MedicalFacilityEntity> entityList = toMedicalFacilityEntityList(newDtoList);
        medicalFacilityRepository.customSaveAll(entityList);

        log.info("addNewMedicalFacilityList size : {}", entityList.size());
        return entityList.size();
    }

    // 병원, 약국의 기본정보 전체 데이터를 가져오는 메서드 (Batch에서 사용)
    public List<BasicInfoDto> getAllBasicInfo() {
        List<BasicInfoDto> drugstoreDtoList = getFacilityInfo(urlProperties.getDrugstoreUrl());
        List<BasicInfoDto> hospitalDtoList = getFacilityInfo(urlProperties.getHospitalUrl());
        drugstoreDtoList.addAll(hospitalDtoList);

        log.info("Total size : {}", drugstoreDtoList.size());
        return drugstoreDtoList;
    }

    // dto 리스트를 통해 기본정보 update
    private int updateMedicalFacilityList(List<BasicInfoDto> basicDtoList) {
        for (BasicInfoDto dto : basicDtoList) {
            medicalFacilityRepository.updateBasicInfo(
                    dto.getCode(), dto.getName(), dto.getAddress(), dto.getPhoneNumber(), dto.getPageUrl(),
                    dto.getType(), dto.getState(), dto.getCity(), dto.getTown(), dto.getPostNumber(), dto.getCoordinate()
            );
        }
        return basicDtoList.size();
    }

    //삭제할 병원, 약국리스트 반환
    private List<String> getRemovedMedicalFacilityList() {
        List<BasicInfoDto> allDtoList = getAllBasicInfo();
        List<String> dbIdList = medicalFacilityRepository.findAllId();
        List<String> apiIdList = allDtoList.stream().map(BasicInfoDto::getCode).toList();
        return getDeleteIdList(dbIdList, apiIdList);
    }

    // API에서 가져온 데이터 중 새로 추가된 데이터의 리스트를 반환하는 메서드
    private List<BasicInfoDto> getNewBasicInfoList(List<String> idList, List<BasicInfoDto> dtoList) {
        Set<String> dbIdSet = new HashSet<>(idList);
        List<BasicInfoDto> newDtoList = dtoList.stream()
                .filter(dto -> !dbIdSet.contains(dto.getCode()))
                .collect(Collectors.toList());
        log.info("newDtoList size: {}", newDtoList.size());
        return newDtoList;
    }

    // DB에 포함되어 있는 데이터 중 API에도 있는 데이터의 리스트를 반환 (update)
    private List<BasicInfoDto> getUpdateDtoList(List<String> dbIdList, List<BasicInfoDto> apiDtoList) {
        Set<String> dbIdSet = new HashSet<>(dbIdList);
        List<BasicInfoDto> updateIdList = apiDtoList.stream()
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

    // BasicInfoDto -> Entity
    private MedicalFacilityEntity toMedicalFacilityEntity(BasicInfoDto dto) {
        return Optional.ofNullable(dto)
                .map(x -> MedicalFacilityEntity.of(
                        dto.getCode(), dto.getName(), dto.getAddress(), dto.getPhoneNumber(), dto.getPageUrl(), dto.getType(),
                        dto.getState(), dto.getCity(), dto.getTown(), dto.getPostNumber(), dto.getCoordinate(),
                        null, null, null, null, null, null, null, null,
                        null, null, null, null, null, null, null, null, null))
                .orElseThrow(() -> new OpenApiProblemException(OpenApiErrorCode.NULL_POINT));
    }

    // BasicInfoDtoList -> EntityList
    private List<MedicalFacilityEntity> toMedicalFacilityEntityList(List<BasicInfoDto> dtoList) {
        return Optional.ofNullable(dtoList)
                .map(x -> x.stream()
                        .map(this::toMedicalFacilityEntity)
                        .collect(Collectors.toList())
                )
                .orElseThrow(() -> new OpenApiProblemException(OpenApiErrorCode.NULL_POINT));
    }


    // 약국의 기본정보 데이터를 가져오는 메서드
    private List<BasicInfoDto> getFacilityInfo(String url) {
        int pageSize = medicalFacilityApi.getPageSize(url);
        List<CompletableFuture<List<BasicInfoDto>>> futureList = new ArrayList<>();

        for (int i = 1; i <= pageSize; i++) {
            int finalI = i;
            CompletableFuture<List<BasicInfoDto>> future = CompletableFuture.supplyAsync(() -> {
                        List<MedicalFacilityXmlDto> xmlDtoList = medicalFacilityApi.getMedicalFacilityInfoList(url, finalI).join();
                        if (!xmlDtoList.isEmpty()) {
                            return convertToFacilityDtoList(xmlDtoList);
                        } else {
                            return new ArrayList<BasicInfoDto>();
                        }
                    }, executorService)
                    .exceptionally(ex -> {
                        log.error("진행 중에 오류가 발생했습니다. : {}", ex.getMessage());
                        return new ArrayList<>();
                    });
            futureList.add(future);
        }

        // 전체 실행 확인
        CompletableFuture<Void> allFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
        CompletableFuture<List<BasicInfoDto>> listFuture = allFuture.thenApply(x -> {
            return futureList.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        });
        List<BasicInfoDto> basicInfoDtoList = listFuture.join();
        log.info("전체 크기 : {}", basicInfoDtoList.size());
        return basicInfoDtoList;
    }

    // XmlDto -> Dto
    private List<BasicInfoDto> convertToFacilityDtoList(List<MedicalFacilityXmlDto> medicalFacilityXmlDtoList) {
        List<BasicInfoDto> basicInfoDtoList = new ArrayList<>();
        for (MedicalFacilityXmlDto dto : medicalFacilityXmlDtoList) {
            String phoneNumber = checkPhoneNumber(dto.getPhoneNumber());
            String pageUrl = checkPageUrl(dto.getPageUrl());
            Point coordinate = getPointFromXYPos(dto.getXPos(), dto.getYPos());

            BasicInfoDto medicalFacilityDto = new BasicInfoDto(dto.getCode(), dto.getName(), dto.getAddress(), phoneNumber, pageUrl, dto.getPostNumber(),
                    dto.getType(), dto.getState(), dto.getCity(), dto.getTown(), coordinate);
            basicInfoDtoList.add(medicalFacilityDto);
        }
        return basicInfoDtoList;
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
        Point point = geometryFactory.createPoint(new Coordinate(0, 0));
        point.setSRID(4326);
        try {
            if (xPos != null && yPos != null) {
                double x = Double.parseDouble(xPos);
                double y = Double.parseDouble(yPos);
                point = geometryFactory.createPoint(new Coordinate(x, y));
                point.setSRID(4326);
            }
        } catch (NumberFormatException e) {
            point = geometryFactory.createPoint(new Coordinate(0, 0));
            point.setSRID(4326);
            return point;
        }
        return point;
    }
}
