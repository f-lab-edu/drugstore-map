package org.healthmap.db.medicalfacility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;



@Getter
@NoArgsConstructor
@AllArgsConstructor(access= AccessLevel.PROTECTED)
@Entity
@Table(name="medical_facility")
public class MedicalFacilityEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(length = 30)
    private String phoneNumber;

    @Column(length = 30)
    private String type;

    @Column(length = 30)
    private String state;

    @Column(length = 30)
    private String city;

    @Column(length = 30)
    private String town;

    @Column(length = 16)
    private String postNumber;

    private Point coordinate;

    // 사용되지 않을 경우 삭제할 예정
    public static MedicalFacilityEntity of(String id, String name, String address, String phoneNumber, String type, String state, String city, String town, String postNumber, Point coordinate) {
        return new MedicalFacilityEntity(id, name, address, phoneNumber, type, state, city, town, postNumber, coordinate);
    }
}
