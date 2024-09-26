package org.healthmap.openapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.openapi.api.FacilityDetailInfoApi;
import org.healthmap.openapi.dto.FacilityDetailJsonDto;
import org.healthmap.openapi.dto.FacilityDetailUpdateDto;
import org.healthmap.openapi.error.OpenApiErrorCode;
import org.healthmap.openapi.exception.OpenApiProblemException;
import org.healthmap.openapi.pattern.PatternMatcherManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacilityDetailApiService {
    private final FacilityDetailInfoApi facilityDetailInfoApi;
    private final MedicalFacilityRepository medicalFacilityRepository;
    private final PatternMatcherManager patternMatcherManager;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1000);

    // 세부정보 저장
    //TODO: 변경 예정
    @Transactional
    public int saveFacilityDetail() {
        List<FacilityDetailUpdateDto> facilityDetailList = getFacilityDetailListTest();

        int updateSize = updateFacilityDetailList(facilityDetailList);
        log.info("update end: {}", updateSize);
        return updateSize;
    }

    //JsonDto를 updateDto로 변환 후 CompletableFuture로 반환
    //Async
    private List<CompletableFuture<FacilityDetailJsonDto>> getFacilityDetailInfo() {
        List<CompletableFuture<FacilityDetailJsonDto>> futureList = new ArrayList<>();
        List<String> allIdList = getAllIdList();

        for (String id : allIdList) {
            CompletableFuture<FacilityDetailJsonDto> future = facilityDetailInfoApi.getFacilityDetailInfoAsync(id);
            futureList.add(future);
        }
        return futureList;
    }

    // 리스트 안의 Dto를 변환
    private List<CompletableFuture<FacilityDetailUpdateDto>> convertJsonDtoToUpdateDto() {
        List<CompletableFuture<FacilityDetailUpdateDto>> futureList = new ArrayList<>();
        List<CompletableFuture<FacilityDetailJsonDto>> facilityDetailInfo = getFacilityDetailInfo();

        for (CompletableFuture<FacilityDetailJsonDto> future : facilityDetailInfo) {
            CompletableFuture<FacilityDetailUpdateDto> updateFuture = future.thenApplyAsync(jsonDto -> {
                        if(jsonDto != null) {
                            return convertJsonDto(jsonDto);
                        } else {
                            return null;
                        }
                    }, executorService)
                    .exceptionally(ex -> {
                        log.error("ConvertJsonDtoToUpdateDto 시에 문제 발생 : {}", ex.getMessage());
                        throw new OpenApiProblemException(OpenApiErrorCode.SERVER_ERROR);
                    });
            futureList.add(updateFuture);
        }
        return futureList;
    }

    // DTO 변환 로직
    public FacilityDetailUpdateDto convertJsonDto(FacilityDetailJsonDto dto) {
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

        return FacilityDetailUpdateDto.of(dto.getCode(), dto.getParkXpnsYn(), dto.getParkEtc(), treatmentMon, treatmentTue,
                treatmentWed, treatmentThu, treatmentFri, treatmentSat, treatmentSun, receiveWeek, receiveSat,
                lunchWeek, lunchSat, noTreatmentSun, noTreatmentHoliday, dto.getEmyDayYn(), dto.getEmyNgtYn());
    }

    private String getSundayTreatment(String noTreatmentSun, String treatmentStart, String treatmentEnd) {
        String treatmentN = "휴진";
        if(noTreatmentSun != null && noTreatmentSun.equals(treatmentN)) {
            return treatmentN;
        } else {
            return getTreatmentTime(treatmentStart, treatmentEnd);
        }
    }

    private String changeLunchTime(String lunchTime) {
        String nothing = "없음";
        Set<String> noLunch = new HashSet<>(List.of("공란", "휴진", "없음", "휴무", "전체휴진", "오전진료", "무", "점심시간없음"));
        if(lunchTime == null){
            return null;
        }

        String spaceRemoved = lunchTime.replaceAll(" ", "");
        if(noLunch.contains(spaceRemoved)){
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
        if(time == null) {
            return null;
        }
        String spaceRemoved = time.replaceAll(" ", "");
        if(treatmentYes.contains(spaceRemoved)) {
            return treatmentY;
        } else if(treatmentNo.contains(spaceRemoved)) {
            return treatmentN;
        }
        return time;
    }

    private String changeTimeFormat(String time) {
        return patternMatcherManager.matchAndFormat(time);
    }

    private String getTreatmentTime(String start, String end) {
        String result = null;
        if((start != null && start.length() == 4)
                && (end != null && end.length() == 4)) {
            String startTime = String.format("%s:%s", start.substring(0,2), start.substring(2,4));
            String endTime = String.format("%s:%s", end.substring(0,2), end.substring(2,4));
            result = String.format("%s ~ %s", startTime, endTime);
        }
        return result;
    }


    // 세부정보 데이터 가져오는 메서드
    //TODO: 변경 예정
    private List<FacilityDetailUpdateDto> getFacilityDetailListTest() {
        List<FacilityDetailUpdateDto> facilityDetailDtoList = Collections.synchronizedList(new ArrayList<>());
        List<String> allIdList = getAllIdList();    //10만개

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < allIdList.size(); i++) {
            int tempI = i;
            CompletableFuture<Void> future = CompletableFuture
                    .supplyAsync(() -> facilityDetailInfoApi.getFacilityDetailInfoFromJson(allIdList.get(tempI))
                            , executorService)
                    .thenAccept(facilityDetailInfo -> {
                        if (facilityDetailInfo != null) {
                            facilityDetailDtoList.add(facilityDetailInfo);
                        }
                        log.info("line: {}", tempI);
                    }).exceptionally(ex -> {
                        log.error("Error occurred while processing ID: {}", allIdList.get(tempI), ex);
                        return null;
                    });
            futures.add(future);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        allFutures.join();
        executorService.shutdown();

        log.info("facilityDetailDtoList: {}", facilityDetailDtoList.size());
        return facilityDetailDtoList;
    }

    // 세부정보 데이터 가져오는 메서드
    // Sync
    private List<FacilityDetailUpdateDto> getFacilityDetailList() {
        List<FacilityDetailUpdateDto> facilityDetailDtoList = new ArrayList<>();
        List<String> allIdList = getAllIdList();    //10만개
        for (String id : allIdList) {
            FacilityDetailUpdateDto facilityDetailInfo = facilityDetailInfoApi.getFacilityDetailInfoFromJson(id);
            if (facilityDetailInfo != null) {
                facilityDetailDtoList.add(facilityDetailInfo);
            }
        }

        log.info("facilityDetailDtoList: {}", facilityDetailDtoList.size());
        return facilityDetailDtoList;
    }

    // DTO 리스트로 update하는 메서드
    private int updateFacilityDetailList(List<FacilityDetailUpdateDto> facilityDetailDtoList) {
        for (FacilityDetailUpdateDto dto : facilityDetailDtoList) {
            medicalFacilityRepository.updateDetail(
                    dto.getCode(), dto.getParking(), dto.getParkingEtc(), dto.getTreatmentMon(), dto.getTreatmentTue(), dto.getTreatmentWed(),
                    dto.getTreatmentThu(), dto.getTreatmentFri(), dto.getTreatmentSat(), dto.getTreatmentSun(), dto.getReceiveWeek(),
                    dto.getReceiveSat(), dto.getLunchWeek(), dto.getLunchSat(), dto.getNoTreatmentSun(), dto.getNoTreatmentHoliday(),
                    dto.getEmergencyDay(), dto.getEmergencyNight()
            );
        }
        return facilityDetailDtoList.size();
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
