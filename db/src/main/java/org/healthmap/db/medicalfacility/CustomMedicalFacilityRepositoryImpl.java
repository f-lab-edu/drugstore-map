package org.healthmap.db.medicalfacility;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CustomMedicalFacilityRepositoryImpl implements CustomMedicalFacilityRepository {
    private final EntityManager em;

    @Override
    public void customSaveAll(List<MedicalFacilityEntity> entityList) {
        for (MedicalFacilityEntity entity : entityList) {
            em.persist(entity);
        }
        em.flush();
        em.clear();
    }

    @Override
    public List<Object[]> findNearMedicalFacility(double latitude, double longitude, int range) {
        String nativeQuery = "SELECT m.*, " +
                "ST_Distance_Sphere(ST_GeomFromText(CONCAT('POINT(', ?1, ' ', ?2, ')'), 4326), coordinate) AS distance " +
                "FROM medical_facility m " +
                "WHERE ST_Distance_Sphere(ST_GeomFromText(CONCAT('POINT(', ?1, ' ', ?2, ')'), 4326), coordinate) <= ?3 " +
                "ORDER BY distance limit 30";
        Query query = em.createNativeQuery(nativeQuery, "MedicalFacilityMapping");
        query.setParameter(1, latitude);
        query.setParameter(2, longitude);
        query.setParameter(3, range * 1000);

        List<Object[]> result = query.getResultList();

        return result;
    }
}
