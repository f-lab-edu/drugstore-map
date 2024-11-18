package org.healthmap.app.controller;

import lombok.RequiredArgsConstructor;
import org.healthmap.app.dto.HealthMapRequestDto;
import org.healthmap.app.dto.HealthMapResponseDto;
import org.healthmap.app.service.HealthMapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/healthmap")
public class HealthMapController {
    private final HealthMapService healthMapService;

    @GetMapping
    public HealthMapRequestDto findAllDrugstore() {
        return null;
    }

    @GetMapping("/around")
    public ResponseEntity<List<HealthMapResponseDto>> findFacilityAround(
            @ModelAttribute HealthMapRequestDto requestDto
    ) {
        List<HealthMapResponseDto> nearByMedicalFacility = healthMapService.getNearByMedicalFacilityMongo(requestDto);

        return ResponseEntity.ok(nearByMedicalFacility);
    }

}
