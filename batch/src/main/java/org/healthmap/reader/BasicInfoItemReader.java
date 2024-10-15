package org.healthmap.reader;

import lombok.RequiredArgsConstructor;
import org.healthmap.openapi.dto.MedicalFacilityDto;
import org.healthmap.openapi.service.MedicalFacilityApiService;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BasicInfoItemReader {
    private final MedicalFacilityApiService medicalFacilityApiService;

    @Bean
    public ItemReader<MedicalFacilityDto> basicInfoListItemReader() {
        List<MedicalFacilityDto> allList = medicalFacilityApiService.getAllMedicalFacility();
        return new ListItemReader<>(allList);
    }
}
