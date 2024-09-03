package org.healthmap.db.medicalfacility;

import java.util.List;

public interface CustomMedicalFacilityRepository {
    void customSaveAll(List<MedicalFacilityEntity> medicalFacilityEntities);
}
