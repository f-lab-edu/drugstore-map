package org.healthmap.db.medicalfacility;

import java.util.List;

public interface CustomMedicalFacilityRepository {
    List<Object[]> findNearMedicalFacility(double latitude, double longitude, int range);
}
