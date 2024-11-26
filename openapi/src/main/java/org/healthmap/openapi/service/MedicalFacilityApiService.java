package org.healthmap.openapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.dto.BasicInfoDto;
import org.healthmap.openapi.api.MedicalFacilityApi;
import org.healthmap.openapi.config.UrlProperties;
import org.healthmap.openapi.dto.MedicalFacilityXmlDto;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    private final MedicalFacilityApi medicalFacilityApi;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    // 병원, 약국의 기본정보 전체 데이터를 가져오는 메서드
    public List<BasicInfoDto> getAllBasicInfo() {
        List<BasicInfoDto> drugstoreDtoList = getFacilityInfo(urlProperties.getDrugstoreUrl());
        List<BasicInfoDto> hospitalDtoList = getFacilityInfo(urlProperties.getHospitalUrl());
        drugstoreDtoList.addAll(hospitalDtoList);

        log.info("Total size : {}", drugstoreDtoList.size());
        return drugstoreDtoList;
    }

    // 약국의 기본정보 데이터
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
        CompletableFuture<List<BasicInfoDto>> listFuture = allFuture.thenApply(
                x -> futureList.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList()));
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
            GeoJsonPoint coordinate = getPointFromXYPos(dto.getXPos(), dto.getYPos());

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
    private GeoJsonPoint getPointFromXYPos(String xPos, String yPos) {
        GeoJsonPoint point = new GeoJsonPoint(0, 0);
        try {
            if (xPos != null && yPos != null) {
                double x = Double.parseDouble(xPos);
                double y = Double.parseDouble(yPos);
                point = new GeoJsonPoint(x, y);
            }
        } catch (NumberFormatException e) {
            return point;
        }
        return point;
    }
}
