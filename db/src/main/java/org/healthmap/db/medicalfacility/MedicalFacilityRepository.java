package org.healthmap.db.medicalfacility;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalFacilityRepository extends JpaRepository<MedicalFacilityEntity, String> {
}
