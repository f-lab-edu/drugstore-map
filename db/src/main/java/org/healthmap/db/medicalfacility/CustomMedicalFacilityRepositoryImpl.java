package org.healthmap.db.medicalfacility;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CustomMedicalFacilityRepositoryImpl implements CustomMedicalFacilityRepository{
    private final EntityManager em;


    @Override
    public void customSaveAll(List<MedicalFacilityEntity> entityList) {
        for(MedicalFacilityEntity entity : entityList) {
            em.persist(entity);
        }
        em.flush();
        em.clear();
    }
}
