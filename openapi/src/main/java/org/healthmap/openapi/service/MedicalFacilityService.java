package org.healthmap.openapi.service;

import lombok.RequiredArgsConstructor;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.openapi.api.MedicalFacilityApi;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicalFacilityService {
    private final MedicalFacilityRepository medicalFacilityRepository;
    private final MedicalFacilityApi hospitalApi;

/*    public List<MedicalFacilityDto> getAllMedicalFacilities() {
        List<MedicalFacilityDto> hospitalInfo = hospitalApi.getHospitalInfo(0);
    }*/

}
