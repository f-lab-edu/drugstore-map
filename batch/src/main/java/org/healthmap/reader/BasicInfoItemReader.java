package org.healthmap.reader;

import lombok.RequiredArgsConstructor;
import org.healthmap.dto.BasicInfoDto;
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
    public ItemReader<BasicInfoDto> basicInfoListItemReader() {
        List<BasicInfoDto> allList = medicalFacilityApiService.getAllBasicInfo();
        return new ListItemReader<>(allList);
    }
}
