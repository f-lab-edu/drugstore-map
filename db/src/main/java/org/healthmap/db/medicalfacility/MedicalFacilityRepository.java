package org.healthmap.db.medicalfacility;

import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalFacilityRepository extends JpaRepository<MedicalFacilityEntity, String>, CustomMedicalFacilityRepository {

    List<MedicalFacilityEntity> findByCoordinateIsNull();

    @Query("SELECT m.id FROM MedicalFacilityEntity m")
    List<String> findAllId();

    @Modifying
    @Transactional
    @Query("UPDATE MedicalFacilityEntity m SET " +
            "m.parking = COALESCE(:parking, m.parking), " +
            "m.parkingEtc = COALESCE(:parkingEtc, m.parkingEtc), " +
            "m.treatmentMon = COALESCE(:treatmentMon, m.treatmentMon), " +
            "m.treatmentTue = COALESCE(:treatmentTue, m.treatmentTue), " +
            "m.treatmentWed = COALESCE(:treatmentWed, m.treatmentWed), " +
            "m.treatmentThu = COALESCE(:treatmentThu, m.treatmentThu), " +
            "m.treatmentFri = COALESCE(:treatmentFri, m.treatmentFri), " +
            "m.treatmentSat = COALESCE(:treatmentSat, m.treatmentSat), " +
            "m.treatmentSun = COALESCE(:treatmentSun, m.treatmentSun), " +
            "m.receiveWeek = COALESCE(:receiveWeek, m.receiveWeek), " +
            "m.receiveSat = COALESCE(:receiveSat, m.receiveSat), " +
            "m.lunchWeek = COALESCE(:lunchWeek, m.lunchWeek), " +
            "m.lunchSat = COALESCE(:lunchSat, m.lunchSat), " +
            "m.noTreatmentSun = COALESCE(:noTreatmentSun, m.noTreatmentSun), " +
            "m.noTreatmentHoliday = COALESCE(:noTreatmentHoliday, m.noTreatmentHoliday), " +
            "m.emergencyDay = COALESCE(:emergencyDay, m.emergencyDay), " +
            "m.emergencyNight = COALESCE(:emergencyNight, m.emergencyNight) " +
            "WHERE m.id = :id")
    void updateDetail(
            @Param("id") String id, @Param("parking") String parking, @Param("parkingEtc") String parkingEtc,
            @Param("treatmentMon") String treatmentMon, @Param("treatmentTue") String treatmentTue, @Param("treatmentWed") String treatmentWed,
            @Param("treatmentThu") String treatmentThu, @Param("treatmentFri") String treatmentFri, @Param("treatmentSat") String treatmentSat,
            @Param("treatmentSun") String treatmentSun, @Param("receiveWeek") String receiveWeek, @Param("receiveSat") String receiveSat,
            @Param("lunchWeek") String lunchWeek, @Param("lunchSat") String lunchSat, @Param("noTreatmentSun") String noTreatmentSun,
            @Param("noTreatmentHoliday") String noTreatmentHoliday, @Param("emergencyDay") String emergencyDay,
            @Param("emergencyNight") String emergencyNight
    );

    @Modifying
    @Query("UPDATE MedicalFacilityEntity m SET " +
            "m.name = COALESCE(:name, m.name), " +
            "m.address = COALESCE(:address, m.address), " +
            "m.phoneNumber = COALESCE(:phoneNumber, m.phoneNumber), " +
            "m.url = COALESCE(:url, m.url), " +
            "m.type = COALESCE(:type, m.type), " +
            "m.state = COALESCE(:state, m.state), " +
            "m.city = COALESCE(:city, m.city), " +
            "m.town = COALESCE(:town, m.town), " +
            "m.postNumber = COALESCE(:postNumber, m.postNumber), " +
            "m.coordinate = COALESCE(:coordinate, m.coordinate) " +
            "WHERE m.id = :id")
    void updateFacilityInfo(
            @Param("id") String id, @Param("name") String name, @Param("address") String address, @Param("phoneNumber") String phoneNumber,
            @Param("url") String url, @Param("type") String type, @Param("state") String state, @Param("city") String city,
            @Param("town") String town, @Param("postNumber") String postNumber, @Param("coordinate") Point coordinate
    );

    @Modifying
    @Query("UPDATE MedicalFacilityEntity m SET " +
            "m.coordinate = :coordinate " +
            "WHERE m.id = :id")
    void updateNullCoordinate(@Param("coordinate") Point coordinate, @Param("id") String id);
}
