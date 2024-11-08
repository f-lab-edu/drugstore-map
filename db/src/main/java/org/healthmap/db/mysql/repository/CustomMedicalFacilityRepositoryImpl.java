package org.healthmap.db.mysql.repository;

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
    public List<Object[]> findNearMedicalFacility(double latitude, double longitude, int range) {
        String nativeQuery = "SELECT m.id, m.name, m.address, m.phone_number, m.url, m.type, m.state, m.city, " +
                "m.town, m.post_number, m.coordinate, m.parking, m.parking_etc, m.treatment_mon, m.treatment_tue, " +
                "m.treatment_wed, m.treatment_thu, m.treatment_fri, m.treatment_sat, m.treatment_sun, m.receive_week, " +
                "m.receive_sat, m.lunch_week, m.lunch_sat, m.no_treatment_sun, m.no_treatment_holiday, m.emergency_day, " +
                "m.emergency_night, " +
                "ST_Distance_Sphere(ST_GeomFromText(CONCAT('POINT(', ?1, ' ', ?2, ')'), 4326), coordinate) AS distance " +
                "FROM medical_facility m " +
                "WHERE ST_Distance_Sphere(ST_GeomFromText(CONCAT('POINT(', ?1, ' ', ?2, ')'), 4326), coordinate) <= ?3 " +
                "ORDER BY distance limit 30";
        Query query = em.createNativeQuery(nativeQuery);
        query.setParameter(1, latitude);
        query.setParameter(2, longitude);
        query.setParameter(3, range * 1000);

        List<Object[]> result = query.getResultList();

        return result;
    }
}
