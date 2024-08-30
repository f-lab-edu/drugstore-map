package org.healthmap.openapi.service;

import lombok.RequiredArgsConstructor;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.openapi.api.DrugstoreApi;
import org.healthmap.openapi.api.HospitalApi;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicalFacilityService {
    private final MedicalFacilityRepository medicalFacilityRepository;
    private final HospitalApi hospitalApi;
    private final DrugstoreApi drugstoreApi;

}
