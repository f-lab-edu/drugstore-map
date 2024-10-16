package org.healthmap.openapi.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.openapi.api.FacilityDetailInfoApi;
import org.healthmap.openapi.dto.FacilityDetailDto;
import org.healthmap.openapi.dto.FacilityDetailUpdateDto;
import org.healthmap.openapi.pattern.PatternMatcherManager;
import org.healthmap.openapi.utility.RateLimitBucket;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class FacilityDetailApiService {
    private final FacilityDetailInfoApi facilityDetailInfoApi;
    private final MedicalFacilityRepository medicalFacilityRepository;
    private final PatternMatcherManager patternMatcherManager;
    private final ExecutorService executorService;
    private final BlockingQueue<String> idQueue;

    public FacilityDetailApiService(FacilityDetailInfoApi facilityDetailInfoApi, MedicalFacilityRepository medicalFacilityRepository, PatternMatcherManager patternMatcherManager, RateLimitBucket rateLimitBucket) {
        this.facilityDetailInfoApi = facilityDetailInfoApi;
        this.medicalFacilityRepository = medicalFacilityRepository;
        this.patternMatcherManager = patternMatcherManager;
        this.executorService = Executors.newFixedThreadPool(100);
        this.idQueue = new LinkedBlockingQueue<>(2000000);
    }


    // 1. API로부터 JsonDTO 가져오기
    // 2. jsonDTO를 updateDTO로 변환
    // 3. repository에 update 진행
    @Transactional
    public CompletableFuture<Integer> saveFacilityDetail() {
        List<String> allIdList = getAllIdList();
        idQueue.addAll(allIdList);

        AtomicInteger updateCount = new AtomicInteger(0);
        List<CompletableFuture<Void>> futureList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<CompletableFuture<Void>> allStepList = new ArrayList<>();
                try {
                    while (!idQueue.isEmpty()) {
                        if (idQueue.size() % 500 == 0) {
                            log.info("idQueue size : {}", idQueue.size());
                        }
                        String id = idQueue.poll(10, TimeUnit.SECONDS);

                        if (id == null && idQueue.isEmpty()) {
                            log.info("idQueue is empty");
                            break;
                        } else if (id == null) {
                            log.info("id: null");
                            continue;
                        }

                        CompletableFuture<FacilityDetailDto> jsonDtoFuture = facilityDetailInfoApi.getFacilityDetailDtoFromApi(id, idQueue);
                        FacilityDetailDto jsonDto = jsonDtoFuture.join();

                        CompletableFuture<Void> steps = CompletableFuture.supplyAsync(() -> {
                                    if (jsonDto != null) {
                                        return convertToUpdateDto(jsonDto);
                                    } else {
                                        return null;
                                    }
                                }, executorService)
                                .thenAccept(updateDto -> {
                                    if (updateDto != null) {
                                        updateFacilityDetail(updateDto);
                                        updateCount.incrementAndGet();
                                    }
                                })
                                .exceptionally(ex -> {
                                    log.error("진행중에 오류가 발생했습니다. : {}", ex.getMessage());
                                    idQueue.add(id);
                                    return null;
                                });
                        allStepList.add(steps);

                    }
                } catch (InterruptedException e) {
                    log.error("Queue 처리 중 인터럽트 발생: {}", e.getMessage());
                }
                log.info("step allOf 실행 시작");
                CompletableFuture<Void> allStepFuture = CompletableFuture.allOf(allStepList.toArray(new CompletableFuture[0]));
                allStepFuture.join();
            }, executorService);
            futureList.add(future);
        }
        log.info("return 문 작업 시작");
        return CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    log.info("updateSize : {}", updateCount.get());
                    return updateCount.get();
                });
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

        return FacilityDetailUpdateDto.of(dto.getCode(), dto.getParkXpnsYn(), dto.getParkEtc(), treatmentMon, treatmentTue,
                treatmentWed, treatmentThu, treatmentFri, treatmentSat, treatmentSun, receiveWeek, receiveSat,
                lunchWeek, lunchSat, noTreatmentSun, noTreatmentHoliday, dto.getEmyDayYn(), dto.getEmyNgtYn());
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
